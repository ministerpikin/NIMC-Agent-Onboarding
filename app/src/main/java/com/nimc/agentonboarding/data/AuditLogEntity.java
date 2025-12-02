package com.nimc.agentonboarding.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "audit_logs")
public class AuditLogEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "event")
    public String event;

    @ColumnInfo(name = "details")
    public String details;

    @ColumnInfo(name = "timestamp")
    public long timestamp;
}

