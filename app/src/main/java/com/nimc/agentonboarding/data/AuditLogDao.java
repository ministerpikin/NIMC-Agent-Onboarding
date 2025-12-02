package com.nimc.agentonboarding.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface AuditLogDao {
    @Insert
    long insert(AuditLogEntity e);

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    List<AuditLogEntity> getAll();

    @Query("DELETE FROM audit_logs")
    void clear();
}
