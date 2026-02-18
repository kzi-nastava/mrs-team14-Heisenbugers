package com.ftn.heisenbugers.gotaxi.service;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Location;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.StopRideRequestDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.models.services.EmailService;
import com.ftn.heisenbugers.gotaxi.repositories.LocationRepository;
import com.ftn.heisenbugers.gotaxi.repositories.PassengerRepository;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import com.ftn.heisenbugers.gotaxi.services.RideActionsService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideActionsServiceTest {

    @Mock RideRepository rideRepository;
    @Mock LocationRepository locationRepository;
    @Mock UserRepository userRepository;

    // NEW dependencies in service
    @Mock EmailService emailService;
    @Mock PassengerRepository passengerRepository;

    @InjectMocks RideActionsService service;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // --------------------
    // NEGATIVE CASES
    // --------------------

    @Test
    @DisplayName("if ride not found -> 404 + message")
    void testStopRide_WhenRideNotFound_Returns404() throws Exception {
        UUID rideId = UUID.randomUUID();
        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        ResponseEntity<?> resp = service.stopRide(rideId, req(45.0, 19.0, "addr"));

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertEquals("Ride not found.", bodyAsMap(resp).get("message"));

        verify(rideRepository).findById(rideId);
        verifyNoMoreInteractions(rideRepository, locationRepository, userRepository, emailService, passengerRepository);
    }

    @Test
    @DisplayName("if status != ONGOING -> 409 + message")
    void testStopRide_WhenNotOngoing_Returns409() throws Exception {
        UUID rideId = UUID.randomUUID();
        Ride r = ride(rideId, RideStatus.ASSIGNED, driverWithId(UUID.randomUUID()),
                loc(45, 19, "Start"), loc(45.1, 19.1, "End"), 100);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(r));

        ResponseEntity<?> resp = service.stopRide(rideId, req(45.0, 19.0, "addr"));

        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertEquals("Ride is not ongoing.", bodyAsMap(resp).get("message"));

        verify(rideRepository).findById(rideId);
        verifyNoMoreInteractions(rideRepository, locationRepository, userRepository, emailService, passengerRepository);
    }

    @Test
    @DisplayName("if wrong driver -> 403 + message")
    void testStopRide_WhenWrongDriver_Returns403() throws Exception {
        UUID rideId = UUID.randomUUID();
        Driver realDriver = driverWithId(UUID.randomUUID());
        Driver otherDriver = driverWithId(UUID.randomUUID());

        authAsDriver(otherDriver);

        Ride r = ride(rideId, RideStatus.ONGOING, realDriver,
                loc(45, 19, "Start"), loc(45.1, 19.1, "End"), 100);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(r));

        ResponseEntity<?> resp = service.stopRide(rideId, req(45.0, 19.0, "addr"));

        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
        assertEquals("Only ride driver can stop the ride.", bodyAsMap(resp).get("message"));

        verify(rideRepository).findById(rideId);
        verifyNoMoreInteractions(rideRepository, locationRepository, userRepository, emailService, passengerRepository);
    }

    @Test
    @DisplayName("if request null or coords missing -> 400 + message")
    void testStopRide_WhenMissingCoords_Returns400() throws Exception {
        UUID rideId = UUID.randomUUID();
        Driver d = driverWithId(UUID.randomUUID());
        authAsDriver(d);

        Ride r = ride(rideId, RideStatus.ONGOING, d,
                loc(45, 19, "Start"), loc(45.1, 19.1, "End"), 100);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(r));

        ResponseEntity<?> resp1 = service.stopRide(rideId, null);
        assertEquals(HttpStatus.BAD_REQUEST, resp1.getStatusCode());
        assertEquals("Stop location (latitude/longitude) is required.", bodyAsMap(resp1).get("message"));

        ResponseEntity<?> resp2 = service.stopRide(rideId, req(null, 19.0, "addr"));
        assertEquals(HttpStatus.BAD_REQUEST, resp2.getStatusCode());

        ResponseEntity<?> resp3 = service.stopRide(rideId, req(45.0, null, "addr"));
        assertEquals(HttpStatus.BAD_REQUEST, resp3.getStatusCode());

        verify(rideRepository, times(3)).findById(rideId);
        verifyNoMoreInteractions(rideRepository, locationRepository, userRepository, emailService, passengerRepository);
    }

    @Test
    @DisplayName("if ride has no start -> 409 + message")
    void testStopRide_WhenNoStart_Returns409() throws Exception {
        UUID rideId = UUID.randomUUID();
        Driver d = driverWithId(UUID.randomUUID());
        authAsDriver(d);

        Ride r = ride(rideId, RideStatus.ONGOING, d,
                null, loc(45.1, 19.1, "End"), 100);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(r));

        ResponseEntity<?> resp = service.stopRide(rideId, req(45.0, 19.0, "addr"));

        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertEquals("Ride has no start location.", bodyAsMap(resp).get("message"));

        verify(rideRepository).findById(rideId);
        verifyNoMoreInteractions(rideRepository, locationRepository, userRepository, emailService, passengerRepository);
    }

    // --------------------
    // HAPPY PATHS
    // --------------------

    @Test
    @DisplayName("success -> saves stop location, sets FINISHED, sets endedAt/end, recalculates price, returns 200")
    void testStopRide_Success_UpdatesRideAndReturns200() throws Exception {
        UUID rideId = UUID.randomUUID();
        Driver d = driverWithId(UUID.randomUUID());
        authAsDriver(d);

        Location start = loc(45.0, 19.0, "Start");
        Location oldEnd = loc(45.10, 19.10, "Old destination");
        Ride r = ride(rideId, RideStatus.ONGOING, d, start, oldEnd, 100.0);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(r));
        when(locationRepository.save(any(Location.class))).thenAnswer(inv -> inv.getArgument(0));
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

        StopRideRequestDTO request = req(45.05, 19.05, "New stop address");

        ResponseEntity<?> resp = service.stopRide(rideId, request);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        Map<String, Object> body = bodyAsMap(resp);

        assertEquals("Ride stopped and finished.", body.get("message"));
        assertNotNull(body.get("rideId"));
        assertNotNull(body.get("endedAt"));
        assertNotNull(body.get("price"));
        assertNotNull(body.get("newDestination"));

        assertEquals(RideStatus.FINISHED, r.getStatus());
        assertNotNull(r.getEndedAt());
        assertNotNull(r.getEnd());
        assertEquals("New stop address", r.getEnd().getAddress());
        assertTrue(r.getPrice() > 0);

        ArgumentCaptor<Location> locCaptor = ArgumentCaptor.forClass(Location.class);
        verify(locationRepository).save(locCaptor.capture());
        assertEquals(45.05, locCaptor.getValue().getLatitude());
        assertEquals(19.05, locCaptor.getValue().getLongitude());
        assertEquals("New stop address", locCaptor.getValue().getAddress());

        // called in success flow
        verify(userRepository).save(same(d));
        verify(rideRepository, times(2)).save(any(Ride.class));

        // with empty passengers list => no email calls
        verify(emailService, never()).sendMail(anyString(), anyString(), anyString());
        verify(passengerRepository, never()).delete(any());
    }

    @Test
    @DisplayName("if address blank -> sets default 'Stopped at: lat, lon'")
    void testStopRide_WhenAddressBlank_SetsDefaultAddress() throws Exception {
        UUID rideId = UUID.randomUUID();
        Driver d = driverWithId(UUID.randomUUID());
        authAsDriver(d);

        Ride r = ride(rideId, RideStatus.ONGOING, d,
                loc(45.0, 19.0, "Start"),
                loc(45.1, 19.1, "Old destination"),
                100.0);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(r));
        when(locationRepository.save(any(Location.class))).thenAnswer(inv -> inv.getArgument(0));
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<?> resp = service.stopRide(rideId, req(45.05, 19.05, "   "));

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("Stopped at: 45.05, 19.05", r.getEnd().getAddress());

        verify(userRepository).save(same(d));
        verify(rideRepository, times(2)).save(any(Ride.class));
    }

    @Test
    @DisplayName("price recalculation ~50% when stop is mid-route")
    void testStopRide_PriceRecalc_HalfDistance_SetsHalfPrice() throws Exception {
        UUID rideId = UUID.randomUUID();
        Driver d = driverWithId(UUID.randomUUID());
        authAsDriver(d);

        Location start = loc(0.0, 0.0, "Start");
        Location oldEnd = loc(0.0, 1.0, "Old end");
        Ride r = ride(rideId, RideStatus.ONGOING, d, start, oldEnd, 100.0);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(r));
        when(locationRepository.save(any(Location.class))).thenAnswer(inv -> inv.getArgument(0));
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<?> resp = service.stopRide(rideId, req(0.0, 0.5, "Mid stop"));

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(50.0, r.getPrice(), 0.01);

        verify(userRepository).save(same(d));
        verify(rideRepository, times(2)).save(any(Ride.class));
    }

    @Test
    @DisplayName("price recalculation clamps to min 10% when stop too close to start")
    void testStopRide_PriceRecalc_ClampsToMin10Percent() throws Exception {
        UUID rideId = UUID.randomUUID();
        Driver d = driverWithId(UUID.randomUUID());
        authAsDriver(d);

        Location start = loc(0.0, 0.0, "Start");
        Location oldEnd = loc(0.0, 1.0, "Old end");
        Ride r = ride(rideId, RideStatus.ONGOING, d, start, oldEnd, 100.0);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(r));
        when(locationRepository.save(any(Location.class))).thenAnswer(inv -> inv.getArgument(0));
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<?> resp = service.stopRide(rideId, req(0.0, 0.01, "Near start"));

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(10.0, r.getPrice(), 0.01);

        verify(userRepository).save(same(d));
        verify(rideRepository, times(2)).save(any(Ride.class));
    }

    @Test
    @DisplayName("price recalculation clamps to max 100% when stop beyond old end")
    void testStopRide_PriceRecalc_ClampsToMax100Percent() throws Exception {
        UUID rideId = UUID.randomUUID();
        Driver d = driverWithId(UUID.randomUUID());
        authAsDriver(d);

        Location start = loc(0.0, 0.0, "Start");
        Location oldEnd = loc(0.0, 1.0, "Old end");
        Ride r = ride(rideId, RideStatus.ONGOING, d, start, oldEnd, 100.0);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(r));
        when(locationRepository.save(any(Location.class))).thenAnswer(inv -> inv.getArgument(0));
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<?> resp = service.stopRide(rideId, req(0.0, 2.0, "Beyond end"));

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(100.0, r.getPrice(), 0.01);

        verify(userRepository).save(same(d));
        verify(rideRepository, times(2)).save(any(Ride.class));
    }

    // --------------------
    // EXCEPTION CASES
    // --------------------

    @Test
    @DisplayName("if principal is NOT Driver -> throws InvalidUserType")
    void testStopRide_WhenPrincipalNotDriver_ThrowsInvalidUserType() throws Exception {
        UUID rideId = UUID.randomUUID();

        Driver rideDriver = driverWithId(UUID.randomUUID());
        Ride r = ride(rideId, RideStatus.ONGOING, rideDriver,
                loc(45, 19, "Start"), loc(45.1, 19.1, "End"), 100);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(r));

        var auth = new UsernamePasswordAuthenticationToken("not-a-driver", null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(InvalidUserType.class, () -> service.stopRide(rideId, req(45.0, 19.0, "addr")));
    }

    @Test
    @DisplayName("if there is NO authentication -> throws NullPointerException (from AuthContextService)")
    void testStopRide_WhenNoAuthentication_ThrowsNullPointerException() throws Exception {
        UUID rideId = UUID.randomUUID();

        Driver rideDriver = driverWithId(UUID.randomUUID());
        Ride r = ride(rideId, RideStatus.ONGOING, rideDriver,
                loc(45, 19, "Start"), loc(45.1, 19.1, "End"), 100);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(r));

        SecurityContextHolder.clearContext();

        assertThrows(NullPointerException.class, () -> service.stopRide(rideId, req(45.0, 19.0, "addr")));
    }

    // --------------------
    // HELPERS
    // --------------------

    private Driver driverWithId(UUID id) {
        Driver d = new Driver();
        d.setId(id);
        return d;
    }

    private Ride ride(UUID id, RideStatus status, Driver driver, Location start, Location end, double price) {
        Ride r = new Ride();
        r.setId(id);
        r.setStatus(status);
        r.setDriver(driver);
        r.setStart(start);
        r.setEnd(end);
        r.setPrice(price);

        // IMPORTANT: service iterates passengers; must not be null
        r.setPassengers(new ArrayList<User>());

        return r;
    }

    private Location loc(double lat, double lon, String addr) {
        return Location.builder().latitude(lat).longitude(lon).address(addr).build();
    }

    private StopRideRequestDTO req(Double lat, Double lon, String address) {
        StopRideRequestDTO dto = new StopRideRequestDTO();
        dto.setLatitude(lat);
        dto.setLongitude(lon);
        dto.setAddress(address);
        return dto;
    }

    private void authAsDriver(Driver driver) {
        var auth = new UsernamePasswordAuthenticationToken(driver, null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> bodyAsMap(ResponseEntity<?> resp) {
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody() instanceof Map);
        return (Map<String, Object>) resp.getBody();
    }
}
