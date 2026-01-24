package com.ftn.heisenbugers.gotaxi.repositories;


import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Passenger;
import com.ftn.heisenbugers.gotaxi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);


    @Query("SELECT u from User u WHERE type(u) = Driver ")
    List<Driver> findAllDrivers();

    @Query("SELECT d FROM Driver d WHERE d.id = :id")
    Optional<Driver> findDriverById(@Param("id") UUID id);

    Passenger findPassengerById(UUID uuid);

    User findUserById(UUID id);
}
