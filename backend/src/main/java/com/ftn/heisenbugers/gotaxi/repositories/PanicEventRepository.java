package com.ftn.heisenbugers.gotaxi.repositories;

import com.ftn.heisenbugers.gotaxi.models.PanicEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PanicEventRepository extends JpaRepository<PanicEvent, UUID> {
    List<PanicEvent> findByResolvedFalseOrderByCreatedAtDesc();
    List<PanicEvent> findByRideIdOrderByCreatedAtDesc(UUID rideId);
    Optional<PanicEvent> findFirstByRideIdAndResolvedFalseOrderByCreatedAtDesc(UUID rideId);
}
