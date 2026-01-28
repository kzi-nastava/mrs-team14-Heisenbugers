package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverProfileRequestDetailDTO {

    private UUID id;
    private boolean approved;

    private String submittedBy;
    private LocalDateTime submittedAt;

    private DriverProfileDTO oldProfile;

    private DriverProfileDTO newProfile;
}