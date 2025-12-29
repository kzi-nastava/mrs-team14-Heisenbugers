package com.ftn.heisenbugers.gotaxi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class SetDriverPasswordDTO {
    @Getter @Setter
    private String password;
}
