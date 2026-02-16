package com.ftn.heisenbugers.gotaxi.models;

import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Ride entity representing a single trip.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @NotNull
    private RideStatus status;

    private LocalDateTime scheduledAt;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    private double price;

    private boolean canceled;
    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;
    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @ManyToOne
    @JoinColumn(name = "start_id")
    private Location start;

    @ManyToOne
    @JoinColumn(name = "end_id")
    private Location end;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "canceled_by_id")
    private User canceledBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    @NotNull
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToMany
    @JoinTable(
            name = "ride_passengers",
            joinColumns = @JoinColumn(name = "ride_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> passengers;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id")
    private Route route;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "rating_id")
    private Rating rating;

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications;

    @OneToOne(mappedBy = "ride", cascade = CascadeType.ALL)
    private PanicEvent panicEvent;

    @Column(nullable = false, columnDefinition = "boolean default false")
    boolean petTransport = false;

    @Column(nullable = false, columnDefinition = "boolean default false")
    boolean babyTransport = false;

    VehicleType vehicleType;

    public void addPassenger(Passenger p) {
        this.passengers.add(p);
    }

    public String toString() {
        return "Ride{id=" + this.getId() + "}";
    }

}
