package com.example.gotaximobile.models.dtos;

import java.util.UUID;

public class DriverRequestListDTO {
    public UUID id;
    public String firstName;
    public String lastName;
    public String email;

    public DriverRequestListDTO(UUID id, String firstName, String lastName, String email){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
