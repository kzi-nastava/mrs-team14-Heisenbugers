package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDTO {
    @Getter @Setter
    private String oldPassword;
    @Getter @Setter
    private String newPassword;
}
