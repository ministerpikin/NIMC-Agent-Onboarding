package com.nimc.agentonboarding.config;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

public class ConfigManager {
    private static final String PREFS = "nimc_config_prefs_v7";
    private static final String KEY_OFFLINE_FIRST = "offline_first";
    private SharedPreferences prefs;

    public ConfigManager(Context ctx) {
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

    public boolean isOfflineFirst() {
        return prefs.getBoolean(KEY_OFFLINE_FIRST, true);
    }
    public void setOfflineFirst(boolean v) {
        prefs.edit().putBoolean(KEY_OFFLINE_FIRST, v).apply();
    }
}
