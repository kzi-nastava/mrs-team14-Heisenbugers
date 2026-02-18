package com.example.gotaximobile.models.dtos;

import java.util.List;

public class RouteDTO {
    public LocationDTO start;
    public LocationDTO destination;
    public List<LocationDTO> stops;
    public double distanceKm;
    public int estimatedTimeMin;
}