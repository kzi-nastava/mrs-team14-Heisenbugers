package com.ftn.heisenbugers.gotaxi.repositories;

import com.ftn.heisenbugers.gotaxi.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {
    @Query("""
        select d from Driver d
        join fetch d.vehicle
        where d.id = :id
    """)
    Optional<Driver> findByIdWithVehicle(UUID id);

    List<Driver> findByWorkingTrue();

    List<Driver> findByWorkingTrueAndAvailableTrue();
}
