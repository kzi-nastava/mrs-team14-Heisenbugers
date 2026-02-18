package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.Vehicle;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import com.ftn.heisenbugers.gotaxi.models.services.EmailService;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class FinishRideIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    RideRepository rideRepository;
    @Autowired
    UserRepository userRepository;

    @MockitoBean
    EmailService emailService;

    @Test
    void finishRide_success_returns200_and_updates_ride_and_driver() throws Exception {
        Driver driver = persistDriverWithVehicle("driver1@example.test");
        Ride ride = persistRide(driver, RideStatus.ONGOING);

        mockMvc.perform(post("/api/rides/{rideId}/finish", ride.getId())
                        .with(authAsDriver(driver)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.message").value("Ride finished successfully"));

        Ride updatedRide = rideRepository.findById(ride.getId()).orElseThrow();
        assertThat(updatedRide.getStatus()).isEqualTo(RideStatus.FINISHED);
        assertThat(updatedRide.getEndedAt()).isNotNull();

        Driver updatedDriver = (Driver) userRepository.findById(driver.getId()).orElseThrow();
        assertThat(updatedDriver.isAvailable()).isTrue();
    }

    @Test
    void finishRide_when_ride_not_ongoing_returns400() throws Exception {
        Driver driver = persistDriverWithVehicle("driver2@example.test");
        Ride ride = persistRide(driver, RideStatus.ASSIGNED);

        mockMvc.perform(post("/api/rides/{rideId}/finish", ride.getId())
                        .with(authAsDriver(driver)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void finishRide_when_wrong_driver_returns400() throws Exception {
        Driver realDriver = persistDriverWithVehicle("driver3@example.test");
        Driver otherDriver = persistDriverWithVehicle("driver4@example.test");
        Ride ride = persistRide(realDriver, RideStatus.ONGOING);

        mockMvc.perform(post("/api/rides/{rideId}/finish", ride.getId())
                        .with(authAsDriver(otherDriver)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void finishRide_without_auth_returns403_or_401_depends_on_config() throws Exception {
        Driver driver = persistDriverWithVehicle("driver5@example.test");
        Ride ride = persistRide(driver, RideStatus.ONGOING);

        mockMvc.perform(post("/api/rides/{rideId}/finish", ride.getId()))
                .andExpect(status().isForbidden());
    }

    private RequestPostProcessor authAsDriver(Driver driver) {
        var auth = new UsernamePasswordAuthenticationToken(
                driver,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_DRIVER"))
        );
        return authentication(auth);
    }

    private Ride persistRide(Driver driver, RideStatus status) {
        Ride r = new Ride();
        r.setDriver(driver);
        r.setStatus(status);
        r.setPassengers(List.of());
        return rideRepository.save(r);
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

        d.setAvailable(false);
        d.setWorking(false);
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

        return (Driver) userRepository.save(d);
    }
}