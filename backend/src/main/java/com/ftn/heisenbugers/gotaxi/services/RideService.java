package com.ftn.heisenbugers.gotaxi.services;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.*;
import com.ftn.heisenbugers.gotaxi.models.dtos.*;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.models.security.JwtService;
import com.ftn.heisenbugers.gotaxi.models.services.EmailService;
import com.ftn.heisenbugers.gotaxi.repositories.*;
import com.ftn.heisenbugers.gotaxi.utils.DrivingSimulator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RideService {
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;
    private final TrafficViolationRepository violationRepository;
    private final RatingRepository ratingRepository;
    private final EmailService emailService;
    private final DriverService driverService;
    private final JwtService jwtService;
    private final NotificationService notificationService;
    private final PriceRepository priceRepository;
    private final DrivingSimulator drivingSimulator;

    public CreatedRideDTO addRide(CreateRideDTO request) throws InvalidUserType {

        User user = AuthContextService.getCurrentUser();

        Route route = new Route();
        Location start = new Location(request.getRoute().getStart().getLatitude(), request.getRoute().getStart().getLongitude(), request.getRoute().getStart().getAddress());
        Location end = new Location(request.getRoute().getDestination().getLatitude(), request.getRoute().getDestination().getLongitude(), request.getRoute().getDestination().getAddress());
        List<Location> stops = new ArrayList<Location>();
        List<Location> points = new ArrayList<Location>();
        points.add(start);
        for (int i = 0; i < request.getRoute().getStops().size(); i++) {
            Location stop = new Location(request.getRoute().getStops().get(i).getLatitude(), request.getRoute().getStops().get(i).getLongitude(), request.getRoute().getStops().get(i).getAddress());
            stops.add(stop);
        }
        points.addAll(stops);
        points.add(end);
        route.setStart(start);
        route.setDestination(end);
        route.setStops(stops);
        route.setPolyline(points);
        route.setEstimatedTimeMin(request.getRoute().getEstimatedTimeMin());
        route.setDistanceKm(request.getRoute().getDistanceKm());
        route.setUser(user);

        double price = priceRepository.findAll().stream()
                .filter(p -> p.getVehicleType().equals(request.getVehicleType()))
                .max(Comparator.comparing(Price::getCreatedAt))
                .map(Price::getStartingPrice)
                .get();

        Ride ride = new Ride();
        ride.setRoute(route);
        ride.setStatus(RideStatus.REQUESTED);
        ride.setCanceled(false);
        ride.setStart(start);
        ride.setEnd(end);
        ride.setPrice(request.getRoute().getDistanceKm() * 120 + price);
        ride.setPassengers(new ArrayList<>());
        for (int i = 0; i < request.getPassengersEmails().size(); i++) {
            Optional<Passenger> p = passengerRepository.findByEmail(request.getPassengersEmails().get(i));
            if (p.isPresent()) {
                ride.addPassenger(p.get());
            } else {
                Passenger pass = new Passenger(request.getPassengersEmails().get(i), "hashPassword", "/", "/", "0000000000", "Address");
                ride.addPassenger(pass);
                passengerRepository.save(pass);
            }
        }

        if (request.getScheduledAt() != null) {
            ZonedDateTime zdt = ZonedDateTime.parse(request.getScheduledAt());
            ride.setScheduledAt(zdt.withZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDateTime());
            ride.setBabyTransport(request.isBabyTransport());
            ride.setPetTransport(request.isPetTransport());
            ride.setVehicleType(request.getVehicleType());
            rideRepository.save(ride);
            return new CreatedRideDTO(ride.getId(), request.getRoute(), request.getVehicleType(), request.isBabyTransport(),
                    request.isPetTransport(), request.getPassengersEmails(), null, RideStatus.REQUESTED);
        } else {
            ride.setScheduledAt(null);
        }

        Optional<Driver> driver = assignDriverToRide(ride, request.isPetTransport(), request.isBabyTransport(), request.getVehicleType());

        if (driver.isEmpty()) {
            throw new RuntimeException("There is no free driver available!");
        } else {
            ride.setDriver(driver.get());
            ride.setStatus(RideStatus.ASSIGNED);
            ride.setVehicle(driver.get().getVehicle());
            driver.get().setAvailable(false);
            rideRepository.save(ride);
            Map<String, Object> claims = Map.of(
                    "rideId", ride.getId().toString()
            );
            sendAcceptedRideEmail(ride.getRoute().getUser(), ride, jwtService.generateToken(ride.getRoute().getUser().getEmail(), claims));
            for (int i = 0; i < ride.getPassengers().size(); i++) {
                sendAcceptedRideEmail(ride.getPassengers().get(i), ride, jwtService.generateToken(ride.getPassengers().get(i).getEmail(), claims));
            }
            notificationService.notifyUser(ride.getRoute().getUser(), "Your ride has been confirmed!",
                    ride, "/base");
        }

        return new CreatedRideDTO(ride.getId(), request.getRoute(), ride.getVehicle().getType(), ride.getVehicle().isBabyTransport(),
                ride.getVehicle().isPetTransport(), request.getPassengersEmails(),
                new DriverDto(ride.getDriver().getFirstName(), ride.getDriver().getLastName()), RideStatus.ASSIGNED);
    }

    public Optional<Driver> assignDriverToRide(Ride ride, boolean petTransport, boolean babyTransport, VehicleType vehicleType) {

        List<Driver> activeDrivers = driverService.findActiveDrivers();

        if (activeDrivers.isEmpty()) {
            return Optional.empty();
        }

        List<Driver> eligible = activeDrivers.stream()
                .filter(d -> driverService.vehicleMatchesRequest(d, petTransport, babyTransport, vehicleType))
                .filter(driver -> driverService.canAcceptRide(driver, ride.getRoute().getEstimatedTimeMin()))
                .toList();

        if (eligible.isEmpty()) {
            return Optional.empty();
        }

        List<Driver> freeDrivers = eligible.stream()
                .filter(Driver::isAvailable)
                .toList();

        if (!freeDrivers.isEmpty()) {
            return findNearestFreeDriver(freeDrivers, ride.getStart());
        }

        List<Driver> endingSoon = eligible.stream()
                .filter(d -> driverService.endsRideWithin(d, 10))
                .toList();

        if (endingSoon.isEmpty()) {
            return Optional.empty();
        }

        return findNearestBusyDriverEndingSoon(endingSoon, ride);
    }

    private Optional<Driver> findNearestFreeDriver(
            List<Driver> drivers,
            Location start) {

        return drivers.stream()
                .min(Comparator.comparing(
                        d -> distance(d.getLocation(), start)
                ));
    }

    private Optional<Driver> findNearestBusyDriverEndingSoon(
            List<Driver> drivers,
            Ride newRide) {

        return drivers.stream()
                .min(Comparator.comparing(d -> {
                    Ride currentRide = rideRepository.findActiveRidesByDriver(d.getId()).get(0);
                    return distance(currentRide.getEnd(), newRide.getStart());
                }));
    }

    private double distance(Location a, Location b) {
        double dx = a.getLatitude() - b.getLatitude();
        double dy = a.getLongitude() - b.getLongitude();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public List<RideTrackingDTO> getAll() {
        List<Ride> rides = rideRepository.findAll();

        return rides.stream()
                .map(ride -> {
                    RideTrackingDTO dto = new RideTrackingDTO();
                    dto.setRideId(ride.getId());
                    Driver driver = ride.getDriver();
                    DriverDto driverDto = new DriverDto(driver.getFirstName(), driver.getLastName());

                    dto.setVehicleLatitude(driver.getLocation().getLatitude());
                    dto.setVehicleLongitude(driver.getLocation().getLongitude());
                    dto.setDriver(driverDto);
                    return dto;
                })
                .toList();
    }

    public RideDTO getRide(UUID rideId) {
        Ride r = rideRepository.findRideById(rideId);
        Driver d = r.getDriver();
        return new RideDTO(
                r.getId().toString(),
                new DriverDto(
                        d.getFirstName(),
                        d.getLastName()
                ),
                r.getRoute().getStops().stream().map(LocationDTO::new).toList(),
                new LocationDTO(r.getStart()),
                new LocationDTO(r.getEnd()),
                r.getPrice(),
                r.getStartedAt(),
                r.getEndedAt()
        );
    }

    public RideTrackingDTO getRideTrackingById(UUID rideId) {
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        Ride ride = rideOpt.orElse(null);

        if (ride == null || ride.getStatus() != RideStatus.ONGOING) {
            return null;
        }

        // Get current vehicle location
        Driver driver = ride.getDriver();
        Location currentLocation = driver.getLocation();

        // Calculate remaining time based on route and current position
        Route route = ride.getRoute();
        List<LocationDTO> routeDTOs = new ArrayList<>();
        if (route != null) {
            // Convert route locations to DTOs
            routeDTOs = route.getStops().stream()
                    .map(location -> new LocationDTO(location.getLatitude(),
                            location.getLongitude(), location.getAddress()))
                    .toList();
        }

        DriverDto driverDto = new DriverDto(driver.getFirstName(), driver.getLastName());
        Location startLocation = ride.getStart();
        LocationDTO startLocationDTO = new LocationDTO(startLocation.getLatitude(),
                startLocation.getLongitude(), startLocation.getAddress());

        Location endLocation = ride.getEnd();
        LocationDTO endLocationDTO = new LocationDTO(endLocation.getLatitude(),
                endLocation.getLongitude(), endLocation.getAddress());

        return new RideTrackingDTO(
                rideId,
                driverDto,
                currentLocation.getLatitude(),
                currentLocation.getLongitude(),
                0,
                routeDTOs,
                startLocationDTO,
                endLocationDTO
        );

    }

    public boolean report(UUID rideId, UUID reporterId, String title, String desc) {
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        Ride ride = rideOpt.orElse(null);

        if (ride == null || ride.getStatus() != RideStatus.ONGOING) {
            return false;
        }

        TrafficViolation trafficViolation = new TrafficViolation();
        Optional<User> reporterOpt = userRepository.findById(reporterId);
        User reporter = reporterOpt.orElse(null);
        if (reporter == null) {
            return false;
        }
        trafficViolation.setReporter(reporter);
        trafficViolation.setRide(ride);
        trafficViolation.setTitle(title);
        trafficViolation.setDescription(desc);
        trafficViolation.setCreatedBy(reporter);
        trafficViolation.setLastModifiedBy(reporter);

        violationRepository.save(trafficViolation);
        return true;
    }

    public boolean start(UUID rideId) {
        Ride ride = rideRepository.findById(rideId).get();

        ride.setStatus(RideStatus.ONGOING);
        ride.setStartedAt(LocalDateTime.now());
        ride.setLastModifiedBy(ride.getDriver());
        try {
            drivingSimulator.driveRoute(ride.getRoute(), ride.getDriver().getId());
        } catch (Exception ignored) {
        }
        rideRepository.save(ride);

        return true;
    }

    public boolean finish(UUID rideId, UUID driverId) {
        Driver driver = (Driver) userRepository.findById(driverId).get();
        Ride ride = rideRepository.findById(rideId).get();
        Driver rideDriver = ride.getDriver();
        if (ride.getStatus() != RideStatus.ONGOING || driver != rideDriver) {
            return false;
        }

        ride.setEndedAt(LocalDateTime.now());
        ride.setStatus(RideStatus.FINISHED);
        ride.setLastModifiedBy(driver);
        rideRepository.save(ride);

        rideDriver.setAvailable(true);
        userRepository.save(rideDriver);

        for (User u : ride.getPassengers()) {
            sendFinishedRideEmail(u, ride);
            if (Objects.equals(u.getFirstName(), "/")) {
                passengerRepository.delete((Passenger) u);
            }
        }
        return true;
    }

    public boolean rate(UUID rideId, UUID raterId, int driverScore, int vehicleScore, String comment) {
        Ride ride = rideRepository.findById(rideId).get();
        User rater = userRepository.findById(raterId).get();

        // If already rated
        if (ratingRepository.findByRaterAndRide(rater, ride).isPresent()) {
            return false;
        }

        // If too old to rate
        if (!isInLastNDays(ride, 3)) {
            return false;
        }

        Rating rating = new Rating();

        rating.setRide(ride);
        rating.setRater(rater);
        rating.setDriverScore(driverScore);
        rating.setVehicleScore(vehicleScore);
        rating.setComment(comment);

        rating.setLastModifiedBy(rater);
        ratingRepository.save(rating);
        /*
        ride.setRating(rating);
        rideRepository.save(ride);

         */
        return true;
    }

    private void sendFinishedRideEmail(User recipient, Ride ride) {
        String subject = "Subject: Your Ride Has Completed – Share Your Feedback!";
        String body =
                """
                        Dear %s %s,
                        
                        Your ride from %s to %s with us has successfully concluded. We hope you had a smooth and enjoyable journey!
                        
                        We would love to hear about your experience. You can leave a review by visiting your ride history in your profile.
                        
                        Thank you for choosing our service. We look forward to serving you again soon!
                        
                        Best regards, \s
                        GoTaxi 
                        """.formatted(!Objects.equals(recipient.getFirstName(), "/") ? recipient.getFirstName() : recipient.getEmail(), recipient.getLastName(),
                        ride.getStart().getAddress(), ride.getEnd().getAddress());
        emailService.sendMail(recipient.getEmail(), subject, body);
    }

    public void sendAcceptedRideEmail(User recipient, Ride ride, String token) {
        String subject = "Subject: Your Ride Is Confirmed!";
        String body =
                """
                        Dear %s %s,
                        
                        Your ride from %s to %s with us is confirmed. Our Driver will soon be at your location.
                        
                        You can track your ride on this link: %s
                        
                        Thank you for choosing our service.
                        
                        Best regards, \s
                        GoTaxi 
                        """.formatted(!Objects.equals(recipient.getFirstName(), "/") ? recipient.getFirstName() : recipient.getEmail(), recipient.getLastName(),
                        ride.getRoute().getStart().getAddress(), ride.getRoute().getDestination().getAddress(), "http://localhost:4200/track?token=" + token);
        emailService.sendMail(recipient.getEmail(), subject, body);
    }

    private boolean isInLastNDays(Ride r, long n) {
        if (r.getEndedAt() == null) return true;
        return r.getEndedAt().isAfter(LocalDateTime.now().minusDays(n));
    }

}
