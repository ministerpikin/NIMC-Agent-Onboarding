package com.nimc.agentonboarding.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface AgentDao {
    @Insert
    long insert(AgentEntity a);

    @Query("SELECT * FROM agents WHERE status = :status")
    List<AgentEntity> getByStatus(String status);

    @Query("SELECT * FROM agents ORDER BY id DESC LIMIT 1")
    AgentEntity getLatest();
}
