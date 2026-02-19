package com.example.gotaximobile.models.dtos;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class FavoriteRouteDTO implements Serializable {
    public UUID Id;
    public LocationDTO startAddress;
    public LocationDTO endAddress;

    public List<LocationDTO> stops;

    public double distanceKm;
    public int timeMin;
}
