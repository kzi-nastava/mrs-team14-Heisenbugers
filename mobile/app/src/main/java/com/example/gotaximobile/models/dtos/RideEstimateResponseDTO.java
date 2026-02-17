package com.example.gotaximobile.models.dtos;

import java.math.BigDecimal;
import java.util.List;

public class RideEstimateResponseDTO {
    private double distanceKm;
    private int estimatedTimeMin;
    private BigDecimal estimatedPrice;
    private String polyline;
    private List<LocationDTO> routePoints;

    public RideEstimateResponseDTO() {}

    public double getDistanceKm() { return distanceKm; }
    public int getEstimatedTimeMin() { return estimatedTimeMin; }
    public BigDecimal getEstimatedPrice() { return estimatedPrice; }
    public String getPolyline() { return polyline; }
    public List<LocationDTO> getRoutePoints() { return routePoints; }

    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
    public void setEstimatedTimeMin(int estimatedTimeMin) { this.estimatedTimeMin = estimatedTimeMin; }
    public void setEstimatedPrice(BigDecimal estimatedPrice) { this.estimatedPrice = estimatedPrice; }
    public void setPolyline(String polyline) { this.polyline = polyline; }
    public void setRoutePoints(List<LocationDTO> routePoints) { this.routePoints = routePoints; }
}
