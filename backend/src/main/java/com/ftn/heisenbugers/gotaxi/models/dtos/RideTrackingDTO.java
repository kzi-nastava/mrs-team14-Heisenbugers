package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class RideTrackingDTO {
    private UUID rideId;
    private DriverDto driver;
    private double vehicleLatitude;
    private double vehicleLongitude;
    private int estimatedTimeRemainingMinutes;
    private List<LocationDTO> route;
}
