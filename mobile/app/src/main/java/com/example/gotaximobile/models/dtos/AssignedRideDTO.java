package com.example.gotaximobile.models.dtos;

import java.util.List;
import java.util.UUID;

public class AssignedRideDTO {
    public UUID rideId;

    public LocationDTO start;
    public LocationDTO end;
    public List<LocationDTO> stops;

    public List<PassengerInfoDTO> passengers;

    public double distanceKm;
    public int estimatedTimeMin;
}
