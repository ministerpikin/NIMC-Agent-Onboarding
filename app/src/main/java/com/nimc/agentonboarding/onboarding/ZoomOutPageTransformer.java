package com.nimc.agentonboarding.onboarding;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
    private static final float MIN_SCALE = .85f;
    private static final float MIN_ALPHA = .5f;

    @Override
    public void transformPage(@NonNull View page, float pos) {
        if (pos < -1) {
            page.setAlpha(0f);
        } else if (pos <= 1) {
            float scale = Math.max(MIN_SCALE, 1 - Math.abs(pos));
            page.setScaleX(scale);
            page.setScaleY(scale);
            page.setAlpha(MIN_ALPHA + (scale - MIN_SCALE)/(1 - MIN_SCALE)*(1 - MIN_ALPHA));
        } else {
            page.setAlpha(0f);
        }
    }
}
