package com.example.gotaximobile.models.dtos;

public class SetDriverPasswordDTO {
    public String password;
    public String confirmPassword;

    public SetDriverPasswordDTO(String password, String confirmPassword){
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}