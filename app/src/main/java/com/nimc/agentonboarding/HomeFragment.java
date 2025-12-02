package com.nimc.agentonboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.nimc.agentonboarding.onboarding.OnboardingActivity;
import com.nimc.agentonboarding.onboarding.OnboardingCache;
import com.nimc.agentonboarding.onboarding.OnboardingViewModel;

import java.util.concurrent.Executor;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inf.inflate(R.layout.fragment_home, container, false);


        OnboardingViewModel model = new ViewModelProvider(requireActivity())
                .get(OnboardingViewModel.class);
        OnboardingCache cache = new OnboardingCache(requireContext());
        cache.load(model);

        if (cache.hasPartial() && !model.isDataComplete()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Resume onboarding?")
                    .setMessage("We found a previous onboarding session. Do you want to continue where you left off?")
                    .setPositiveButton("Resume", (d, which) -> {
                        // Decide which step to start at
                        Intent i = new Intent(requireContext(), OnboardingActivity.class);
                        int startPage = (model.nin == null) ? 0 :
                                (model.faceImageBase64 == null ? 2 : 0);
                        i.putExtra("startPage", startPage);
                        startActivity(i);
                    })
                    .setNegativeButton("Start new", (d, which) -> {
                        cache.clear();
                    })
                    .show();
        }


        Button btnOnboard = v.findViewById(R.id.btnOnboard);
        Button btnBiometric = v.findViewById(R.id.btnBiometric);
        Button btnSupervisor = v.findViewById(R.id.btnSupervisor);

        btnOnboard.setOnClickListener(view ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_home_to_onboarding));

        btnSupervisor.setOnClickListener(view ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_home_to_supervisorLogin));

        btnBiometric.setOnClickListener(view -> startBiometricLogin());

        return v;
    }

    private void startBiometricLogin() {
        BiometricManager bm = BiometricManager.from(requireContext());
        if (bm.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(requireContext(), "Biometric not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Executor executor = ContextCompat.getMainExecutor(requireContext());
        BiometricPrompt prompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        startActivity(new Intent(requireContext(), AgentProfileActivity.class));
                    }
                });

        BiometricPrompt.PromptInfo info = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Agent Login")
                .setSubtitle("Use fingerprint to view your profile")
                .setNegativeButtonText("Cancel")
                .build();

        prompt.authenticate(info);
    }
}
