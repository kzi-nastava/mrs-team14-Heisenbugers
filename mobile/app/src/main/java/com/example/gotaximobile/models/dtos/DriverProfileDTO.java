package com.example.gotaximobile.models.dtos;

import com.example.gotaximobile.models.enums.VehicleType;

public class DriverProfileDTO {
    public String firstName;
    public String lastName;
    public String phone;
    public String address;
    public String profileImageUrl;
    public String model;
    public VehicleType type;
    public String licensePlate;
    public int seatCount;
    public boolean babyTransport;
    public boolean petTransport;

    public DriverProfileDTO(String firstName, String lastName, String phone, String address,
                            String profileImageUrl, String model, VehicleType type, String licensePlate,
                            int seatCount, boolean babyTransport, boolean petTransport){
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.profileImageUrl = profileImageUrl;
        this.model = model;
        this.type = type;
        this.licensePlate = licensePlate;
        this.seatCount = seatCount;
        this.babyTransport = babyTransport;
        this.petTransport = petTransport;
    }
}
