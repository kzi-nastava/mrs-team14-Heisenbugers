package com.example.gotaximobile.models.dtos;

public class LoginResponseDTO {
    public String accessToken;
    public String tokenType;
    public String userId;
    public String role;

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
