package com.example.gotaximobile.models.dtos;

import java.util.List;

public class RideDetailsDTO {
    public String rideId;
    public DriverDto driver;
    public List<LocationDTO> route;
    public LocationDTO startLocation;
    public LocationDTO endLocation;

    public String getRideId() {
        return rideId;
    }

    public DriverDto getDriver() {
        return driver;
    }

    public List<LocationDTO> getRoute() {
        return route;
    }

    public LocationDTO getStartLocation() {
        return startLocation;
    }

    public LocationDTO getEndLocation() {
        return endLocation;
    }
}
