package com.example.gotaximobile.models.dtos;

public class PanicEventDTO {
    private String id;
    private boolean resolved;

    private String rideId;
    private String message;
    private String createdAt;

    private Double vehicleLat;
    private Double vehicleLng;

    private String startAddress;
    private String endAddress;

    public String getId() { return id; }
    public boolean isResolved() { return resolved; }
    public String getRideId() { return rideId; }
    public String getMessage() { return message; }
    public String getCreatedAt() { return createdAt; }
    public Double getVehicleLat() { return vehicleLat; }
    public Double getVehicleLng() { return vehicleLng; }
    public String getStartAddress() { return startAddress; }
    public String getEndAddress() { return endAddress; }
}
