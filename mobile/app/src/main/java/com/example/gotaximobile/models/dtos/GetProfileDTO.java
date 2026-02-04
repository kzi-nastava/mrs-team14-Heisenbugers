package com.example.gotaximobile.models.dtos;

public class GetProfileDTO {
    public String id;
    public String email;
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public String address;
    public String profileImageUrl;

    public GetProfileDTO(String id, String email, String firstName, String lastName, String phoneNumber, String address, String profileImageUrl) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.profileImageUrl = profileImageUrl;
    }

}
