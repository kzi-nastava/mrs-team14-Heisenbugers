package com.ftn.heisenbugers.gotaxi.repositories;

import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.TrafficViolation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TrafficViolationRepository extends JpaRepository<TrafficViolation, UUID> {
    List<TrafficViolation> getAllById(UUID id);

    List<TrafficViolation> getAllByRide(Ride ride);
    List<TrafficViolation> findByRideIdOrderByCreatedAtDesc(UUID rideId);
}
