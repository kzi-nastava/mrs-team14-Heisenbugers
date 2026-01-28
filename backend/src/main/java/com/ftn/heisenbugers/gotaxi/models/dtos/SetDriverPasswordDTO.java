package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetDriverPasswordDTO {
    private String password;
    private String confirmPassword;
}
