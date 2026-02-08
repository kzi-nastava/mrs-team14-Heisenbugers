package com.example.gotaximobile.models.dtos;

public class ChangePasswordDTO {
    public String oldPassword;
    public String newPassword;
    public String confirmNewPassword;

    public ChangePasswordDTO(String oldPassword, String newPassword, String confirmNewPassword){
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }
}
