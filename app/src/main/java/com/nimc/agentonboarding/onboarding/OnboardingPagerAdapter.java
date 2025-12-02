package com.nimc.agentonboarding.onboarding;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OnboardingPagerAdapter extends FragmentStateAdapter {

    public OnboardingPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new IntroFragment();
            case 1: return new DataEntryFragment();
            case 2: return new FaceCaptureFragment();
            default: return new IntroFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Intro, DataEntry, FaceCapture
    }
}
