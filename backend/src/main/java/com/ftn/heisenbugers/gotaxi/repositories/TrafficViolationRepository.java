package com.ftn.heisenbugers.gotaxi.repositories;

import com.ftn.heisenbugers.gotaxi.models.TrafficViolation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrafficViolationRepository extends JpaRepository<TrafficViolation, UUID> {
}
