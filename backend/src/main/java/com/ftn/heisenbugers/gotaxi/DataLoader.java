package com.ftn.heisenbugers.gotaxi;

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

import java.math.BigDecimal;
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

        Location l = new Location(45.23421, 30.34512, "Nesto");

        Driver driver1 = new Driver();
        driver1.setEmail("alice@example.com");
        driver1.setPasswordHash("password123"); // in real app, hash it
        driver1.setFirstName("Alice");
        driver1.setLastName("Smith");
        driver1.setAvailable(true);
        driver1.setLocation(l);
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
        ride1.setPrice(new BigDecimal(500));

        locationRepository.save(l);
        driverRepository.saveAll(List.of(driver1, driver2)); // cascade saves vehicles
        rideRepository.saveAll(List.of(ride1));

    }
}


