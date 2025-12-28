package com.example.gotaximobile.models;

import android.icu.text.NumberFormat;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Ride implements Serializable {
    private Driver driver;
    private String startLocation;
    private String endLocation;
    private Date startTime;
    private Date endTime;
    private double price;
    private double rating;
    private double maxRating;
    private boolean cancelled;
    private List<User> passengers;
    private List<String> trafficViolations;
    private boolean wasPanic;

    public Ride(Driver driver, String startLocation, String endLocation, Date startTime,
                Date endTime, double price, double rating, double maxRating, boolean cancelled,
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getFormatedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Constants.LOCALE);
        return sdf.format(startTime) + " - " + sdf.format(endTime);
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
                startTime != null ? sdf.format(startTime) : "N/A",
                endLocation,
                endTime != null ? sdf.format(endTime) : "N/A");
    }

    @NonNull
    @Override
    public String toString() {
        return "CALLED TO STRING";
    }
}
