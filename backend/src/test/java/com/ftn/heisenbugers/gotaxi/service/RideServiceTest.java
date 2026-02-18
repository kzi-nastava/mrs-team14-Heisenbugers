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
import com.ftn.heisenbugers.gotaxi.models.security.JwtService;
import com.ftn.heisenbugers.gotaxi.models.services.EmailService;
import com.ftn.heisenbugers.gotaxi.repositories.PassengerRepository;
import com.ftn.heisenbugers.gotaxi.repositories.PriceRepository;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import com.ftn.heisenbugers.gotaxi.services.DriverService;
import com.ftn.heisenbugers.gotaxi.services.NotificationService;
import com.ftn.heisenbugers.gotaxi.services.RideService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    private EmailService emailService;
    @Mock
    private DriverService driverService;
    @Mock
    private JwtService jwtService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private RideService rideService;

    private User user;

    @BeforeEach
    public void setup() {
        user = new Passenger();
        user.setFirstName("Passenger");
        user.setLastName("Test");
        user.setEmail("passenger@gmail.com");
    }

    @Test
    @DisplayName("Scheduled ride returns requested status and never sends email")
    void shouldReturnRequestedNoEmail() throws InvalidUserType {
        try (MockedStatic<AuthContextService> mocked = mockStatic(AuthContextService.class)) {
            mocked.when(AuthContextService::getCurrentUser).thenReturn(user);

            CreateRideDTO request = basicRequest();
            request.setScheduledAt(ZonedDateTime.now().plusHours(4).toString());

            when(passengerRepository.findByEmail(any())).thenReturn(Optional.of(new Passenger()));
            when(rideRepository.save(any())).thenAnswer(inv -> {
                Ride r = inv.getArgument(0);
                if (r.getId() == null) r.setId(UUID.randomUUID());
                return r;
            });
            when(priceRepository.getStartingPriceByVehicleType(any())).thenReturn(200.0);

            CreatedRideDTO result = rideService.addRide(request);

            assertEquals(RideStatus.REQUESTED, result.getStatus());
            verify(emailService, never()).sendMail(any(), any(), any());
            verify(driverService, never()).findActiveDrivers();
        }
    }

    @Test
    @DisplayName("Throws RuntimeException when no active drivers exist")
    void shouldThrowRuntimeExceptionWhenNoDrivers() {
        try (MockedStatic<AuthContextService> mocked = mockStatic(AuthContextService.class)) {
            mocked.when(AuthContextService::getCurrentUser).thenReturn(user);

            when(passengerRepository.findByEmail(any())).thenReturn(Optional.of(new Passenger()));
            when(driverService.findActiveDrivers()).thenReturn(Collections.emptyList());
            when(priceRepository.getStartingPriceByVehicleType(any())).thenReturn(200.0);

            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    rideService.addRide(basicRequest())
            );

            assertEquals("There is no free driver available!", exception.getMessage());
        }
    }

    @Test
    @DisplayName("Throws RuntimeException when no driver matches vehicle or transport requirements")
    void shouldThrowRuntimeExceptionWhenVehicleMismatch() {
        try (MockedStatic<AuthContextService> mocked = mockStatic(AuthContextService.class)) {
            mocked.when(AuthContextService::getCurrentUser).thenReturn(user);

            when(passengerRepository.findByEmail(any())).thenReturn(Optional.of(new Passenger()));
            when(driverService.findActiveDrivers()).thenReturn(List.of(mockDriver(true)));
            when(driverService.vehicleMatchesRequest(any(), anyBoolean(), anyBoolean(), any())).thenReturn(false);
            when(priceRepository.getStartingPriceByVehicleType(any())).thenReturn(200.0);

            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    rideService.addRide(basicRequest())
            );

            assertEquals("There is no free driver available!", exception.getMessage());
        }
    }

    @Test
    @DisplayName("Throws RuntimeException when eligible driver cannot accept ride because of work-time limit")
    void shouldThrowRuntimeExceptionWhenNoDriverCanAcceptRide() {
        try (MockedStatic<AuthContextService> mocked = mockStatic(AuthContextService.class)) {
            mocked.when(AuthContextService::getCurrentUser).thenReturn(user);

            when(passengerRepository.findByEmail(any())).thenReturn(Optional.of(new Passenger()));
            when(driverService.findActiveDrivers()).thenReturn(List.of(mockDriver(true)));
            when(driverService.vehicleMatchesRequest(any(), anyBoolean(), anyBoolean(), any())).thenReturn(true);
            when(driverService.canAcceptRide(any(), anyInt())).thenReturn(false);
            when(priceRepository.getStartingPriceByVehicleType(any())).thenReturn(200.0);

            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    rideService.addRide(basicRequest())
            );

            assertEquals("There is no free driver available!", exception.getMessage());
        }
    }

    @Test
    @DisplayName("Returns assigned status when free driver is found")
    void shouldOrderSuccessfulRideReturnsAssigned() throws InvalidUserType {
        try (MockedStatic<AuthContextService> mocked = mockStatic(AuthContextService.class)) {
            mocked.when(AuthContextService::getCurrentUser).thenReturn(user);

            successfulImmediateRide();

            CreatedRideDTO result = rideService.addRide(basicRequest());

            assertEquals(RideStatus.ASSIGNED, result.getStatus());
        }
    }

    @Test
    @DisplayName("Returns assigned status when free driver is found even with multiple stops in route")
    void shouldOrderSuccessfulWithMultipleStops() throws InvalidUserType {
        try (MockedStatic<AuthContextService> mocked = mockStatic(AuthContextService.class)) {
            mocked.when(AuthContextService::getCurrentUser).thenReturn(user);

            successfulImmediateRide();

            CreateRideDTO request = basicRequest();
            request.getRoute().setStops(List.of(
                    new LocationDTO(2, 2, "stopA"),
                    new LocationDTO(3, 3, "stopB")
            ));

            CreatedRideDTO result = rideService.addRide(request);

            assertEquals(RideStatus.ASSIGNED, result.getStatus());
        }
    }

    @Test
    @DisplayName("Sends exactly 2 emails for ride owner and 1 passenger on immediate ride")
    void shouldSend2Emails() throws InvalidUserType {
        try (MockedStatic<AuthContextService> mocked = mockStatic(AuthContextService.class)) {
            mocked.when(AuthContextService::getCurrentUser).thenReturn(user);

            successfulImmediateRide();
            rideService.addRide(basicRequest());

            verify(emailService, times(2)).sendMail(any(), any(), any());
        }
    }

    @Test
    @DisplayName("Sends notification to ride owner")
    void shouldSendNotificationToOwner() throws InvalidUserType {
        try (MockedStatic<AuthContextService> mocked = mockStatic(AuthContextService.class)) {
            mocked.when(AuthContextService::getCurrentUser).thenReturn(user);

            successfulImmediateRide();
            rideService.addRide(basicRequest());

            verify(notificationService, times(1)).notifyUser(eq(user), any(), any(), any());
        }
    }

    @Test
    @DisplayName("Ride price is calculated as distanceKm * 120 + starting price")
    void shouldCalculatePriceCorrectly() throws InvalidUserType {
        try (MockedStatic<AuthContextService> mocked = mockStatic(AuthContextService.class)) {
            mocked.when(AuthContextService::getCurrentUser).thenReturn(user);

            when(passengerRepository.findByEmail(any())).thenReturn(Optional.of(new Passenger()));
            Driver driver = mockDriver(true);
            when(driverService.findActiveDrivers()).thenReturn(List.of(driver));
            when(driverService.vehicleMatchesRequest(any(), anyBoolean(), anyBoolean(), any())).thenReturn(true);
            when(driverService.canAcceptRide(any(), anyInt())).thenReturn(true);
            when(priceRepository.getStartingPriceByVehicleType(any())).thenReturn(200.0);
            when(jwtService.generateToken(any(), any())).thenReturn("token");
            when(rideRepository.save(any())).thenAnswer(inv -> {
                Ride r = inv.getArgument(0);
                if (r.getId() == null) r.setId(UUID.randomUUID());
                return r;
            });

            rideService.addRide(basicRequest());

            ArgumentCaptor<Ride> captor = ArgumentCaptor.forClass(Ride.class);
            verify(rideRepository).save(captor.capture());
            assertEquals(1400.0, captor.getValue().getPrice(), 0.001);
        }
    }

    @Test
    @DisplayName("Unknown passenger email causes new passenger to be persisted")
    void shouldSaveAsNewPassenger() {
        try (MockedStatic<AuthContextService> mocked = mockStatic(AuthContextService.class)) {
            mocked.when(AuthContextService::getCurrentUser).thenReturn(user);

            when(passengerRepository.findByEmail(any())).thenReturn(Optional.empty());
            when(driverService.findActiveDrivers()).thenReturn(Collections.emptyList());
            when(priceRepository.getStartingPriceByVehicleType(any())).thenReturn(200.0);

            assertThrows(RuntimeException.class, () -> rideService.addRide(basicRequest()));

            verify(passengerRepository, times(1)).save(any(Passenger.class));
        }
    }

    @Test
    @DisplayName("No passengers in request so no passenger lookup performed")
    void shouldHaveNoPassengerLookup() {
        try (MockedStatic<AuthContextService> mocked = mockStatic(AuthContextService.class)) {
            mocked.when(AuthContextService::getCurrentUser).thenReturn(user);

            CreateRideDTO request = basicRequest();
            request.setPassengersEmails(Collections.emptyList());

            when(driverService.findActiveDrivers()).thenReturn(Collections.emptyList());
            when(priceRepository.getStartingPriceByVehicleType(any())).thenReturn(200.0);

            assertThrows(RuntimeException.class, () -> rideService.addRide(request));

            verify(passengerRepository, never()).findByEmail(any());
        }
    }

    @Test
    @DisplayName("Nearest free driver is selected when multiple free drivers exist")
    void shouldSelectNearestFreeDriver() {
        Ride ride = rideWithStart(0, 0);

        Driver far = mockDriver(true);
        far.setLocation(new Location(10, 10, "far"));
        Driver near = mockDriver(true);
        near.setLocation(new Location(1, 1, "near"));

        when(driverService.findActiveDrivers()).thenReturn(List.of(far, near));
        when(driverService.vehicleMatchesRequest(any(), anyBoolean(), anyBoolean(), any())).thenReturn(true);
        when(driverService.canAcceptRide(any(), anyInt())).thenReturn(true);

        Optional<Driver> result = rideService.assignDriverToRide(ride, false, false, VehicleType.STANDARD);

        assertTrue(result.isPresent());
        assertEquals("near", result.get().getLocation().getAddress());
    }

    @Test
    @DisplayName("Only free driver returned")
    void shouldReturnSingleFreeDriver() {
        Ride ride = rideWithStart(0, 0);
        Driver driver = mockDriver(true);
        driver.setLocation(new Location(5, 5, "only"));

        when(driverService.findActiveDrivers()).thenReturn(List.of(driver));
        when(driverService.vehicleMatchesRequest(any(), anyBoolean(), anyBoolean(), any())).thenReturn(true);
        when(driverService.canAcceptRide(any(), anyInt())).thenReturn(true);

        Optional<Driver> result = rideService.assignDriverToRide(ride, false, false, VehicleType.STANDARD);

        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Busy driver whose ride ends near the new start is selected")
    void shouldSelectDriverWithRideEndingSoon() {
        Ride newRide = rideWithStart(0, 0);

        Driver busy = mockDriver(false);
        busy.setId(UUID.randomUUID());

        Ride activeRide = new Ride();
        activeRide.setEnd(new Location(1, 1, "nearEnd"));

        when(driverService.findActiveDrivers()).thenReturn(List.of(busy));
        when(driverService.vehicleMatchesRequest(any(), anyBoolean(), anyBoolean(), any())).thenReturn(true);
        when(driverService.canAcceptRide(any(), anyInt())).thenReturn(true);
        when(driverService.endsRideWithin(any(), anyInt())).thenReturn(true);

        Optional<Driver> result = rideService.assignDriverToRide(newRide, false, false, VehicleType.STANDARD);

        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Returns empty when no active drivers exist")
    void shouldReturnsEmptyWhenNoActiveDrivers() {
        when(driverService.findActiveDrivers()).thenReturn(Collections.emptyList());

        Optional<Driver> result = rideService.assignDriverToRide(
                rideWithStart(0, 0), false, false, VehicleType.STANDARD);

        assertTrue(result.isEmpty());
    }


    @Test
    @DisplayName("Returns empty when all eligible drivers are busy and none ends within 10 min")
    void shouldReturnEmptyWhenAllBusyNoneEndingSoon() {
        Driver busy = mockDriver(false);

        when(driverService.findActiveDrivers()).thenReturn(List.of(busy));
        when(driverService.vehicleMatchesRequest(any(), anyBoolean(), anyBoolean(), any())).thenReturn(true);
        when(driverService.canAcceptRide(any(), anyInt())).thenReturn(true);
        when(driverService.endsRideWithin(any(), anyInt())).thenReturn(false);

        Optional<Driver> result = rideService.assignDriverToRide(
                rideWithStart(0, 0), false, false, VehicleType.STANDARD);

        assertTrue(result.isEmpty());
    }

    private CreateRideDTO basicRequest() {
        LocationDTO start = new LocationDTO(0, 0, "start");
        LocationDTO end = new LocationDTO(5, 5, "end");

        RouteDTO route = new RouteDTO();
        route.setStart(start);
        route.setDestination(end);
        route.setStops(new ArrayList<>());
        route.setDistanceKm(10);
        route.setEstimatedTimeMin(15);

        CreateRideDTO dto = new CreateRideDTO();
        dto.setRoute(route);
        dto.setVehicleType(VehicleType.STANDARD);
        dto.setPassengersEmails(new ArrayList<>(List.of("p@gmail.com")));
        dto.setPetTransport(false);
        dto.setBabyTransport(false);
        return dto;
    }

    private Ride rideWithStart(double lat, double lng) {
        Ride ride = new Ride();
        ride.setRoute(new Route());
        ride.getRoute().setEstimatedTimeMin(15);
        ride.setStart(new Location(lat, lng, "start"));
        return ride;
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
        driver.setFirstName("Driver");
        driver.setLastName("Test");
        return driver;
    }

    private void successfulImmediateRide() {
        when(passengerRepository.findByEmail(any())).thenReturn(Optional.of(new Passenger()));
        when(driverService.findActiveDrivers()).thenReturn(List.of(mockDriver(true)));
        when(driverService.vehicleMatchesRequest(any(), anyBoolean(), anyBoolean(), any())).thenReturn(true);
        when(driverService.canAcceptRide(any(), anyInt())).thenReturn(true);
        when(priceRepository.getStartingPriceByVehicleType(any())).thenReturn(200.0);
        when(jwtService.generateToken(any(), any())).thenReturn("token");
        when(rideRepository.save(any())).thenAnswer(inv -> {
            Ride r = inv.getArgument(0);
            if (r.getId() == null) r.setId(UUID.randomUUID());
            return r;
        });
    }

    // =======================================================
    // Tests for finish method
    // =======================================================

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
    @DisplayName("finish: deletes passenger records that were created as placeholders (firstName == \"\")")
    void finishDeletesPlaceholderPassengers() {
        UUID rideId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        Driver driver = new Driver();
        driver.setId(driverId);
        driver.setAvailable(false);

        Ride ride = new Ride();
        ride.setId(rideId);
        ride.setStatus(RideStatus.ONGOING);
        ride.setDriver(driver);
        ride.setStart(new Location(0, 0, "A"));
        ride.setEnd(new Location(1, 1, "B"));

        Passenger placeholder = new Passenger();
        placeholder.setFirstName("/"); // triggers delete
        placeholder.setLastName("/");
        placeholder.setEmail("placeholder@example.com");

        Passenger normal = new Passenger();
        normal.setFirstName("Real");
        normal.setLastName("User");
        normal.setEmail("real@example.com");

        ride.setPassengers(List.of(placeholder, normal));

        when(userRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        boolean ok = rideService.finish(rideId, driverId);

        assertTrue(ok);

        verify(emailService, times(2)).sendMail(any(), any(), any());
        verify(passengerRepository, times(1)).delete(placeholder);
        verify(passengerRepository, never()).delete(normal);
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
}
