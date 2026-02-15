package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Location;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.Vehicle;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import com.ftn.heisenbugers.gotaxi.models.services.EmailService;
import com.ftn.heisenbugers.gotaxi.repositories.LocationRepository;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("RideActions: Stop ride integration tests")
@AutoConfigureMockMvc
class StopRideIntegrationTest {

    @Autowired MockMvc mockMvc;

    @Autowired RideRepository rideRepository;
    @Autowired UserRepository userRepository;
    @Autowired LocationRepository locationRepository;

    @MockitoBean EmailService emailService;


    private static final double LAT = 45.0;
    private static final double LON = 19.0;

    private static final double STOP_LAT = 45.05;
    private static final double STOP_LON = 19.05;

    private static final String START_ADDR = "Start";
    private static final String OLD_END_ADDR = "Old destination";
    private static final String NEW_STOP_ADDR = "New stop address";

    private Driver baseDriver;

    @BeforeEach
    void setUp() {
        baseDriver = persistDriverWithVehicle("driver-stop-base-" + UUID.randomUUID() + "@example.test");
    }

    @Test
    @DisplayName("200 OK: driver stops ongoing ride -> ride becomes FINISHED, end & endedAt updated, price recalculated")
    void testStopRide_Success_Returns200_AndUpdatesRide() throws Exception {
        // Given
        Ride ride = persistRide(baseDriver, RideStatus.ONGOING, true, true, 100.0);

        // When & Then
        mockMvc.perform(post("/api/rides/{rideId}/stop", ride.getId())
                        .with(authAsDriver(baseDriver))
                        .contentType("application/json")
                        .content(stopBody(STOP_LAT, STOP_LON, NEW_STOP_ADDR)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.message").value("Ride stopped and finished."))
                .andExpect(jsonPath("$.newDestination.address").value(NEW_STOP_ADDR))
                .andExpect(jsonPath("$.price").isNumber());

        // Then
        Ride updated = rideRepository.findById(ride.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(RideStatus.FINISHED);
        assertThat(updated.getEndedAt()).isNotNull();
        assertThat(updated.getEnd()).isNotNull();
        assertThat(updated.getEnd().getAddress()).isEqualTo(NEW_STOP_ADDR);
        assertThat(updated.getPrice()).isGreaterThan(0);
    }

    @Test
    @DisplayName("404 NOT FOUND: stop ride for unknown rideId")
    void testStopRide_WhenRideNotFound_Returns404() throws Exception {
        // Given
        UUID missingRideId = UUID.randomUUID();

        // When & Then: request stop for missing ride -> 404 + message
        mockMvc.perform(post("/api/rides/{rideId}/stop", missingRideId)
                        .with(authAsDriver(baseDriver))
                        .contentType("application/json")
                        .content(stopBody(LAT, LON, "new address")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Ride not found."));
    }

    @Test
    @DisplayName("409 CONFLICT: stop ride when status is not ONGOING")
    void testStopRide_WhenNotOngoing_Returns409() throws Exception {
        // Given
        Ride ride = persistRide(baseDriver, RideStatus.ASSIGNED, true, true, 100.0);

        // When & Then
        mockMvc.perform(post("/api/rides/{rideId}/stop", ride.getId())
                        .with(authAsDriver(baseDriver))
                        .contentType("application/json")
                        .content(stopBody(LAT, LON, "new address")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Ride is not ongoing."));
    }

    @Test
    @DisplayName("403 FORBIDDEN: stop ride by a driver who is not assigned to this ride")
    void testStopRide_WhenWrongDriver_Returns403() throws Exception {
        // Given
        Driver realDriver = persistDriverWithVehicle("driver-stop-real-" + UUID.randomUUID() + "@example.test");
        Driver otherDriver = persistDriverWithVehicle("driver-stop-other-" + UUID.randomUUID() + "@example.test");
        Ride ride = persistRide(realDriver, RideStatus.ONGOING, true, true, 100.0);

        // When & Then
        mockMvc.perform(post("/api/rides/{rideId}/stop", ride.getId())
                        .with(authAsDriver(otherDriver))
                        .contentType("application/json")
                        .content(stopBody(LAT, LON, "new address")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Only ride driver can stop the ride."));
    }

    @Test
    @DisplayName("400 BAD REQUEST: stop ride without latitude/longitude in request body")
    void testStopRide_WhenMissingCoords_Returns400() throws Exception {
        // Given
        Ride ride = persistRide(baseDriver, RideStatus.ONGOING, true, true, 100.0);

        // When & Then
        mockMvc.perform(post("/api/rides/{rideId}/stop", ride.getId())
                        .with(authAsDriver(baseDriver))
                        .contentType("application/json")
                        .content(bodyWithoutCoords("new address")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Stop location (latitude/longitude) is required."));
    }

    @Test
    @DisplayName("409 CONFLICT: stop ride when ride has no start location")
    void testStopRide_WhenNoStart_Returns409() throws Exception {
        // Given
        Ride ride = persistRide(baseDriver, RideStatus.ONGOING, false, true, 100.0);

        // When & Then
        mockMvc.perform(post("/api/rides/{rideId}/stop", ride.getId())
                        .with(authAsDriver(baseDriver))
                        .contentType("application/json")
                        .content(stopBody(LAT, LON, "new address")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Ride has no start location."));
    }

    @Test
    @DisplayName("403 FORBIDDEN: stop ride without authentication")
    void testStopRide_WithoutAuth_Returns403() throws Exception {
        // Given
        Ride ride = persistRide(baseDriver, RideStatus.ONGOING, true, true, 100.0);

        // When & Then
        mockMvc.perform(post("/api/rides/{rideId}/stop", ride.getId())
                        .contentType("application/json")
                        .content(stopBody(LAT, LON, "new address")))
                .andExpect(status().isForbidden());
    }









    private RequestPostProcessor authAsDriver(Driver driver) {
        // Creates Spring Security Authentication with ROLE_DRIVER
        var auth = new UsernamePasswordAuthenticationToken(
                driver,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_DRIVER"))
        );
        return authentication(auth);
    }

    private String stopBody(double lat, double lon, String address) {
        // JSON body used by stop endpoint
        return """
            {
              "note": "stop now",
              "latitude": %s,
              "longitude": %s,
              "address": "%s"
            }
            """.formatted(lat, lon, address);
    }

    private String bodyWithoutCoords(String address) {
        // Negative case body
        return """
            { "address": "%s" }
            """.formatted(address);
    }

    private Ride persistRide(Driver driver, RideStatus status, boolean withStart, boolean withEnd, double price) {
        // Persists start/end locations optionally and creates a Ride entity
        Location start = null;
        Location end = null;

        if (withStart) {
            start = locationRepository.save(Location.builder()
                    .latitude(LAT).longitude(LON).address(START_ADDR)
                    .build());
        }
        if (withEnd) {
            end = locationRepository.save(Location.builder()
                    .latitude(45.1).longitude(19.1).address(OLD_END_ADDR)
                    .build());
        }

        Ride r = new Ride();
        r.setDriver(driver);
        r.setStatus(status);
        r.setPassengers(List.of());
        r.setStart(start);
        r.setEnd(end);
        r.setPrice(price);
        r.setCanceled(false);

        return rideRepository.save(r);
    }

    private Driver persistDriverWithVehicle(String email) {
        // Persists a driver + vehicle with minimum required fields
        Driver d = new Driver();
        d.setEmail(email);
        d.setPasswordHash("test");
        d.setFirstName("Test");
        d.setLastName("Driver");
        d.setPhone("000");
        d.setAddress("Addr");
        d.setActivated(true);
        d.setBlocked(false);

        d.setAvailable(false);
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

        return (Driver) userRepository.save(d);
    }
}
