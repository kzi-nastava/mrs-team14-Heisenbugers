package com.ftn.heisenbugers.gotaxi.utils;

import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Location;
import com.ftn.heisenbugers.gotaxi.models.Route;
import com.ftn.heisenbugers.gotaxi.repositories.LocationRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DrivingSimulator {
    private final UserRepository userRepository;
    private final ApplicationContext context;
    private final LocationRepository locationRepository;

    public DrivingSimulator(UserRepository userRepository, ApplicationContext context, LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.context = context;
        this.locationRepository = locationRepository;
    }

    public void driveRoute(Route route, UUID driverId) throws Exception {
        List<Location> stops = new ArrayList<>(List.of(route.getStart()));
        stops.addAll(route.getStopsWithAddresses());
        stops.add(route.getDestination());
        driveRoute(stops, driverId);
    }

    public void driveRoute(List<Location> route, UUID driverId) throws Exception {
        List<OsrmRouteService.Point> fullRoute = getPointRoute(toPoints(route));
        driveAsync(fullRoute, driverId);
    }

    private List<OsrmRouteService.Point> toPoints(List<Location> route) {
        return route.stream().map(l -> new OsrmRouteService.Point(l.getLatitude(), l.getLongitude())).toList();
    }

    private List<OsrmRouteService.Point> getPointRoute(List<OsrmRouteService.Point> stops) throws Exception {
        return OsrmRouteService.getRoutePoints(stops);
    }

    @Async
    public void driveAsync(List<OsrmRouteService.Point> points, UUID driverId) {
        DrivingSimulator proxy = context.getBean(DrivingSimulator.class);
        for (int i = 0; i < points.size(); i += 10) {
            try {
                OsrmRouteService.Point p = points.get(i);
                Location loc = new Location();
                loc.setLatitude(p.getLatitude());
                loc.setLongitude(p.getLongitude());
                proxy.updateDriverLocation(driverId, loc);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Transactional
    public void updateDriverLocation(UUID driverId, Location loc) {
        Driver d = (Driver) userRepository.findById(driverId).orElseThrow();
        d.setLocation(loc);
        locationRepository.save(loc);
        userRepository.save(d);
    }
}
