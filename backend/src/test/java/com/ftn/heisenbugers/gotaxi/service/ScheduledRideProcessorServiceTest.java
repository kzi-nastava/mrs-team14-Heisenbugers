package com.ftn.heisenbugers.gotaxi.service;

import com.ftn.heisenbugers.gotaxi.models.*;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import com.ftn.heisenbugers.gotaxi.models.security.JwtService;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.services.NotificationService;
import com.ftn.heisenbugers.gotaxi.services.RideService;
import com.ftn.heisenbugers.gotaxi.services.ScheduledRideProcessorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduledRideProcessorServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private RideService rideService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private ScheduledRideProcessorService scheduledRideProcessorService;

    private Passenger passenger;
    private Driver driver;
    private Vehicle vehicle;
    private Route route;

    @BeforeEach
    public void setup() {
        passenger = new Passenger();
        passenger.setId(UUID.randomUUID());
        passenger.setEmail("passenger@test.com");
        passenger.setFirstName("Passenger");
        passenger.setLastName("Test");

        vehicle = new Vehicle();
        vehicle.setType(VehicleType.STANDARD);
        vehicle.setPetTransport(false);
        vehicle.setBabyTransport(false);

        driver = new Driver();
        driver.setId(UUID.randomUUID());
        driver.setEmail("driver@test.com");
        driver.setFirstName("Driver");
        driver.setLastName("Test");
        driver.setVehicle(vehicle);
        driver.setAvailable(true);

        route = new Route();
        route.setUser(passenger);
        route.setStart(new Location(0, 0, "Start Address"));
        route.setDestination(new Location(1, 1, "End Address"));
        route.setEstimatedTimeMin(30);
    }

    @Test
    @DisplayName("Should do nothing when no scheduled rides are found")
    public void shouldReturnNoRidesFound() {
        when(rideRepository.findByStatusAndScheduledAtAfter(any(RideStatus.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        scheduledRideProcessorService.processScheduledRides();

        verify(rideRepository, times(1))
                .findByStatusAndScheduledAtAfter(eq(RideStatus.REQUESTED), any(LocalDateTime.class));
        verify(rideService, never()).assignDriverToRide(any(), anyBoolean(), anyBoolean(), any());
        verify(notificationService, never()).notifyUser(any(), any(), (Ride) any());
    }

    @Test
    @DisplayName("Should assign driver to ride scheduled in 14 minutes with no driver assigned")
    public void shouldAssignDriverExactly14MinutesBefore() {
        LocalDateTime scheduledTime = LocalDateTime.now().plusMinutes(14);
        Ride ride = createRide(scheduledTime, null);

        when(rideRepository.findByStatusAndScheduledAtAfter(any(RideStatus.class), any(LocalDateTime.class)))
                .thenReturn(List.of(ride));
        when(rideService.assignDriverToRide(any(), anyBoolean(), anyBoolean(), any()))
                .thenReturn(Optional.of(driver));
        when(jwtService.generateToken(anyString(), any())).thenReturn("test-token");

        scheduledRideProcessorService.processScheduledRides();

        verify(rideService, times(1))
                .assignDriverToRide(eq(ride), eq(false), eq(false), eq(VehicleType.STANDARD));
        verify(rideRepository, times(1)).save(ride);
        assertEquals(RideStatus.ASSIGNED, ride.getStatus());
        assertEquals(driver, ride.getDriver());
        assertEquals(vehicle, ride.getVehicle());
        assertFalse(driver.isAvailable());
        verify(rideService, times(2)).sendAcceptedRideEmail(any(), eq(ride), eq("test-token"));
    }

    @Test
    @DisplayName("Should assign driver to ride scheduled in 15 minutes with no driver assigned")
    public void shouldAssignDriverExactly15MinutesBefore() {
        LocalDateTime scheduledTime = LocalDateTime.now().plusMinutes(15);
        Ride ride = createRide(scheduledTime, null);

        when(rideRepository.findByStatusAndScheduledAtAfter(any(RideStatus.class), any(LocalDateTime.class)))
                .thenReturn(List.of(ride));
        when(rideService.assignDriverToRide(any(), anyBoolean(), anyBoolean(), any()))
                .thenReturn(Optional.of(driver));
        when(jwtService.generateToken(anyString(), any())).thenReturn("test-token");

        scheduledRideProcessorService.processScheduledRides();

        verify(rideService, times(1))
                .assignDriverToRide(eq(ride), eq(false), eq(false), eq(VehicleType.STANDARD));
        verify(rideRepository, times(1)).save(ride);
        assertEquals(RideStatus.ASSIGNED, ride.getStatus());
    }

    @Test
    @DisplayName("Should not assign driver to ride scheduled in 16 minutes")
    public void shouldNotAssignDriverIfEarly() {
        LocalDateTime scheduledTime = LocalDateTime.now().plusMinutes(16);
        Ride ride = createRide(scheduledTime, null);

        when(rideRepository.findByStatusAndScheduledAtAfter(any(RideStatus.class), any(LocalDateTime.class)))
                .thenReturn(List.of(ride));

        scheduledRideProcessorService.processScheduledRides();

        verify(rideService, never()).assignDriverToRide(any(), anyBoolean(), anyBoolean(), any());
        verify(rideRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should do nothing if ride scheduled in 14 minutes but driver already assigned")
    public void shouldDoNothingAsDriverAlreadyAssigned() {
        LocalDateTime scheduledTime = LocalDateTime.now().plusMinutes(14);
        Ride ride = createRide(scheduledTime, driver);

        when(rideRepository.findByStatusAndScheduledAtAfter(any(RideStatus.class), any(LocalDateTime.class)))
                .thenReturn(List.of(ride));

        scheduledRideProcessorService.processScheduledRides();

        verify(rideService, never()).assignDriverToRide(any(), anyBoolean(), anyBoolean(), any());
        verify(rideRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should do nothing if ride scheduled in 14 minutes but there is no driver available")
    public void shouldDoNothingAsNoDriverAvailable() {
        LocalDateTime scheduledTime = LocalDateTime.now().plusMinutes(14);
        Ride ride = createRide(scheduledTime, null);

        when(rideRepository.findByStatusAndScheduledAtAfter(any(RideStatus.class), any(LocalDateTime.class)))
                .thenReturn(List.of(ride));
        when(rideService.assignDriverToRide(any(), anyBoolean(), anyBoolean(), any()))
                .thenReturn(Optional.empty());

        scheduledRideProcessorService.processScheduledRides();

        verify(rideService, times(1)).assignDriverToRide(any(), anyBoolean(), anyBoolean(), any());
        verify(rideRepository, never()).save(any());
        assertNull(ride.getDriver());
        assertEquals(RideStatus.REQUESTED, ride.getStatus());
    }

    @Test
    @DisplayName("Should send reminder 15 minutes before scheduled time")
    public void shouldSendReminderAt15Minutes() {
        LocalDateTime scheduledTime = LocalDateTime.now().plusSeconds(930);
        Ride ride = createRide(scheduledTime, driver);

        when(rideRepository.findByStatusAndScheduledAtAfter(any(RideStatus.class), any(LocalDateTime.class)))
                .thenReturn(List.of(ride));

        scheduledRideProcessorService.processScheduledRides();

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationService, times(1))
                .notifyUser(eq(passenger), messageCaptor.capture(), eq(ride));

        String capturedMessage = messageCaptor.getValue();
        assertTrue(capturedMessage.contains("Reminder: You have a scheduled ride at"));
    }

    @Test
    @DisplayName("Should send reminder 10 minutes before scheduled time")
    public void shouldSendReminderAt10Minutes() {
        LocalDateTime scheduledTime = LocalDateTime.now().plusSeconds(630);
        Ride ride = createRide(scheduledTime, driver);

        when(rideRepository.findByStatusAndScheduledAtAfter(any(RideStatus.class), any(LocalDateTime.class)))
                .thenReturn(List.of(ride));

        scheduledRideProcessorService.processScheduledRides();

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationService, times(1))
                .notifyUser(eq(passenger), messageCaptor.capture(), eq(ride));

        assertTrue(messageCaptor.getValue().contains("Reminder"));
    }

    @Test
    @DisplayName("Should send reminder 5 minutes before scheduled time")
    public void shouldSendReminderAt5Minutes() {
        LocalDateTime scheduledTime = LocalDateTime.now().plusSeconds(330);
        Ride ride = createRide(scheduledTime, driver);

        when(rideRepository.findByStatusAndScheduledAtAfter(any(RideStatus.class), any(LocalDateTime.class)))
                .thenReturn(List.of(ride));

        scheduledRideProcessorService.processScheduledRides();

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationService, times(1))
                .notifyUser(eq(passenger), messageCaptor.capture(), eq(ride));

        assertTrue(messageCaptor.getValue().contains("Reminder"));
    }

    @Test
    @DisplayName("Should not send reminder at 12 minutes (not divisible by 5)")
    public void shouldNotSendReminderAt12Minutes() {
        LocalDateTime scheduledTime = LocalDateTime.now().plusMinutes(12);
        Ride ride = createRide(scheduledTime, driver);

        when(rideRepository.findByStatusAndScheduledAtAfter(any(RideStatus.class), any(LocalDateTime.class)))
                .thenReturn(List.of(ride));

        scheduledRideProcessorService.processScheduledRides();

        verify(notificationService, never()).notifyUser(any(), any(), (Ride) any());
    }

    @Test
    @DisplayName("Should notify user at ride time if no driver assigned")
    public void shouldNotifyNoDriverAtRideTime() {
        LocalDateTime scheduledTime = LocalDateTime.now().plusSeconds(30);
        Ride ride = createRide(scheduledTime, null);

        when(rideRepository.findByStatusAndScheduledAtAfter(any(RideStatus.class), any(LocalDateTime.class)))
                .thenReturn(List.of(ride));

        scheduledRideProcessorService.processScheduledRides();

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationService, times(2))
                .notifyUser(eq(passenger), messageCaptor.capture(), eq(ride));

        String capturedMessage = messageCaptor.getValue();
        assertTrue(capturedMessage.contains("Unfortunately there is no free driver for your ride!"));
    }

    @Test
    @DisplayName("Should not notify user at ride time if driver assigned")
    public void shouldNotNotifyIfDriverAssigned() {
        LocalDateTime scheduledTime = LocalDateTime.now();
        Ride ride = createRide(scheduledTime, driver);

        when(rideRepository.findByStatusAndScheduledAtAfter(any(RideStatus.class), any(LocalDateTime.class)))
                .thenReturn(List.of(ride));

        scheduledRideProcessorService.processScheduledRides();

        verify(notificationService, never()).notifyUser(any(), any(),(Ride) any());
    }


    private Ride createRide(LocalDateTime scheduledTime, Driver assignedDriver) {
        Ride ride = new Ride();
        ride.setId(UUID.randomUUID());
        ride.setRoute(route);
        ride.setStart(route.getStart());
        ride.setEnd(route.getDestination());
        ride.setScheduledAt(scheduledTime);
        ride.setDriver(assignedDriver);
        ride.setStatus(RideStatus.REQUESTED);
        ride.setPetTransport(false);
        ride.setBabyTransport(false);
        ride.setVehicleType(VehicleType.STANDARD);
        ride.setPassengers(List.of(passenger));
        return ride;
    }
}