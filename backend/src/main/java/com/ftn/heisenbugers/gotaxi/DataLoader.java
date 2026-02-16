package com.ftn.heisenbugers.gotaxi;
/*
import com.ftn.heisenbugers.gotaxi.models.*;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {
    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public DataLoader(RideRepository rideRepository, UserRepository userRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        Ride r = rideRepository.findRideById(UUID.fromString("c527273a-ba41-43e2-aa7c-ab78560177ee"));
        Passenger p = (Passenger) userRepository.findPassengerById(UUID.fromString("a770919d-3303-45a8-ba06-6de0a97bda93"));
        List<User> passengers = new ArrayList<>();
        passengers.add(p);
        r.setPassengers(passengers);
        r.setRoute(new Route());
        List<Location> coords = new ArrayList<>();
        coords.add(new Location(45.249570, 19.815809));
        coords.add(new Location(45.242299, 19.796333));
        coords.add(new Location(45.241604, 19.842757));
        r.getRoute().setPolyline(coords);

        rideRepository.save(r);
        User p = userRepository.findById(UUID.fromString("874645b9-4653-4ac8-8bed-279a6b3d8762")).orElseThrow();
        List<Passenger> ps = new ArrayList<>();
        ps.add((Passenger) p);
        r.setPassengers(ps);
        rideRepository.save(r);


    }
}

import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Location;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.Vehicle;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import com.ftn.heisenbugers.gotaxi.repositories.DriverRepository;
import com.ftn.heisenbugers.gotaxi.repositories.LocationRepository;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public void run(String... args) throws Exception {
        Vehicle v1 = new Vehicle();
        v1.setModel("Toyota Corolla");
        v1.setLicensePlate("ABC123");
        v1.setSeatCount(4);
        v1.setType(VehicleType.STANDARD);
        v1.setBabyTransport(true);
        v1.setPetTransport(false);

        Vehicle v2 = new Vehicle();
        v2.setModel("Honda Civic");
        v2.setLicensePlate("XYZ789");
        v2.setSeatCount(4);
        v2.setType(VehicleType.STANDARD);
        v2.setBabyTransport(false);
        v2.setPetTransport(true);

        Location l1 = new Location(45.23421, 30.34512, "Nesto");
        Location l2 = new Location(45.23421, 30.34512, "Adresa 123");
        Location l3 = new Location(45.23421, 30.34512, "Neka druga adresa");

        Driver driver1 = new Driver();
        driver1.setEmail("alice@example.com");
        driver1.setPasswordHash("password123"); // in real app, hash it
        driver1.setFirstName("Alice");
        driver1.setLastName("Smith");
        driver1.setAvailable(true);
        driver1.setLocation(l1);
        driver1.setVehicle(v1);
        v1.setDriver(driver1);

        Driver driver2 = new Driver();
        driver2.setEmail("bob@example.com");
        driver2.setPasswordHash("password123");
        driver2.setFirstName("Bob");
        driver2.setLastName("Johnson");
        driver2.setAvailable(false);
        driver2.setVehicle(v2);
        v2.setDriver(driver2);

        Ride ride1 = new Ride();
        ride1.setStatus(RideStatus.ONGOING);
        ride1.setDriver(driver1);
        ride1.setPrice(500);
        ride1.setVehicle(v1);
        ride1.setStartedAt(LocalDateTime.now().minusHours(2));
        ride1.setEndedAt(LocalDateTime.now());
        ride1.setEnd(l2);

        Ride ride2 = new Ride();
        ride2.setStatus(RideStatus.FINISHED);
        ride2.setDriver(driver1);
        ride2.setPrice(400);
        ride2.setVehicle(v1);
        ride2.setStartedAt(LocalDateTime.now().minusHours(3));
        ride2.setEndedAt(LocalDateTime.now().minusHours(2));
        ride2.setEnd(l3);

        locationRepository.saveAll(List.of(l1, l2, l3));
        driverRepository.saveAll(List.of(driver1, driver2)); // cascade saves vehicles
        rideRepository.saveAll(List.of(ride1, ride2));

    }
}


*/