package com.ftn.heisenbugers.gotaxi.repositories;

import com.ftn.heisenbugers.gotaxi.models.Price;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PriceRepository extends JpaRepository<Price, UUID> {
    @Query("SELECT p.startingPrice FROM Price p WHERE p.vehicleType = :vehicleType")
    double getStartingPriceByVehicleType(@Param("vehicleType") VehicleType vehicleType);
}
