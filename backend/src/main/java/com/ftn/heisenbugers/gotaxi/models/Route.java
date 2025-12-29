package com.ftn.heisenbugers.gotaxi.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Route entity representing the path of a ride.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "routes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route extends BaseEntity {

    private double distanceKm;

    private int estimatedTimeMin;

    @Lob
    private String polyline;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "start_location_id")
    private Location start;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_location_id")
    private Location destination;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private List<Location> stops;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
