package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerInfoDTO {
    private UUID passengerId;
    private String firstName;
    private String lastName;
    //private String profileImageUrl;
    private String profileImage;
}
