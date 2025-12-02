package com.nimc.agentonboarding.onboarding;

import androidx.lifecycle.ViewModel;

public class OnboardingViewModel extends ViewModel {
    public String nin, firstName, lastName, gender, email, dob, fepName, fepCode, state;
    public String faceImageBase64;

    public boolean isDataComplete() {
        return nin != null && faceImageBase64 != null;
    }
}
