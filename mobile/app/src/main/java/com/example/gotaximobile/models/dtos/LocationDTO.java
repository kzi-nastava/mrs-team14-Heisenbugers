package com.example.gotaximobile.models.dtos;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class LocationDTO implements Serializable {
    public double latitude;
    public double longitude;
    public String address;

    public LocationDTO(String address, double latitude, double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationDTO(){}

    public String getAddress() { return address; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    @NonNull
    @Override
    public String toString() {
        return address;
    }
}
