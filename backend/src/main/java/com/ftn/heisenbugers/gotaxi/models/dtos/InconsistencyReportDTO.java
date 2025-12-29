package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InconsistencyReportDTO {
    private UUID rideId;
    private UUID reporterId;
    private String note;
    private LocalDateTime createdAt;

}
