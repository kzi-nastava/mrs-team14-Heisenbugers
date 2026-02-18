package com.example.gotaximobile.viewmodels;

import androidx.lifecycle.ViewModel;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class RideBookingViewModel extends ViewModel {

    public double distanceKm = 0;
    public int durationMinutes = 0;

    public GeoPoint startPoint;
    public GeoPoint endPoint;
    public final List<GeoPoint> stopPoints = new ArrayList<>();

    // We also need to keep track of the addresses (Strings) for the payload
    public String startAddress;
    public String endAddress;
    public final List<String> stopAddresses = new ArrayList<>();

    public void addStop(GeoPoint point, String address) {
        stopPoints.add(point);
        stopAddresses.add(address);
    }

    public void clearAll() {
        startPoint = null;
        endPoint = null;
        stopPoints.clear();
        startAddress = null;
        endAddress = null;
        stopAddresses.clear();
        distanceKm = 0;
        durationMinutes = 0;
    }
}