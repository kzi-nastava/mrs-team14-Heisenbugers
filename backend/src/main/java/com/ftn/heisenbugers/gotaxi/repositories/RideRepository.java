package com.ftn.heisenbugers.gotaxi.repositories;

import com.ftn.heisenbugers.gotaxi.models.Ride;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RideRepository extends JpaRepository<Ride, UUID> {
    List<Ride> getRideById(UUID id);

    List<Ride> findByDriverId(UUID driverId);

    List<Ride> findByDriverId(UUID driverId, Sort sort);

    List<Ride> findByDriverIdAndStartedAtBetween(UUID driverId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Ride> findByDriverIdAndStartedAtAfter(UUID driverId, LocalDateTime start, Sort sort);

    List<Ride> findByDriverIdAndStartedAtBefore(UUID driverId, LocalDateTime end, Sort sort);

}
