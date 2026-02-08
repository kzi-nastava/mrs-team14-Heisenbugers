package com.example.gotaximobile.models.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class DriverProfileRequestDetailDTO {

    public String id;
    public boolean approved;

    public String submittedBy;
    public LocalDateTime submittedAt;

    public DriverProfileDTO oldProfile;

    public DriverProfileDTO newProfile;

    public  DriverProfileRequestDetailDTO(String id, boolean approved, String submittedBy,
                                          LocalDateTime submittedAt, DriverProfileDTO oldProfile,
                                          DriverProfileDTO newProfile){
        this.id = id;
        this.approved = approved;
        this.submittedBy = submittedBy;
        this.submittedAt = submittedAt;
        this.oldProfile = oldProfile;
        this.newProfile = newProfile;
    }
}