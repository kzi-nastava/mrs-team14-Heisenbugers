package com.example.gotaximobile.models.dtos;

import java.util.List;
import java.util.UUID;

public class RideTrackingDTO {
    public UUID rideId;
    public DriverDto driver;
    public double vehicleLatitude;
    public double vehicleLongitude;
    public int estimatedTimeRemainingMinutes;
    public List<LocationDTO> route;
    public LocationDTO startLocation;
    public LocationDTO endLocation;
}
