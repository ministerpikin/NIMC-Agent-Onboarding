package com.nimc.agentonboarding.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.nimc.agentonboarding.AgentTempHolder;
import com.nimc.agentonboarding.R;

public class DataEntryFragment extends Fragment {

    private EditText etNin, etFirst, etLast, etGender, etEmail, etDob, etFepName, etFepCode, etState;

    private OnboardingViewModel model;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup parent,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_data_entry, parent, false);

        //model = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);

        model = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);
        new OnboardingCache(requireContext()).load(model);

        if (model.nin != null) etNin.setText(model.nin);
        if (model.firstName != null) etFirst.setText(model.firstName);
        if (model.lastName != null) etNin.setText(model.lastName);
        if (model.gender != null) etFirst.setText(model.gender);
        if (model.email != null) etNin.setText(model.email);
        if (model.dob != null) etFirst.setText(model.dob);
        if (model.fepName != null) etNin.setText(model.fepName);
        if (model.fepCode != null) etFirst.setText(model.fepCode);
        if (model.state != null) etNin.setText(model.state);


        etNin = v.findViewById(R.id.etNin);
        etFirst = v.findViewById(R.id.etFirstName);
        etLast = v.findViewById(R.id.etLastName);
        etGender = v.findViewById(R.id.etGender);
        etEmail = v.findViewById(R.id.etEmail);
        etDob = v.findViewById(R.id.etDob);
        etFepName = v.findViewById(R.id.etFepName);
        etFepCode = v.findViewById(R.id.etFepCode);
        etState = v.findViewById(R.id.etState);

        Button btnBack = v.findViewById(R.id.btnBack);
        Button btnNext = v.findViewById(R.id.btnNext);

        btnBack.setOnClickListener(v2 -> ((OnboardingActivity) requireActivity()).goToPreviousPage());

        btnNext.setOnClickListener(v2 -> {
            if (!validate()) return;


            AgentTempHolder.nin = etNin.getText().toString().trim();
            AgentTempHolder.firstName = etFirst.getText().toString().trim();
            AgentTempHolder.lastName = etLast.getText().toString().trim();
            AgentTempHolder.gender = etGender.getText().toString().trim();
            AgentTempHolder.email = etEmail.getText().toString().trim();
            AgentTempHolder.dob = etDob.getText().toString().trim();
            AgentTempHolder.fepName = etFepName.getText().toString().trim();
            AgentTempHolder.fepCode = etFepCode.getText().toString().trim();
            AgentTempHolder.state = etState.getText().toString().trim();


            model.nin = etNin.getText().toString().trim();
            model.firstName = etFirst.getText().toString().trim();
            model.lastName = etLast.getText().toString().trim();
            model.gender = etGender.getText().toString().trim();
            model.email = etEmail.getText().toString().trim();
            model.dob = etDob.getText().toString().trim();
            model.fepName = etFepName.getText().toString().trim();
            model.fepCode = etFepCode.getText().toString().trim();
            model.state = etState.getText().toString().trim();

            OnboardingCache cache = new OnboardingCache(requireContext());
            cache.save(model);

            ((OnboardingActivity) requireActivity()).goToNextPage();
        });

        return v;
    }

    private boolean validate() {
        String nin = etNin.getText().toString().trim();
        if (!nin.matches("^[1-9][0-9]{10}$")) {
            etNin.setError("Invalid NIN format");
            return false;
        }
        if (etFirst.getText().toString().trim().isEmpty()) return error(etFirst);
        if (etLast.getText().toString().trim().isEmpty()) return error(etLast);
        if (etGender.getText().toString().trim().isEmpty()) return error(etGender);
        if (etEmail.getText().toString().trim().isEmpty()) return error(etEmail);
        if (etDob.getText().toString().trim().isEmpty()) return error(etDob);
        if (etFepName.getText().toString().trim().isEmpty()) return error(etFepName);
        if (etFepCode.getText().toString().trim().isEmpty()) return error(etFepCode);
        if (etState.getText().toString().trim().isEmpty()) return error(etState);

        return true;
    }

    private boolean error(EditText e) {
        e.setError("Required");
        return false;
    }
}
