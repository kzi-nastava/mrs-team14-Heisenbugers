package com.example.gotaximobile.models.dtos;

import java.util.List;

public class RideRequestDTO {
    public RouteDTO route;
    public List<String> passengersEmails;
    public String vehicleType;
    public boolean babyTransport;
    public boolean petTransport;
    public String scheduledAt; // ISO 8601 string or null
}
