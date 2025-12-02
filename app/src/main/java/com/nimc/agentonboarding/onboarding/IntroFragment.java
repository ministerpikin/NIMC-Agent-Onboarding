package com.nimc.agentonboarding.onboarding;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nimc.agentonboarding.R;
import com.nimc.agentonboarding.data.AuditLogger;

public class IntroFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_intro, container, false);

        Button btnContinue = v.findViewById(R.id.btnContinue);
        Button btnExit = v.findViewById(R.id.btnNotInterested);

        btnContinue.setOnClickListener(v2 ->
                ((OnboardingActivity) requireActivity()).goToNextPage()
        );

        btnExit.setOnClickListener(v2 ->
                new AlertDialog.Builder(requireContext())
                        .setTitle("Exit")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", (d, i) -> requireActivity().finish())
                        .setNegativeButton("No", null)
                        .show()
        );

        AuditLogger.log(requireContext(), "ONBOARDING_START", "Intro screen visited");

        return v;
    }
}
