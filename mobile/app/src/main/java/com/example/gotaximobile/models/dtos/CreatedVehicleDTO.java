package com.example.gotaximobile.models.dtos;

import com.example.gotaximobile.models.enums.VehicleType;

import java.util.UUID;

public class CreatedVehicleDTO {
    public UUID id;
    public String vehicleModel;
    public VehicleType vehicleType;
    public String licensePlate;
    public int seatCount;
    public boolean babyTransport;
    public boolean petTransport;
}
