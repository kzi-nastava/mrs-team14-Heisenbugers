package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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
    //private String profileImageUrl; // optional
    private MultipartFile profileImage;
}
