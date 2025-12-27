package com.ftn.heisenbugers.gotaxi.models;

import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
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

    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

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
}
