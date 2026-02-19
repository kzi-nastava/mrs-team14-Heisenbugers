package com.example.gotaximobile.models.dtos;

import com.example.gotaximobile.models.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class RideHistoryDTO {
    private UUID rideId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String startAddress;
    private String endAddress;
    private boolean canceled;
    private User canceledBy;
    private double price;
    private boolean panicTriggered;
    private boolean favorite;
    private boolean rated;


    public UUID getRideId() {
        return rideId;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public User getCanceledBy() {
        return canceledBy;
    }

    public double getPrice() {
        return price;
    }

    public boolean isPanicTriggered() {
        return panicTriggered;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public boolean isRated() {
        return rated;
    }

    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}
