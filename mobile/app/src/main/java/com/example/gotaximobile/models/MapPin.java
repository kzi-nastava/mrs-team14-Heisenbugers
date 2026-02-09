package com.example.gotaximobile.models;

import org.osmdroid.util.GeoPoint;

public class MapPin {
    public double lat;
    public double lng;
    public boolean snapToRoad = false; // optional
    public String popup;               // optional
    public int iconResId = 0;          // optional drawable resource

    public MapPin(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public GeoPoint toGeoPoint() {
        return new GeoPoint(lat, lng);
    }
}
