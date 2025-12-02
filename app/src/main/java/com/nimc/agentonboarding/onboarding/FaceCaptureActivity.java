package com.nimc.agentonboarding.onboarding;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

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

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FaceCaptureActivity extends AppCompatActivity {
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private FaceDetector detector;
    private int blinkCount = 0;
    private boolean eyesPreviouslyOpen = true;
    private float lastYaw = 0f;
    private boolean hasTurned = false;
    private boolean sawSmile = false;

    private int captureAttempts = 0;
    private static final int MAX_ATTEMPTS = 5;


    private ActivityResultLauncher<String> permissionLauncher;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_face_capture);

        previewView = findViewById(R.id.previewView);
        Button btnCapture = findViewById(R.id.btnCapture);

        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                // .setTrackingEnabled(true)
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build();
        detector = FaceDetection.getClient(options);

        cameraExecutor = Executors.newSingleThreadExecutor();

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) startCamera();
                    else {
                        Toast.makeText(this, "Camera permission required", Toast.LENGTH_LONG).show();
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }
                }
        );

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCamera();
        }

        btnCapture.setOnClickListener(v -> {

            if (captureAttempts >= MAX_ATTEMPTS) {
                Toast.makeText(this, "Maximum capture attempts reached. Contact supervisor.", Toast.LENGTH_LONG).show();
                return;
            }
            if (blinkCount >= 1 && hasTurned && sawSmile) {
                captureAttempts++;
                takePhotoAndSave();
            } else {
                Toast.makeText(this, "Please blink, smile and turn your head first.", Toast.LENGTH_LONG).show();
                AuditLogger.log(this, "FACE_CAPTURE_FAIL", "Agent NIN=" + AgentTempHolder.nin);
            }
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                imageCapture = new ImageCapture.Builder().build();

                ImageAnalysis analysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                analysis.setAnalyzer(cameraExecutor, this::analyzeImage);

                CameraSelector selector = CameraSelector.DEFAULT_FRONT_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, selector, preview, analysis, imageCapture);
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void analyzeImage(ImageProxy imageProxy) {
        try {
            if (imageProxy.getImage() == null) { imageProxy.close(); return; }
            InputImage img = InputImage.fromMediaImage(
                    imageProxy.getImage(),
                    imageProxy.getImageInfo().getRotationDegrees()
            );
            detector.process(img)
                    .addOnSuccessListener(faces -> handleFaces(faces, imageProxy))
                    .addOnFailureListener(e -> imageProxy.close());
        } catch (Exception e) {
            imageProxy.close();
        }
    }

    private void handleFaces(List<Face> faces, ImageProxy proxy) {
        if (faces == null || faces.isEmpty()) { proxy.close(); return; }
        Face f = faces.get(0);

        Float left = f.getLeftEyeOpenProbability();
        Float right = f.getRightEyeOpenProbability();
        Float smile = f.getSmilingProbability();
        float yaw = f.getHeadEulerAngleY();

        if (Math.abs(yaw - lastYaw) > 15f) hasTurned = true;
        lastYaw = yaw;

        if (left != null && right != null) {
            boolean eyesOpen = left > 0.6f && right > 0.6f;
            if (!eyesOpen && eyesPreviouslyOpen) {
                blinkCount++;
            }
            eyesPreviouslyOpen = eyesOpen;
        }
        if (smile != null && smile > 0.6f) {
            sawSmile = true;
        }
        proxy.close();
    }

    private void takePhotoAndSave() {
        if (imageCapture == null) {
            Toast.makeText(this, "Camera not ready", Toast.LENGTH_SHORT).show();
            return;
        }
        imageCapture.takePicture(
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        Bitmap bmp = previewView.getBitmap();
                        if (bmp == null) {
                            bmp = Bitmap.createBitmap(320, 240, Bitmap.Config.ARGB_8888);
                        }
                        Matrix m = new Matrix();
                        Bitmap rotated = Bitmap.createBitmap(bmp, 0,0, bmp.getWidth(), bmp.getHeight(), m, true);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        rotated.compress(Bitmap.CompressFormat.JPEG, 85, baos);
                        byte[] jpeg = baos.toByteArray();
                        String base64 = Base64.encodeToString(jpeg, Base64.NO_WRAP);

                        saveAgent(base64);
                        image.close();


                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        exception.printStackTrace();
                        Toast.makeText(FaceCaptureActivity.this,
                                "Capture error: " + exception.getMessage(),
                                Toast.LENGTH_LONG).show();

                    }
                }
        );
    }

    private void saveAgent(String imageBase64) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(FaceCaptureActivity.this);
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
            a.imageBase64 = imageBase64;
            a.status = "PENDING";
            db.agentDao().insert(a);

            AuditLogger.log(this, "FACE_CAPTURE_SUCCESS", "Agent NIN=" + AgentTempHolder.nin);

            runOnUiThread(() -> {
                Toast.makeText(this, "Saved locally. Verification will run in background.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, SuccessActivity.class));
                setResult(Activity.RESULT_OK);
                finish();
            });
        }).start();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (detector != null) detector.close();
        if (cameraExecutor != null) cameraExecutor.shutdown();
    }
}
