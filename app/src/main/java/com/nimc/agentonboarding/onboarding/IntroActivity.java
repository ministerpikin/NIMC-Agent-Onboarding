package com.nimc.agentonboarding.onboarding;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.nimc.agentonboarding.R;

public class IntroActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_intro);

        Button btnContinue = findViewById(R.id.btnContinue);
        Button btnNotInterested = findViewById(R.id.btnNotInterested);

        btnContinue.setOnClickListener(v ->
                startActivity(new Intent(this, DataEntryActivity.class))
        );

        btnNotInterested.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Exit")
                        .setMessage("Are you sure you want to exit the application?")
                        .setPositiveButton("Yes", (d, i) -> finish())
                        .setNegativeButton("No", null)
                        .show()
        );
    }
}
