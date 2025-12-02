package com.nimc.agentonboarding;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import com.nimc.agentonboarding.config.ConfigManager;

public class ConfigActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_config);

        Switch sw = findViewById(R.id.switchOfflineFirst);
        Button btnSave = findViewById(R.id.btnSaveConfig);
        ConfigManager cm = new ConfigManager(this);

        sw.setChecked(cm.isOfflineFirst());
        btnSave.setOnClickListener(v -> {
            cm.setOfflineFirst(sw.isChecked());
            finish();
        });
    }
}
