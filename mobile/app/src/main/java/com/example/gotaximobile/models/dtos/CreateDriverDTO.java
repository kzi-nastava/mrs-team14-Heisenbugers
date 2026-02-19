package com.example.gotaximobile.models.dtos;

public class CreateDriverDTO {
    public String email;
    public String firstName;
    public String lastName;
    public String phone;
    public String address;
    public String profileImage;
    public CreateVehicleDTO vehicle;

    public CreateDriverDTO(String email, String firstName, String lastName, String phone,
                           String address, String profileImage, CreateVehicleDTO vehicle){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.profileImage = profileImage;
        this.vehicle = vehicle;
    }
}