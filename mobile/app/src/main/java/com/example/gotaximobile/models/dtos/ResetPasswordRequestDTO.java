package com.example.gotaximobile.models.dtos;

public class ResetPasswordRequestDTO {
    public String token;
    public String newPassword;
    public String confirmPassword;

    public ResetPasswordRequestDTO(String token, String newPassword, String confirmPassword) {
        this.token = token;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getToken() {
        return token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
