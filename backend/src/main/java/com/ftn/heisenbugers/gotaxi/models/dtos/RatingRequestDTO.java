package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RatingRequestDTO {
    private UUID rideId;
    private int driverScore;
    private int vehicleScore;
    private String comment;

}
