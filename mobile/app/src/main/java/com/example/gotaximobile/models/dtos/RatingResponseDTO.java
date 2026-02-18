package com.example.gotaximobile.models.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class RatingResponseDTO {
    private UUID id;
    private UUID rideId;
    private int driverScore;
    private int vehicleScore;
    private String comment;
    private LocalDateTime createdAt;


    public Integer getDriverScore() { return driverScore; }
    public Integer getVehicleScore() { return vehicleScore; }
    public String getComment() { return comment; }
}
