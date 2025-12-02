package com.nimc.agentonboarding.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "agents")
public class AgentEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String nin;
    public String firstName;
    public String lastName;
    public String gender;
    public String email;
    public String dob;
    public String fepName;
    public String fepCode;
    public String state;
    public String imageBase64;
    public String status; // PENDING or VERIFIED
}
