package com.ftn.heisenbugers.gotaxi.service;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.*;
import com.ftn.heisenbugers.gotaxi.models.dtos.CreateRideDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.CreatedRideDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.LocationDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.RouteDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.models.services.EmailService;
import com.ftn.heisenbugers.gotaxi.repositories.*;
import com.ftn.heisenbugers.gotaxi.services.DriverService;
import com.ftn.heisenbugers.gotaxi.services.RideService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RideServiceTest {

    @Mock
    private RideRepository rideRepository;
    @Mock private UserRepository userRepository;
    @Mock private PassengerRepository passengerRepository;
    @Mock private TrafficViolationRepository violationRepository;
    @Mock private RatingRepository ratingRepository;
    @Mock private EmailService emailService;
    @Mock private DriverService driverService;

    @InjectMocks
    private RideService rideService;

    private User user;

    @BeforeEach
    public void setup(){
        user = new Passenger();
        user.setFirstName("Marko");
        user.setLastName("Markovic");
        user.setEmail("marko@gmail.com");
    }

    @Test
    public void testScheduledRide() throws InvalidUserType {

        try (MockedStatic<AuthContextService> mocked =
                     mockStatic(AuthContextService.class)) {

            mocked.when(AuthContextService::getCurrentUser)
                    .thenReturn(user);

            CreateRideDTO request = basicRequest();
            request.setScheduledAt(ZonedDateTime.now().plusDays(1).toString());

            when(passengerRepository.findByEmail(any()))
                    .thenReturn(Optional.of(new Passenger()));

            when(rideRepository.save(any()))
                    .thenAnswer(inv -> inv.getArgument(0));

            CreatedRideDTO result = rideService.addRide(request);

            assertEquals(RideStatus.REQUESTED.toString(), result.getStatus().toString());
            verify(emailService, never()).sendMail(any(), any(), any());
        }
    }

    @Test
    public void testNoActiveDrivers() throws InvalidUserType {

        try (MockedStatic<AuthContextService> mocked =
                     mockStatic(AuthContextService.class)) {

            mocked.when(AuthContextService::getCurrentUser).thenReturn(user);

            CreateRideDTO request = basicRequest();

            when(passengerRepository.findByEmail(any()))
                    .thenReturn(Optional.of(new Passenger()));

            when(driverService.findActiveDrivers())
                    .thenReturn(Collections.emptyList());

            assertThrows(RuntimeException.class, () -> {
                rideService.addRide(request);
            });
        }
    }

    @Test
    public void testDriversNotEligible() throws RuntimeException {

        try (MockedStatic<AuthContextService> mocked =
                     mockStatic(AuthContextService.class)) {

            mocked.when(AuthContextService::getCurrentUser).thenReturn(user);

            CreateRideDTO request = basicRequest();

            when(passengerRepository.findByEmail(any()))
                    .thenReturn(Optional.of(new Passenger()));

            Driver driver = mockDriver(true);

            when(driverService.findActiveDrivers())
                    .thenReturn(List.of(driver));

            when(driverService.vehicleMatchesRequest(any(), anyBoolean(), anyBoolean(), any()))
                    .thenReturn(false);

            assertThrows(RuntimeException.class, () -> {
                rideService.addRide(request);
            });
        }
    }

    @Test
    public void testAssignNearestFreeDriver() {

        Ride ride = new Ride();
        ride.setRoute(new Route());
        ride.setStart(new Location(0, 0, "start"));

        Driver d1 = mockDriver(true);
        d1.setLocation(new Location(10, 10, "far"));

        Driver d2 = mockDriver(true);
        d2.setLocation(new Location(1, 1, "near"));

        when(driverService.findActiveDrivers())
                .thenReturn(List.of(d1, d2));

        when(driverService.vehicleMatchesRequest(any(), anyBoolean(), anyBoolean(), any()))
                .thenReturn(true);

        when(driverService.canAcceptRide(any(), anyInt()))
                .thenReturn(true);

        Optional<Driver> result =
                rideService.assignDriverToRide(
                        ride, false, false, VehicleType.STANDARD);

        assertTrue(result.isPresent());
        assertEquals("near", result.get().getLocation().getAddress());
    }

    @Test
    public void testBusyDriverEndingSoonSelected() {

        Ride newRide = new Ride();
        newRide.setRoute(new Route());
        newRide.setStart(new Location(0, 0, "start"));

        Driver busy = mockDriver(false);
        busy.setId(UUID.randomUUID());
        busy.setLocation(new Location(5, 5, "busy"));

        Ride currentRide = new Ride();
        currentRide.setEnd(new Location(1, 1, "nearEnd"));

        when(driverService.findActiveDrivers())
                .thenReturn(List.of(busy));

        when(driverService.vehicleMatchesRequest(any(), anyBoolean(), anyBoolean(), any()))
                .thenReturn(true);

        when(driverService.canAcceptRide(any(), anyInt()))
                .thenReturn(true);

        when(driverService.endsRideWithin(any(), anyInt()))
                .thenReturn(true);

        Optional<Driver> result =
                rideService.assignDriverToRide(
                        newRide, false, false, VehicleType.STANDARD);

        assertTrue(result.isPresent());
    }

    @Test
    public void testPassengerNotFound() throws NoSuchElementException {

        try (MockedStatic<AuthContextService> mocked =
                     mockStatic(AuthContextService.class)) {

            mocked.when(AuthContextService::getCurrentUser).thenReturn(user);

            CreateRideDTO request = basicRequest();

            when(passengerRepository.findByEmail(any()))
                    .thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> {
                rideService.addRide(request);
            });
        }
    }

    @Test
    public void testSuccessfulImmediateRide() {

        try (MockedStatic<AuthContextService> mocked =
                     mockStatic(AuthContextService.class)) {

            mocked.when(AuthContextService::getCurrentUser).thenReturn(user);

            CreateRideDTO request = basicRequest();

            when(passengerRepository.findByEmail(any()))
                    .thenReturn(Optional.of(new Passenger()));

            Driver driver = mockDriver(true);

            when(driverService.findActiveDrivers())
                    .thenReturn(List.of(driver));

            when(driverService.vehicleMatchesRequest(any(), anyBoolean(), anyBoolean(), any()))
                    .thenReturn(true);

            when(driverService.canAcceptRide(any(), anyInt()))
                    .thenReturn(true);

            when(rideRepository.save(any()))
                    .thenAnswer(inv -> inv.getArgument(0));

            CreatedRideDTO result = rideService.addRide(request);

            assertEquals(RideStatus.ASSIGNED, result.getStatus());
            verify(emailService, times(1))
                    .sendMail(any(), any(), any());
        } catch (InvalidUserType e) {
            throw new RuntimeException(e);
        }
    }

    private CreateRideDTO basicRequest() {

        LocationDTO start = new LocationDTO(0, 0, "start");
        LocationDTO end = new LocationDTO(5, 5, "end");

        RouteDTO route = new RouteDTO();
        route.setStart(start);
        route.setDestination(end);
        route.setStops(List.of());
        route.setDistanceKm(10);
        route.setEstimatedTimeMin(15);

        CreateRideDTO dto = new CreateRideDTO();
        dto.setRoute(route);
        dto.setVehicleType(VehicleType.STANDARD);
        dto.setPassengersEmails(List.of("p@gmail.com"));
        dto.setPetTransport(false);
        dto.setBabyTransport(false);

        return dto;
    }

    private Driver mockDriver(boolean available) {

        Vehicle vehicle = new Vehicle();
        vehicle.setType(VehicleType.STANDARD);
        vehicle.setPetTransport(false);
        vehicle.setBabyTransport(false);

        Driver driver = new Driver();
        driver.setAvailable(available);
        driver.setVehicle(vehicle);
        driver.setLocation(new Location(0, 0, "driver"));

        return driver;
    }

}
