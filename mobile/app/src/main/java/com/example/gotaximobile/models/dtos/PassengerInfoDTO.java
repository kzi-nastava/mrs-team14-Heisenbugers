package com.example.gotaximobile.models.dtos;

import java.util.UUID;

public class PassengerInfoDTO {
    private UUID passengerId;
    private String firstName;
    private String lastName;
    private String email;
    private String profileImageUrl;

    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }
    public String getEmail()     { return email; }
}
