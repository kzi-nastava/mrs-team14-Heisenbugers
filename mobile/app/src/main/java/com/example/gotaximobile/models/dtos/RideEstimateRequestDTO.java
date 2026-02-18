package com.example.gotaximobile.models.dtos;

import com.example.gotaximobile.models.enums.VehicleType;
import java.util.List;

public class RideEstimateRequestDTO {
    private LocationDTO start;
    private LocationDTO destination;
    private List<LocationDTO> stops;
    private VehicleType vehicleType;
    private boolean babyTransport;
    private boolean petTransport;

    public RideEstimateRequestDTO() {}

    public LocationDTO getStart() { return start; }
    public LocationDTO getDestination() { return destination; }
    public List<LocationDTO> getStops() { return stops; }
    public VehicleType getVehicleType() { return vehicleType; }
    public boolean isBabyTransport() { return babyTransport; }
    public boolean isPetTransport() { return petTransport; }

    public void setStart(LocationDTO start) { this.start = start; }
    public void setDestination(LocationDTO destination) { this.destination = destination; }
    public void setStops(List<LocationDTO> stops) { this.stops = stops; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
    public void setBabyTransport(boolean babyTransport) { this.babyTransport = babyTransport; }
    public void setPetTransport(boolean petTransport) { this.petTransport = petTransport; }
}
