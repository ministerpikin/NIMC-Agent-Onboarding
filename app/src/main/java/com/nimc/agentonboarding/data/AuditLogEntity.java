package com.nimc.agentonboarding.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "audit_logs")
public class AuditLogEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String eventType;
    public String details;
    public long timestamp;
}
