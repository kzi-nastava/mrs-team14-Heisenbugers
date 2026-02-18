package com.ftn.heisenbugers.gotaxi.services;

import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.security.JwtService;
import com.ftn.heisenbugers.gotaxi.repositories.DriverRepository;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduledRideProcessorService {
    private final RideRepository rideRepository;
    private final NotificationService notificationService;
    private final RideService rideService;
    private final JwtService jwtService;

    private final DriverRepository driverRepository;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void processScheduledRides() {

        LocalDateTime now = LocalDateTime.now();

        List<Ride> rides = rideRepository
                .findByStatusAndScheduledAtAfter(RideStatus.REQUESTED, now.minusHours(5));

        for (Ride ride : rides) {

            LocalDateTime scheduled = ride.getScheduledAt();

            if (scheduled.minusMinutes(15).isBefore(now) &&
                    ride.getDriver() == null) {

                Optional<Driver> driver = rideService.assignDriverToRide(ride, ride.isPetTransport(), ride.isBabyTransport(), ride.getVehicleType());
                if (driver.isPresent()) {
                    ride.setDriver(driver.get());
                    ride.setStatus(RideStatus.ASSIGNED);
                    ride.setVehicle(driver.get().getVehicle());
                    driver.get().setAvailable(false);
                    rideRepository.save(ride);
                    Map<String, Object> claims = Map.of(
                            "rideId", ride.getId().toString()
                    );
                    rideService.sendAcceptedRideEmail(ride.getRoute().getUser(), ride, jwtService.generateToken(ride.getRoute().getUser().getEmail(), claims));
                    for (int i = 0; i < ride.getPassengers().size(); i++) {
                        rideService.sendAcceptedRideEmail(ride.getPassengers().get(i), ride, jwtService.generateToken(ride.getPassengers().get(i).getEmail(), claims));
                    }
                }
            }

            if (scheduled.isAfter(now)) {

                long minutesUntil = Duration.between(now, scheduled).toMinutes();

                if (minutesUntil <= 15 && minutesUntil % 5 == 0) {

                    notificationService.notifyUser(
                            ride.getRoute().getUser(),
                            "Reminder: You have a scheduled ride at "
                                    + scheduled.toString(),
                            ride
                    );
                }

                if (minutesUntil == 0 && ride.getDriver() == null){
                    notificationService.notifyUser(
                            ride.getRoute().getUser(),
                            "Unfortunately there is no free driver for your ride!",
                            ride
                    );
                }
            }
        }
    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void tickWorkingDriversActiveTime() {
        driverRepository.incrementActiveMinutesCapped();
    }
}
