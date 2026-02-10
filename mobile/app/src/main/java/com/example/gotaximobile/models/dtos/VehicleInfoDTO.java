package com.example.gotaximobile.models.dtos;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.MapPin;

import java.util.UUID;

public class VehicleInfoDTO {
    public UUID id;
    public String model;
    public String licensePlate;
    public double latitude;
    public double longitude;
    public boolean occupied;

    public MapPin toMapPin() {
        MapPin pin = new MapPin(this.latitude, this.longitude);
        pin.iconResId = this.occupied ? R.drawable.ic_car_occupied : R.drawable.ic_car_available;
        pin.popup = "Model: " + this.model + "\nLicense Plate: " + this.licensePlate
                + (this.occupied ? "\nOccupied" : "\nAvailable");
        return pin;
    }
}
