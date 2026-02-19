package com.example.gotaximobile.models.dtos;

public class StopRideRequestDTO {
    private String note; //??
    private Double latitude;
    private Double longitude;
    private String address;

    public StopRideRequestDTO(Double latitude, Double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }
}
