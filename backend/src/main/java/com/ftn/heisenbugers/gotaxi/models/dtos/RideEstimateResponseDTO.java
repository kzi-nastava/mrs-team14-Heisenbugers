package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class RideEstimateResponseDTO {
    @Getter @Setter
    private double distanceKm;
    @Getter @Setter
    private int estimatedTimeMin;
    @Getter @Setter
    private BigDecimal estimatedPrice;
    @Getter @Setter
    private String polyline;              // optional
    @Getter @Setter
    private List<LocationDTO> routePoints; // optional
}
