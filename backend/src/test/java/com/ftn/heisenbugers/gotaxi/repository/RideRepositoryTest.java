package com.ftn.heisenbugers.gotaxi.repository;

import com.ftn.heisenbugers.gotaxi.models.*;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class RideRepositoryTest {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should return rides with ASSIGNED or ONGOING status for a specific driver")
    void shouldFindActiveRides() {
        Driver driver = persistDriverWithVehicle("driver1@gmail.com");

        createRide(driver, RideStatus.ASSIGNED);
        createRide(driver, RideStatus.ONGOING);
        createRide(driver, RideStatus.FINISHED);

        Driver otherDriver = persistDriverWithVehicle("driver2@gmail.com");
        createRide(otherDriver, RideStatus.ASSIGNED);

        List<Ride> activeRides = rideRepository.findActiveRidesByDriver(driver.getId());

        assertThat(activeRides).hasSize(2);
        assertThat(activeRides).allMatch(ride ->
                ride.getDriver().getId().equals(driver.getId()) &&
                        (ride.getStatus() == RideStatus.ASSIGNED || ride.getStatus() == RideStatus.ONGOING)
        );
    }

    @Test
    @DisplayName("Should return empty list if driver has no active rides")
    void shouldReturnEmptyWhenNoActiveRides() {
        Driver driver = persistDriverWithVehicle("driver3@gmail.com");
        createRide(driver, RideStatus.CANCELED);

        List<Ride> result = rideRepository.findActiveRidesByDriver(driver.getId());

        assertThat(result).isEmpty();
    }

    private void createRide(Driver d, RideStatus status) {
        Ride ride = new Ride();
        Location start = new Location(19.795411, 45.251058, "Dusana Danilovica 3, Novi Sad");
        Location destination = new Location(19.824706, 45.247205, "Laze Nancica 1, Novi Sad");

        Route route = new Route();
        route.setDistanceKm(3.0);
        route.setEstimatedTimeMin(5);
        route.setPolyline(new ArrayList<Location>());
        route.setStart(start);
        route.setDestination(destination);
        route.setStops(List.of());

        ride.setRoute(route);
        ride.setDriver(d);
        ride.setStatus(status);
        rideRepository.save(ride);
    }

    private Driver persistDriverWithVehicle(String email) {
        Driver d = new Driver();
        d.setEmail(email);
        d.setPasswordHash("testpass123");
        d.setFirstName("Test");
        d.setLastName("Driver");
        d.setPhone("000000000");
        d.setAddress("Test Address 1");
        d.setActivated(true);
        d.setBlocked(false);

        d.setAvailable(true);
        d.setWorking(true);
        d.setActiveHoursLast24h(0);

        Vehicle v = new Vehicle();
        v.setModel("TestModel");
        v.setType(VehicleType.STANDARD);
        v.setLicensePlate("TEST-" + UUID.randomUUID());
        v.setSeatCount(4);
        v.setBabyTransport(false);
        v.setPetTransport(false);

        d.setVehicle(v);
        v.setDriver(d);

        return userRepository.save(d);
    }
}
