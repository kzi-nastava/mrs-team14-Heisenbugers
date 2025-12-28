package com.ftn.heisenbugers.gotaxi.repositories;

import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT u from User u WHERE type(u) = Driver ")
    List<Driver> findAllDrivers();

    @Query("SELECT d FROM Driver d WHERE d.id = :id")
    Optional<Driver> findDriverById(@Param("id") UUID id);
}
