package com.example.gotaximobile.models.dtos;

public class CreateVehicleDTO {
    public String vehicleModel;
    public String vehicleType;
    public String licensePlate;
    public int seatCount;
    public boolean babyTransport;
    public boolean petTransport;

    public CreateVehicleDTO(String vehicleModel,  String vehicleType, String licensePlate, int seatCount, boolean babyTransport, boolean petTransport){
        this.vehicleModel = vehicleModel;
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.seatCount = seatCount;
        this.babyTransport = babyTransport;
        this.petTransport = petTransport;
    }
}
