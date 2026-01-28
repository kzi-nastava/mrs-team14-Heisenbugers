package com.ftn.heisenbugers.gotaxi.repositories;

import com.ftn.heisenbugers.gotaxi.models.DriverProfileRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DriverProfileRequestRepository extends JpaRepository<DriverProfileRequest, UUID> {
}
