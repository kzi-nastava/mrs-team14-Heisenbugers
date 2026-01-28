package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignedRideDTO {
    private UUID rideId;

    private LocationDTO start;
    private LocationDTO end;
    private List<LocationDTO> stops;

    private List<PassengerInfoDTO> passengers;

    private double distanceKm;
    private int estimatedTimeMin;
}
