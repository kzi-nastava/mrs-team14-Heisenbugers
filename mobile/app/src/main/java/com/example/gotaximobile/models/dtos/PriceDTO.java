package com.example.gotaximobile.models.dtos;

public class PriceDTO {
    private String vehicleType;
    private double startingPrice;

    public PriceDTO(String vehicleType, double startingPrice) {
        this.vehicleType = vehicleType;
        this.startingPrice = startingPrice;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public double getStartingPrice() {
        return startingPrice;
    }
}
