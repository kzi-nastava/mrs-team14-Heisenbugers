package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class RatingResponseDTO {
    private UUID id;
    private UUID rideId;
    private int driverScore;
    private int vehicleScore;
    private String comment;
    private LocalDateTime createdAt;
}
