package com.example.gotaximobile.models;

import android.icu.text.NumberFormat;

import androidx.annotation.NonNull;

import com.example.gotaximobile.models.dtos.DriverRideHistoryDTO;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.UUID;

public class Ride implements Serializable {
    private UUID id;
    private Driver driver;
    private String startLocation;
    private String endLocation;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double price;
    private double rating;
    private double maxRating;
    private boolean cancelled;
    private List<User> passengers;
    private List<String> trafficViolations;
    private boolean wasPanic;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    private static final DateTimeFormatter OUT_FMT =
            DateTimeFormatter.ofPattern("HH:mm");

    public Ride(Driver driver, String startLocation, String endLocation, LocalDateTime startTime,
                LocalDateTime endTime, double price, double rating, double maxRating, boolean cancelled,
                List<User> passengers, List<String> trafficViolations, boolean wasPanic) {
        this.driver = driver;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.rating = rating;
        this.maxRating = maxRating;
        this.cancelled = cancelled;
        this.passengers = passengers;
        this.trafficViolations = trafficViolations;
        this.wasPanic = wasPanic;
    }

    public Ride(DriverRideHistoryDTO dto) {
        this.id = dto.rideId;
        this.driver = null;
        this.startLocation = dto.startAddress;
        this.endLocation = dto.endAddress;

        DateTimeFormatter FMT = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .optionalEnd()
                .toFormatter();

        this.startTime = dto.startedAt != null ? LocalDateTime.parse(dto.startedAt, FMT) : null;
        this.endTime = dto.endedAt != null ? LocalDateTime.parse(dto.endedAt, FMT) : null;

        this.price = dto.price;
        this.rating = 0.0;
        this.maxRating = 0.0;
        this.cancelled = dto.canceled;
        this.passengers = new java.util.ArrayList<>();
        this.trafficViolations = dto.trafficViolations != null
                ? dto.trafficViolations.stream().map(Object::toString).collect(java.util.stream.Collectors.toList())
                : new java.util.ArrayList<>();
        this.wasPanic = dto.panicTriggered;
    }

    public UUID getId() {
        return id;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getStartTimeString() {
        return startTime != null ? startTime.format(OUT_FMT) : "N/A";
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getEndTimeString() {
        return endTime != null ? endTime.format(OUT_FMT) : "N/A";
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getFormatedTime() {
        return getStartTimeString() + " - " + getEndTimeString();
    }

    public double getPrice() {
        return price;
    }

    public String getFormatedPrice() {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Constants.LOCALE);
        return currencyFormatter.format(price);
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getMaxRating() {
        return maxRating;
    }

    public void setMaxRating(double maxRating) {
        this.maxRating = maxRating;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public List<User> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<User> passengers) {
        this.passengers = passengers;
    }

    public List<String> getTrafficViolations() {
        return trafficViolations;
    }

    public void setTrafficViolations(List<String> trafficViolations) {
        this.trafficViolations = trafficViolations;
    }

    public boolean isWasPanic() {
        return wasPanic;
    }

    public void setWasPanic(boolean wasPanic) {
        this.wasPanic = wasPanic;
    }

    public String getInfoForList() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Constants.LOCALE);

        return String.format("%s (%s)\n ↓\n %s (%s)",
                startLocation,
                startTime != null ? startTime.format(OUT_FMT) : "N/A",
                endLocation,
                endTime != null ? endTime.format(OUT_FMT) : "N/A");
    }

    @NonNull
    @Override
    public String toString() {
        return "CALLED TO STRING";
    }
}
