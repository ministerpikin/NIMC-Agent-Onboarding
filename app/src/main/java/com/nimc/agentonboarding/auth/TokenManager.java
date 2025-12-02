package com.nimc.agentonboarding.auth;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

public class TokenManager {
    private static final String PREFS = "nimc_tokens_v1";
    private static final String KEY_ACCESS = "access";
    private static final String KEY_REFRESH = "refresh";
    private SharedPreferences prefs;

    public TokenManager(Context ctx) {
        try {
            String mk = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            prefs = EncryptedSharedPreferences.create(
                    PREFS, mk, ctx,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        }
    }

    public void saveTokens(String access, String refresh) {
        prefs.edit().putString(KEY_ACCESS, access).putString(KEY_REFRESH, refresh).apply();
    }
    public String getAccessToken() { return prefs.getString(KEY_ACCESS, null); }
    public String getRefreshToken() { return prefs.getString(KEY_REFRESH, null); }
    public void clear() { prefs.edit().clear().apply(); }
}
