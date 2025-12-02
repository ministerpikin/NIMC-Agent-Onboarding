package com.nimc.agentonboarding;

import com.nimc.agentonboarding.R;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_settings);

        Button btnLight = findViewById(R.id.btnLight);
        Button btnDark = findViewById(R.id.btnDark);
        Button btnSystem = findViewById(R.id.btnSystem);

        btnLight.setOnClickListener(v -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO));
        btnDark.setOnClickListener(v -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES));
        btnSystem.setOnClickListener(v -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM));
    }
}
