package com.ftn.heisenbugers.gotaxi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class GetProfileDto {
    @Getter @Setter
    private Long id;
    @Getter @Setter
    private String email;
    @Getter @Setter
    private String firstName;
    @Getter @Setter
    private String lastName;
    @Getter @Setter
    private String phoneNumber;
    @Getter @Setter
    private String address;
    @Getter @Setter
    private String profileImageUrl;
}
