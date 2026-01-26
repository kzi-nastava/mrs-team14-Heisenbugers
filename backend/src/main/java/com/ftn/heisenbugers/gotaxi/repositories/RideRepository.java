package com.ftn.heisenbugers.gotaxi.repositories;

import com.ftn.heisenbugers.gotaxi.models.Passenger;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RideRepository extends JpaRepository<Ride, UUID> {
    List<Ride> getRideById(UUID id);

    List<Ride> findByDriverId(UUID driverId);

    List<Ride> findByDriverId(UUID driverId, Sort sort);

    List<Ride> findByDriverIdAndStartedAtBetween(UUID driverId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Ride> findByDriverIdAndStartedAtAfter(UUID driverId, LocalDateTime start, Sort sort);

    List<Ride> findByDriverIdAndStartedAtBefore(UUID driverId, LocalDateTime end, Sort sort);

    List<Ride> findByPassengersContaining(Passenger passenger, Sort sort);

    List<Ride> findByPassengersContaining(Passenger passenger);

    List<Ride> findByPassengersContainingAndStartedAtBetween(Passenger passengers, LocalDateTime startedAt,
                                                             LocalDateTime startedAt2, Sort sort);

    List<Ride> findByPassengersContainingAndStartedAtAfter(Passenger passengers, LocalDateTime startedAt,
                                                           Sort sort);

    List<Ride> findByPassengersContainingAndStartedAtBefore(Passenger passengers, LocalDateTime startedAtBefore,
                                                            Sort sort);

    Optional<Ride> findFirstByDriverIdAndStatusInOrderByScheduledAtAsc(UUID driverId, List<RideStatus> statuses);


    Ride findRideById(UUID id);

    Optional<Ride> findByPassengersContainingAndStatus(Passenger passenger, RideStatus status);

    Optional<Ride> findByDriverIdAndStatus(UUID driver_id, RideStatus status);
}
