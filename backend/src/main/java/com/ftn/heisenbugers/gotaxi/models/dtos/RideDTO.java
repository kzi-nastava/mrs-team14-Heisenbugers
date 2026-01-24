package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideDTO {
    private String rideId;
    private DriverDto driver;
    private List<LocationDTO> route;
    private LocationDTO startLocation;
    private LocationDTO endLocation;
    private double price;
}
