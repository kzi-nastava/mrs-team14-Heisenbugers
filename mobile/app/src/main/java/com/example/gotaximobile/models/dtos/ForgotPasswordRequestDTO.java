package com.example.gotaximobile.models.dtos;

public class ForgotPasswordRequestDTO {
    public String email;
    public ForgotPasswordRequestDTO(String email) { this.email = email; }

    public String getEmail() {
        return email;
    }
}
