package com.nimc.agentonboarding.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nimc.agentonboarding.AgentTempHolder;
import com.nimc.agentonboarding.R;

public class DataEntryActivity extends AppCompatActivity {
    private EditText etNin, etFirstName, etLastName, etGender, etEmail, etDob, etFepName, etFepCode, etState;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_data_entry);

        etNin = findViewById(R.id.etNin);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etGender = findViewById(R.id.etGender);
        etEmail = findViewById(R.id.etEmail);
        etDob = findViewById(R.id.etDob);
        etFepName = findViewById(R.id.etFepName);
        etFepCode = findViewById(R.id.etFepCode);
        etState = findViewById(R.id.etState);

        Button btnBack = findViewById(R.id.btnBack);
        Button btnNext = findViewById(R.id.btnNext);

        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            if (isAnyEmpty()) {
                Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_LONG).show();
                return;
            }
            AgentTempHolder.nin = etNin.getText().toString().trim();
            AgentTempHolder.firstName = etFirstName.getText().toString().trim();
            AgentTempHolder.lastName = etLastName.getText().toString().trim();
            AgentTempHolder.gender = etGender.getText().toString().trim();
            AgentTempHolder.email = etEmail.getText().toString().trim();
            AgentTempHolder.dob = etDob.getText().toString().trim();
            AgentTempHolder.fepName = etFepName.getText().toString().trim();
            AgentTempHolder.fepCode = etFepCode.getText().toString().trim();
            AgentTempHolder.state = etState.getText().toString().trim();

            if (!isNinValid(AgentTempHolder.nin)) {
                etNin.setError("Invalid NIN: must be 11 digits with no leading zero");
                etNin.requestFocus();
                return;
            }

            startActivity(new Intent(this, FaceCaptureActivity.class));
        });
    }

    private boolean isAnyEmpty() {
        return etNin.getText().toString().trim().isEmpty() ||
                etFirstName.getText().toString().trim().isEmpty() ||
                etLastName.getText().toString().trim().isEmpty() ||
                etGender.getText().toString().trim().isEmpty() ||
                etEmail.getText().toString().trim().isEmpty() ||
                etDob.getText().toString().trim().isEmpty() ||
                etFepName.getText().toString().trim().isEmpty() ||
                etFepCode.getText().toString().trim().isEmpty() ||
                etState.getText().toString().trim().isEmpty();
    }

    private boolean isNinValid(String nin) {
        return nin.matches("^[1-9][0-9]{10}$");
    }
}
