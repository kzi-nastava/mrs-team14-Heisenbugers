package com.example.gotaximobile.models.dtos;

import java.util.List;

public class RideDTO {
    public String rideId;
    public DriverDto driver;
    public List<LocationDTO> route;
    public LocationDTO startLocation;
    public LocationDTO endLocation;
    public double price;
    public String startTime;
    public String endTime;
}
