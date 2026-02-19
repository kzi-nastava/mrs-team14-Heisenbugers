package com.example.gotaximobile.models.dtos;

import java.util.UUID;

public class CreatedDriverDTO {
    public UUID id;
    public String email;
    public String firstName;
    public String lastName;
    public String phone;
    public String address;
    public CreatedVehicleDTO vehicle;
}