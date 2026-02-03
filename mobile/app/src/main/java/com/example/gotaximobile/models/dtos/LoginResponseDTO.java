package com.example.gotaximobile.models.dtos;

public class LoginResponseDTO {
    public String accessToken;
    public String tokenType;
    public String userId; // UUID как String
    public String role;   // "PASSENGER"/"DRIVER"/"ADMIN"

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }
}
