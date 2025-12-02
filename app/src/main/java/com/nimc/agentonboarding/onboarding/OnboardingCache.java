package com.nimc.agentonboarding.onboarding;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import org.json.JSONObject;

public class OnboardingCache {
    private static final String PREFS = "nimc_onboarding_cache";
    private static final String KEY_DATA = "data";

    private final SharedPreferences prefs;

    public OnboardingCache(Context ctx) {
        SharedPreferences p;
        try {
            String mk = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            p = EncryptedSharedPreferences.create(
                    PREFS, mk, ctx,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        }
        this.prefs = p;
    }

    public void save(OnboardingViewModel m) {
        try {
            JSONObject o = new JSONObject();
            o.put("nin", m.nin);
            o.put("firstName", m.firstName);
            o.put("lastName", m.lastName);
            o.put("gender", m.gender);
            o.put("email", m.email);
            o.put("dob", m.dob);
            o.put("fepName", m.fepName);
            o.put("fepCode", m.fepCode);
            o.put("state", m.state);
            o.put("face", m.faceImageBase64);
            prefs.edit().putString(KEY_DATA, o.toString()).apply();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void load(OnboardingViewModel m) {
        String json = prefs.getString(KEY_DATA, null);
        if (json == null) return;
        try {
            JSONObject o = new JSONObject(json);
            m.nin = o.optString("nin", null);
            m.firstName = o.optString("firstName", null);
            m.lastName = o.optString("lastName", null);
            m.gender = o.optString("gender", null);
            m.email = o.optString("email", null);
            m.dob = o.optString("dob", null);
            m.fepName = o.optString("fepName", null);
            m.fepCode = o.optString("fepCode", null);
            m.state = o.optString("state", null);
            m.faceImageBase64 = o.optString("face", null);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public boolean hasPartial() {
        return prefs.contains(KEY_DATA);
    }

    public void clear() {
        prefs.edit().remove(KEY_DATA).apply();
    }
}
