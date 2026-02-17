package com.ftn.heisenbugers.gotaxi.models;

import com.ftn.heisenbugers.gotaxi.utils.GeoHasher;
import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
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

    @Column(columnDefinition = "TEXT")
    @Basic(fetch = FetchType.EAGER)
    private String polyline;

    @Column(name = "point_count")
    private int pointCount;

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

    @Builder.Default
    private boolean favorite = false;

    public void setPolyline(List<Location> locations) {
        double[][] coords = locations.stream()
                .map(l -> new double[]{l.getLongitude(), l.getLatitude()})
                .toArray(double[][]::new);
        this.polyline = GeoHasher.geohash(coords);
        this.pointCount = locations.size();
    }

    public List<Location> getStops() {
        if (polyline == null || pointCount == 0) return List.of();
        double[][] coords = GeoHasher.decodeGeohash(polyline, pointCount);
        return Arrays.stream(coords)
                .map(c -> new Location(c[1], c[0]))
                .toList();
    }

    public List<Location> getStopsWithAddresses(){
        return stops;
    }

}
