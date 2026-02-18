package com.example.gotaximobile.models.dtos;

public class GeocodeSuggestionDTO {
    public String display_name;
    public String name;
    public String lat;
    public String lon;

    public GeocodeSuggestionDTO(String display_name, String name, String lat, String lon){
        this.display_name = display_name;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public String toString() {
        return display_name;
    }
}
