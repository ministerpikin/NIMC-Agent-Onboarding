package com.nimc.agentonboarding.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.SecureRandom;
import java.util.Base64;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

public class DbKeyProvider {
    private static final String PREFS = "nimc_db_key_prefs";
    private static final String KEY_NAME = "db_key";
    private final SharedPreferences prefs;

    public DbKeyProvider(Context ctx) {
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

    public String getOrCreateKey() {
        String existing = prefs.getString(KEY_NAME, null);
        if (existing != null) return existing;
        byte[] raw = new byte[32];
        new SecureRandom().nextBytes(raw);
        String base64 = android.util.Base64.encodeToString(raw, android.util.Base64.NO_WRAP);
        prefs.edit().putString(KEY_NAME, base64).apply();
        return base64;
    }
}
