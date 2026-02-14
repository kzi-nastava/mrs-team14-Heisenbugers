package com.ftn.heisenbugers.gotaxi.models.dtos;

import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PriceDTO {
    public VehicleType vehicleType;
    public double startingPrice;
}
