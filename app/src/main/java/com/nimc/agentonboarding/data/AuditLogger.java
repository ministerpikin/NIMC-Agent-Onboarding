package com.nimc.agentonboarding.data;

import android.content.Context;

public class AuditLogger {
    public static void log(Context ctx, String type, String details) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(ctx);
            AuditLogEntity e = new AuditLogEntity();
            e.eventType = type;
            e.details = details;
            e.timestamp = System.currentTimeMillis();
            db.auditLogDao().insert(e);
        }).start();
    }
}
