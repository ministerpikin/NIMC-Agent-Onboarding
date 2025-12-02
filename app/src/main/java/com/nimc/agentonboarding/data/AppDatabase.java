package com.nimc.agentonboarding.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import net.sqlcipher.DatabaseErrorHandler;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

import com.nimc.agentonboarding.config.ConfigManager; // or a dedicated key manager

//@Database(entities = {AgentEntity.class}, version = 1, exportSchema = false)
@Database(entities = {
        AgentEntity.class,
        AuditLogEntity.class   // ✔ REQUIRED
}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AgentDao agentDao();
    public abstract AuditLogDao auditLogDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context ctx) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // Ensure SQLCipher libs are loaded
                    SQLiteDatabase.loadLibs(ctx);

                    // Get or create passphrase from EncryptedSharedPreferences
                    String pass = new DbKeyProvider(ctx).getOrCreateKey();
                    byte[] passphrase = pass.getBytes();
                    SupportFactory factory = new SupportFactory(passphrase);

                    /*INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(),
                                    AppDatabase.class, "nimc_agent_db_v7_encrypted")
                            .openHelperFactory(factory)
                            .build();*/

                    INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(),
                                    AppDatabase.class, "nimc_agent_db_v7_encrypted.db")
                            .fallbackToDestructiveMigration()
                            .openHelperFactory(factory)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
