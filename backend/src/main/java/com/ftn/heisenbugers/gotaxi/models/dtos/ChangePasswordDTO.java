package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDTO {
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;
}
