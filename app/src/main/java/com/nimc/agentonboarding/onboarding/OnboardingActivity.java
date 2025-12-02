package com.nimc.agentonboarding.onboarding;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nimc.agentonboarding.R;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabDots;
    private TextView tvProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        tabDots = findViewById(R.id.tabDots);
        tvProgress = findViewById(R.id.tvProgress);

        OnboardingPagerAdapter adapter = new OnboardingPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false); // controlled navigation only

        viewPager.setPageTransformer(new ZoomOutPageTransformer());

        int startPage = getIntent().getIntExtra("startPage", 0);
        viewPager.setCurrentItem(startPage, false);
        tvProgress.setText("Step " + (startPage + 1) + " of 3");

        // Progress text listener
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) {
                tvProgress.setText("Step " + (position + 1) + " of 3");
            }
        });

        new TabLayoutMediator(tabDots, viewPager,
                (tab, position) -> { /* Dots only */ }).attach();
    }

    public void goToNextPage() {
        int next = viewPager.getCurrentItem() + 1;
        if (next < 3) viewPager.setCurrentItem(next, true);
    }

    public void goToPreviousPage() {
        int prev = viewPager.getCurrentItem() - 1;
        if (prev >= 0) viewPager.setCurrentItem(prev, true);
    }
}
