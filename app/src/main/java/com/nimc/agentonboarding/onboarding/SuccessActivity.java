package com.nimc.agentonboarding.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.BackoffPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.nimc.agentonboarding.AgentProfileActivity;
import com.nimc.agentonboarding.R;
import com.nimc.agentonboarding.worker.VerificationWorker;

import java.util.concurrent.TimeUnit;

public class SuccessActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_success);

        // Trigger verification worker once
        //OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(VerificationWorker.class).build();
        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(VerificationWorker.class)
                .setBackoffCriteria(
                        BackoffPolicy.EXPONENTIAL,
                        30, TimeUnit.SECONDS
                ).build();
        WorkManager.getInstance(this).enqueue(req);

        Button btnView = findViewById(R.id.btnViewProfile);
        btnView.setOnClickListener(v ->
                startActivity(new Intent(this, AgentProfileActivity.class))
        );
    }
}
