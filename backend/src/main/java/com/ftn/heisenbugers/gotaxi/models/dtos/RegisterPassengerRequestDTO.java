package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterPassengerRequestDTO {
    private String email;
    private String password;
    private String confirmPassword;

    private String firstName;
    private String lastName;

    private String phone;
    private String address;
    private String profileImageUrl; // optional
}
