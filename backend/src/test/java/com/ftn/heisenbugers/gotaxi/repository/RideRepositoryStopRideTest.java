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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RideRepositoryStopRideTest {

    @Autowired RideRepository rideRepository;
    @Autowired UserRepository userRepository;

    @Test
    @DisplayName("findActiveRidesByDriver: returns only ASSIGNED and ONGOING for the given driverId")
    void testFindActiveRidesByDriver_returnsOnlyAssignedAndOngoing() {
        Driver driver = persistDriverWithVehicle(uniqueEmail("driver"));

        Ride assigned = createRide(driver, RideStatus.ASSIGNED, null);
        Ride ongoing  = createRide(driver, RideStatus.ONGOING, null);
        Ride finished = createRide(driver, RideStatus.FINISHED, null);

        Driver otherDriver = persistDriverWithVehicle(uniqueEmail("other"));
        Ride otherOngoing = createRide(otherDriver, RideStatus.ONGOING, null);

        List<Ride> result = rideRepository.findActiveRidesByDriver(driver.getId());

        assertThat(result)
                .extracting(Ride::getId)
                .containsExactlyInAnyOrder(assigned.getId(), ongoing.getId());

        assertThat(result)
                .allMatch(r -> r.getDriver().getId().equals(driver.getId()))
                .allMatch(r -> r.getStatus() == RideStatus.ASSIGNED || r.getStatus() == RideStatus.ONGOING);

        assertThat(result).extracting(Ride::getId).doesNotContain(finished.getId(), otherOngoing.getId());
    }

    @Test
    @DisplayName("findActiveRidesByDriver: returns empty if the driver does not have ASSIGNED/ONGOING")
    void testFindActiveRidesByDriver_ReturnsEmpty_WhenNoActive() {
        Driver driver = persistDriverWithVehicle(uniqueEmail("driver"));
        createRide(driver, RideStatus.FINISHED, null);
        createRide(driver, RideStatus.CANCELED, null);

        List<Ride> result = rideRepository.findActiveRidesByDriver(driver.getId());

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("findFirstByDriverIdAndStatusInOrderByScheduledAtAsc: returns the closest scheduledAt among given statuses")
    void testFindFirstByDriverIdAndStatusInOrderByScheduledAtAsc_ReturnsEarliestScheduled() {
        Driver driver = persistDriverWithVehicle(uniqueEmail("driver"));

        LocalDateTime base = LocalDateTime.now();
        createRide(driver, RideStatus.ASSIGNED, base.plusMinutes(30));
        Ride earlier = createRide(driver, RideStatus.ONGOING, base.plusMinutes(10));

        Optional<Ride> opt = rideRepository.findFirstByDriverIdAndStatusInOrderByScheduledAtAsc(
                driver.getId(),
                List.of(RideStatus.ASSIGNED, RideStatus.ONGOING)
        );

        assertTrue(opt.isPresent());
        assertEquals(earlier.getId(), opt.get().getId(), "Should return the ride with smallest scheduledAt");
    }



    private String uniqueEmail(String prefix) {
        return prefix + "-" + UUID.randomUUID() + "@example.test";
    }

    private String uniquePhone() {
        return "+38164" + (1000000 + (int)(Math.random() * 9000000));
    }

    private Ride createRide(Driver driver, RideStatus status, LocalDateTime scheduledAt) {
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setStatus(status);
        ride.setCanceled(false);
        ride.setScheduledAt(scheduledAt);

        Location start = Location.builder().latitude(45.0).longitude(19.0).address("Start").build();
        Location dest  = Location.builder().latitude(45.1).longitude(19.1).address("Dest").build();

        Route route = new Route();
        route.setStart(start);
        route.setDestination(dest);
        route.setStops(List.of());
        route.setDistanceKm(1.0);
        route.setEstimatedTimeMin(3);

        ride.setRoute(route);
        ride.setPassengers(List.of());

        return rideRepository.saveAndFlush(ride);
    }

    private Driver persistDriverWithVehicle(String email) {
        Driver d = new Driver();
        d.setEmail(email);

        d.setPasswordHash("12345678");

        d.setFirstName("Test");
        d.setLastName("Driver");


        d.setPhone(uniquePhone());

        d.setAddress("Addr");
        d.setActivated(true);
        d.setBlocked(false);

        d.setAvailable(true);
        d.setWorking(true);
        d.setActiveHoursLast24h(0);

        Vehicle v = new Vehicle();
        v.setModel("Model");
        v.setType(VehicleType.STANDARD);
        v.setLicensePlate("TEST-" + UUID.randomUUID());
        v.setSeatCount(4);
        v.setBabyTransport(false);
        v.setPetTransport(false);

        d.setVehicle(v);
        v.setDriver(d);

        return (Driver) userRepository.saveAndFlush(d);
    }
}
