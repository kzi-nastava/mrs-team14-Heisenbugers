package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Location;
import com.ftn.heisenbugers.gotaxi.models.Passenger;
import com.ftn.heisenbugers.gotaxi.models.Vehicle;
import com.ftn.heisenbugers.gotaxi.models.dtos.CreateRideDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.LocationDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.RouteDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import com.ftn.heisenbugers.gotaxi.models.services.EmailService;
import com.ftn.heisenbugers.gotaxi.repositories.LocationRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CreateRideControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    EmailService emailService;

    @Test
    @DisplayName("Should create ride when making POST request to /api/rides")
    void shouldCreateRideSuccessfully() throws Exception {
        Passenger passenger = persistPassenger("passenger1@example.test");
        persistDriverWithVehicle("driver1@example.test");

        LocationDTO start = new LocationDTO(19.795411, 45.251058, "Dusana Danilovica 3, Novi Sad");
        LocationDTO destination = new LocationDTO(19.824706, 45.247205, "Laze Nancica 1, Novi Sad");

        RouteDTO route = new RouteDTO();
        route.setDistanceKm(3.0);
        route.setEstimatedTimeMin(5);
        route.setPolyline("encodedPolyline");
        route.setStart(start);
        route.setDestination(destination);
        route.setStops(List.of());

        CreateRideDTO request = new CreateRideDTO();
        request.setRoute(route);
        request.setVehicleType(VehicleType.STANDARD);
        request.setBabyTransport(false);
        request.setPetTransport(false);
        request.setPassengersEmails(List.of("passenger1@gmail.com"));

        mockMvc.perform(post("/api/rides")
                        .with(authAsPassenger(passenger))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.route.distanceKm").value(3.0))
                .andExpect(jsonPath("$.route.estimatedTimeMin").value(5))
                .andExpect(jsonPath("$.route.start.address").value("Dusana Danilovica 3, Novi Sad"))
                .andExpect(jsonPath("$.route.destination.address").value("Laze Nancica 1, Novi Sad"))
                .andExpect(jsonPath("$.vehicleType").value("STANDARD"))
                .andExpect(jsonPath("$.babyTransport").value(false))
                .andExpect(jsonPath("$.petTransport").value(false))
                .andExpect(jsonPath("$.passengersEmails[0]").value("passenger1@gmail.com"))
                .andExpect(jsonPath("$.status").value("ASSIGNED"));
    }

    @Test
    @DisplayName("Should schedule ride when making POST request to /api/rides")
    void shouldScheduleRideSuccessfully() throws Exception {
        Passenger passenger = persistPassenger("passenger2@example.test");

        LocationDTO start = new LocationDTO(19.795411, 45.251058, "Dusana Danilovica 3, Novi Sad");
        LocationDTO destination = new LocationDTO(19.824706, 45.247205, "Laze Nancica 1, Novi Sad");

        RouteDTO route = new RouteDTO();
        route.setDistanceKm(3.0);
        route.setEstimatedTimeMin(5);
        route.setPolyline("encodedPolyline");
        route.setStart(start);
        route.setDestination(destination);
        route.setStops(List.of());

        CreateRideDTO request = new CreateRideDTO();
        request.setRoute(route);
        request.setVehicleType(VehicleType.STANDARD);
        request.setBabyTransport(false);
        request.setPetTransport(false);
        request.setPassengersEmails(List.of("passenger1@gmail.com"));
        request.setScheduledAt("2026-02-14T16:45:00.000Z");

        mockMvc.perform(post("/api/rides")
                        .with(authAsPassenger(passenger))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.route.distanceKm").value(3.0))
                .andExpect(jsonPath("$.route.estimatedTimeMin").value(5))
                .andExpect(jsonPath("$.route.start.address").value("Dusana Danilovica 3, Novi Sad"))
                .andExpect(jsonPath("$.route.destination.address").value("Laze Nancica 1, Novi Sad"))
                .andExpect(jsonPath("$.vehicleType").value("STANDARD"))
                .andExpect(jsonPath("$.babyTransport").value(false))
                .andExpect(jsonPath("$.petTransport").value(false))
                .andExpect(jsonPath("$.passengersEmails[0]").value("passenger1@gmail.com"))
                .andExpect(jsonPath("$.status").value("REQUESTED"));
    }

    @Test
    @DisplayName("Should fail ride creation when no driver is available when making POST request to /api/rides")
    void shouldFailRideCreationWhenNoDriver() throws Exception {
        Passenger passenger = persistPassenger("passenger3@example.test");

        LocationDTO start = new LocationDTO(19.795411, 45.251058, "Dusana Danilovica 3, Novi Sad");
        LocationDTO destination = new LocationDTO(19.824706, 45.247205, "Laze Nancica 1, Novi Sad");

        RouteDTO route = new RouteDTO();
        route.setDistanceKm(3.0);
        route.setEstimatedTimeMin(5);
        route.setPolyline("encodedPolyline");
        route.setStart(start);
        route.setDestination(destination);
        route.setStops(List.of());

        CreateRideDTO request = new CreateRideDTO();
        request.setRoute(route);
        request.setVehicleType(VehicleType.STANDARD);
        request.setBabyTransport(false);
        request.setPetTransport(false);
        request.setPassengersEmails(List.of("passenger1@gmail.com"));

        mockMvc.perform(post("/api/rides")
                        .with(authAsPassenger(passenger))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("There is no free driver available!"));
    }

    @Test
    @DisplayName("Should fail ride creation when no autorization is present when making POST request to /api/rides")
    void shouldFailRideCreationWhenNoAuthorization() throws Exception {
        persistPassenger("passenger4@example.test");

        LocationDTO start = new LocationDTO(19.795411, 45.251058, "Dusana Danilovica 3, Novi Sad");
        LocationDTO destination = new LocationDTO(19.824706, 45.247205, "Laze Nancica 1, Novi Sad");

        RouteDTO route = new RouteDTO();
        route.setDistanceKm(3.0);
        route.setEstimatedTimeMin(5);
        route.setPolyline("encodedPolyline");
        route.setStart(start);
        route.setDestination(destination);
        route.setStops(List.of());

        CreateRideDTO request = new CreateRideDTO();
        request.setRoute(route);
        request.setVehicleType(VehicleType.STANDARD);
        request.setBabyTransport(false);
        request.setPetTransport(false);
        request.setPassengersEmails(List.of("passenger1@gmail.com"));

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }


    private RequestPostProcessor authAsPassenger(Passenger passenger) {
        var auth = new UsernamePasswordAuthenticationToken(
                passenger,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_PASSENGER"))
        );
        return authentication(auth);
    }

    private Passenger persistPassenger(String email) {
        Passenger p = new Passenger();
        p.setEmail(email);
        p.setPasswordHash("testpass123");
        p.setFirstName("Test");
        p.setLastName("Passenger");
        p.setPhone("000000000");
        p.setAddress("Test Address 1");
        p.setActivated(true);
        p.setBlocked(false);

        return userRepository.save(p);
    }

    private void persistDriverWithVehicle(String email) {
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
        v.setSeatCount(5);
        v.setBabyTransport(false);
        v.setPetTransport(false);

        d.setVehicle(v);
        v.setDriver(d);

        userRepository.save(d);
    }
}
