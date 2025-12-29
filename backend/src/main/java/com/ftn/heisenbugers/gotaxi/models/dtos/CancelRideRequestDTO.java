package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
public class CancelRideRequestDTO {
    private String reason;
}
