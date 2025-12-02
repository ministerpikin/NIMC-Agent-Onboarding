package com.nimc.agentonboarding.onboarding;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.nimc.agentonboarding.AgentTempHolder;
import com.nimc.agentonboarding.R;
import com.nimc.agentonboarding.data.AgentEntity;
import com.nimc.agentonboarding.data.AppDatabase;
import com.nimc.agentonboarding.data.AuditLogger;
import com.nimc.agentonboarding.onboarding.SuccessActivity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FaceCaptureFragment extends Fragment {

    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;

    private FaceDetector detector;
    private int blinkCount = 0;
    private boolean eyesOpenPrev = true;
    private boolean turned = false;
    private boolean smiled = false;
    private float lastYaw = 0f;

    private ActivityResultLauncher<String> permissionLauncher;

    private OnboardingViewModel model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inf.inflate(R.layout.fragment_face_capture, container, false);

        model = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);

        previewView = v.findViewById(R.id.previewView);
        Button btnCapture = v.findViewById(R.id.btnCapture);

        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build();

        detector = FaceDetection.getClient(options);
        cameraExecutor = Executors.newSingleThreadExecutor();

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) startCamera();
                    else Toast.makeText(getContext(), "Camera permission required", Toast.LENGTH_SHORT).show();
                });

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        } else startCamera();

        btnCapture.setOnClickListener(v2 -> tryCapture());

        return v;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> f = ProcessCameraProvider.getInstance(requireContext());
        f.addListener(() -> {
            try {
                ProcessCameraProvider provider = f.get();
                Preview preview = new Preview.Builder().build();
                imageCapture = new ImageCapture.Builder().build();

                ImageAnalysis analysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                analysis.setAnalyzer(cameraExecutor, this::analyze);

                provider.unbindAll();
                provider.bindToLifecycle(this, CameraSelector.DEFAULT_FRONT_CAMERA,
                        preview, analysis, imageCapture);

                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            } catch (Exception e) { e.printStackTrace(); }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void analyze(ImageProxy img) {
        try {
            if (img.getImage() == null) { img.close(); return; }
            InputImage im = InputImage.fromMediaImage(img.getImage(), img.getImageInfo().getRotationDegrees());
            detector.process(im)
                    .addOnSuccessListener(faces -> processFaces(faces, img))
                    .addOnFailureListener(e -> img.close());
        } catch (Exception e) { img.close(); }
    }

    private void processFaces(List<Face> faces, ImageProxy img) {
        if (faces.isEmpty()) { img.close(); return; }
        Face f = faces.get(0);

        Float left = f.getLeftEyeOpenProbability();
        Float right = f.getRightEyeOpenProbability();
        Float smileProb = f.getSmilingProbability();

        if (left != null && right != null) {
            boolean eyesOpen = left > 0.6f && right > 0.6f;
            if (!eyesOpen && eyesOpenPrev) blinkCount++;
            eyesOpenPrev = eyesOpen;
        }

        float yaw = f.getHeadEulerAngleY();
        if (Math.abs(yaw - lastYaw) > 15) turned = true;
        lastYaw = yaw;

        if (smileProb != null && smileProb > 0.6f) smiled = true;

        img.close();
    }

    private void tryCapture() {
        if (!(blinkCount >= 1 && turned && smiled)) {
            Toast.makeText(getContext(), "Please blink, smile & turn your head first", Toast.LENGTH_LONG).show();
            return;
        }

        imageCapture.takePicture(ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy img) {
                        AuditLogger.log(requireContext(), "LIVENESS_PASSED", "Blink+Turn+Smile OK");

                        Bitmap bmp = previewView.getBitmap();
                        if (bmp != null) {
                            Bitmap scaled = Bitmap.createScaledBitmap(bmp, 512, 512, true);
                            saveFace(scaled);
                        }
                        img.close();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exc) {
                        Toast.makeText(getContext(), "Capture failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveFace(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        String base64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);

        //AuditLogger.log(requireContext(), "LIVENESS_PASSED", "Blink+Turn+Smile OK");

        new Thread(() -> {
            AgentEntity a = new AgentEntity();
            a.nin = AgentTempHolder.nin;
            a.firstName = AgentTempHolder.firstName;
            a.lastName = AgentTempHolder.lastName;
            a.gender = AgentTempHolder.gender;
            a.email = AgentTempHolder.email;
            a.dob = AgentTempHolder.dob;
            a.fepName = AgentTempHolder.fepName;
            a.fepCode = AgentTempHolder.fepCode;
            a.state = AgentTempHolder.state;
            a.imageBase64 = base64;
            a.status = "PENDING";

            //model.faceImageBase64 = base64;
            model.faceImageBase64 = base64;
            new OnboardingCache(requireContext()).save(model);

            AppDatabase db = AppDatabase.getDatabase(requireContext());
            db.agentDao().insert(a);

            AuditLogger.log(requireContext(), "FACE_SAVED", "Captured face base64");

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Saved locally!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireContext(), SuccessActivity.class));
            });
        }).start();
    }
}
