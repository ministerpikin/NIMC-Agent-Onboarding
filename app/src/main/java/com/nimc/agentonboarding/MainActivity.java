package com.nimc.agentonboarding;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;


import com.nimc.agentonboarding.onboarding.IntroActivity;


public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // simple menu with three buttons
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        Button btnOnboard  = new Button(this);
        btnOnboard .setText(R.string.start_onboarding_text);
        btnOnboard .setOnClickListener(v -> startActivity(new Intent(this, IntroActivity.class)));
        //setContentView(btnOnboard );

        Button btnSupervisor = new Button(this);
        btnSupervisor.setText("Supervisor Login");
        btnSupervisor.setOnClickListener(v -> startActivity(
                new Intent(this, com.nimc.agentonboarding.supervisor.SupervisorLoginActivity.class)
        ));

        Button btnBiometric = new Button(this);
        btnBiometric.setText("Login with Fingerprint");
        btnBiometric.setOnClickListener(v -> startBiometricLogin());

        root.addView(btnOnboard);
        root.addView(btnBiometric);
        root.addView(btnSupervisor);

        setContentView(root);

    }

    private void startBiometricLogin() {
        BiometricManager bm = BiometricManager.from(this);
        if (bm.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "Biometric not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt prompt = new BiometricPrompt(this,
                executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        // In a real app, you’d verify there’s a verified agent in DB & tokens set
                        startActivity(new Intent(MainActivity.this, AgentProfileActivity.class));
                    }
                });

        BiometricPrompt.PromptInfo info = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Agent Login")
                .setSubtitle("Use your fingerprint to view your profile")
                .setNegativeButtonText("Cancel")
                .build();

        prompt.authenticate(info);
    }
}