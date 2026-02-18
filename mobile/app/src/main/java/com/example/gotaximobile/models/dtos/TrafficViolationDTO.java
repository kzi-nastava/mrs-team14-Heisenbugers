package com.example.gotaximobile.models.dtos;

import androidx.annotation.NonNull;

public class TrafficViolationDTO {
    private String title;
    private String description;

    @NonNull
    @Override
    public String toString() {
        return title;
    }


    public String getTitle() { return title; }
    public String getDescription() { return description; }
}

