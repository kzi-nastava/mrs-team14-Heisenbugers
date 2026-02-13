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
import org.mockito.ArgumentCaptor;
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
    @Mock
    private UserRepository userRepository;
    @Mock
    private PassengerRepository passengerRepository;
    @Mock
    private TrafficViolationRepository violationRepository;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private DriverService driverService;

    @InjectMocks
    private RideService rideService;

    private User user;

    @BeforeEach
    public void setup() {
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

    @Test
    public void testFinishReturnsFalseWhenRideNotOngoing() {
        UUID rideId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        Driver driver = new Driver();
        driver.setId(driverId);

        Driver rideDriver = new Driver();
        rideDriver.setId(driverId); // even if same identity, status should fail
        rideDriver.setAvailable(false);

        Ride ride = new Ride();
        ride.setId(rideId);
        ride.setStatus(RideStatus.ASSIGNED); // not ONGOING
        ride.setDriver(rideDriver);
        ride.setPassengers(List.of(new Passenger(), new Passenger()));

        when(userRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        boolean ok = rideService.finish(rideId, driverId);

        assertFalse(ok);
        verify(rideRepository, never()).save(any(Ride.class));
        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendMail(any(), any(), any());
    }

    @Test
    public void testFinishReturnsFalseWhenDriverIsNotRideDriver() {
        UUID rideId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        Driver callerDriver = new Driver();
        callerDriver.setId(driverId);

        Driver rideDriver = new Driver();
        rideDriver.setId(UUID.randomUUID()); // different instance & id
        rideDriver.setAvailable(false);

        Ride ride = new Ride();
        ride.setId(rideId);
        ride.setStatus(RideStatus.ONGOING);
        ride.setDriver(rideDriver);
        ride.setPassengers(List.of(new Passenger()));

        when(userRepository.findById(driverId)).thenReturn(Optional.of(callerDriver));
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        boolean ok = rideService.finish(rideId, driverId);

        assertFalse(ok);
        verify(rideRepository, never()).save(any(Ride.class));
        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendMail(any(), any(), any());
    }

    @Test
    public void testFinishSuccessUpdatesRideDriverSendsEmails() {
        UUID rideId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        Driver driver = new Driver();
        driver.setId(driverId);
        driver.setAvailable(false);

        Ride ride = new Ride();
        ride.setId(rideId);
        ride.setStatus(RideStatus.ONGOING);
        ride.setDriver(driver);

        Passenger p1 = new Passenger();
        p1.setFirstName("Pera");
        p1.setLastName("Peric");
        p1.setEmail("p1@example.com");

        Passenger p2 = new Passenger();
        p2.setFirstName("Mika");
        p2.setLastName("Mikic");
        p2.setEmail("p2@example.com");

        ride.setPassengers(List.of(p1, p2));
        ride.setStart(new Location(0, 0, "A"));
        ride.setEnd(new Location(1, 1, "B"));

        when(userRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        boolean ok = rideService.finish(rideId, driverId);

        assertTrue(ok);

        assertEquals(RideStatus.FINISHED, ride.getStatus());
        assertNotNull(ride.getEndedAt());
        assertEquals(driver, ride.getLastModifiedBy());
        assertTrue(driver.isAvailable());

        verify(rideRepository, times(1)).save(ride);
        verify(userRepository, times(1)).save(driver);

        ArgumentCaptor<String> recipientCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService, times(2)).sendMail(recipientCaptor.capture(), any(), any());

        List<String> recipients = recipientCaptor.getAllValues();
        assertTrue(recipients.contains("p1@example.com"));
        assertTrue(recipients.contains("p2@example.com"));
    }

    @Test
    public void testFinishSuccessWithNoPassengersDoesNotSendEmails() {
        UUID rideId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        Driver driver = new Driver();
        driver.setId(driverId);
        driver.setAvailable(false);

        Ride ride = new Ride();
        ride.setId(rideId);
        ride.setStatus(RideStatus.ONGOING);
        ride.setDriver(driver);
        ride.setPassengers(Collections.emptyList());
        ride.setStart(new Location(0, 0, "A"));
        ride.setEnd(new Location(1, 1, "B"));

        when(userRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        boolean ok = rideService.finish(rideId, driverId);

        assertTrue(ok);
        verify(emailService, never()).sendMail(any(), any(), any());
    }

    @Test
    public void testFinishThrowsWhenRideNotFound() {
        UUID rideId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        when(userRepository.findById(driverId)).thenReturn(Optional.of(new Driver()));
        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> rideService.finish(rideId, driverId));
    }

    @Test
    public void testFinishThrowsWhenDriverNotFound() {
        UUID rideId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        when(userRepository.findById(driverId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> rideService.finish(rideId, driverId));
        verify(rideRepository, never()).findById(any());
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
