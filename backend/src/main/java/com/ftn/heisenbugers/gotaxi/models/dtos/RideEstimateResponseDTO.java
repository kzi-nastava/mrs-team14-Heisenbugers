package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideEstimateResponseDTO {
    private double distanceKm;
    private int estimatedTimeMin;
    private BigDecimal estimatedPrice;
    private String polyline;              // optional
    private List<LocationDTO> routePoints; // optional
}
