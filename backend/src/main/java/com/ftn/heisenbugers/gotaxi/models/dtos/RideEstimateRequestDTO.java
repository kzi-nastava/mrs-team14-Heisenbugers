package com.ftn.heisenbugers.gotaxi.models.dtos;

import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideEstimateRequestDTO {
    private LocationDTO start;
    private LocationDTO destination;
    private List<LocationDTO> stops;   // optional
    private VehicleType vehicleType;   // STANDARD/LUXURY/VAN
    private boolean babyTransport;
    private boolean petTransport;
}
