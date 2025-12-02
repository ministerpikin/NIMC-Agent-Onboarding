package com.nimc.agentonboarding.supervisor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nimc.agentonboarding.QRScanActivity;
import com.nimc.agentonboarding.R;
import com.nimc.agentonboarding.api.ApiClient;
import com.nimc.agentonboarding.auth.TokenManager;

import retrofit2.Retrofit;

public class SupervisorLoginActivity extends AppCompatActivity {

    private EditText etUser, etPass;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_supervisor_login);

        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        Button btnLogin = findViewById(R.id.btnLoginSupervisor);

        btnLogin.setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        String u = etUser.getText().toString().trim();
        String p = etPass.getText().toString().trim();
        if (u.isEmpty() || p.isEmpty()) {
            Toast.makeText(this, "Username and password required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mocked login; in production call /auth/login-supervisor
        Retrofit r = ApiClient.getClient(this, "http://10.0.2.2:3000/");
        // You can add a SupervisorAuthApi if you want server-side validation.
        // For now, assume login success:
        new TokenManager(this).saveTokens("mock_access_token", "mock_refresh_token");
        startActivity(new Intent(this, QRScanActivity.class));
        finish();
    }
}
