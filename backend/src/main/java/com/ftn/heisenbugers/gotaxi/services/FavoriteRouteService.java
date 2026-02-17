package com.ftn.heisenbugers.gotaxi.services;

import com.ftn.heisenbugers.gotaxi.models.Route;
import com.ftn.heisenbugers.gotaxi.models.dtos.FavoriteRouteDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.LocationDTO;
import com.ftn.heisenbugers.gotaxi.repositories.RouteRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteRouteService {
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;

    public List<FavoriteRouteDTO> getFavorites(UUID userId) {
        return routeRepository.findAllByUserIdAndFavoriteTrue(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public void deleteFavorite(UUID routeId) {
        Route route = routeRepository.findById(routeId).get();
        route.setFavorite(false);

        routeRepository.save(route);
    }

    private FavoriteRouteDTO toDto(Route route) {
        return new FavoriteRouteDTO(
                route.getId(),
                new LocationDTO(route.getStart()) ,
                new LocationDTO(route.getDestination()),
                route.getStopsWithAddresses() == null
                        ? List.of()
                        : route.getStopsWithAddresses().stream()
                        .map(l -> new LocationDTO(
                                l.getLatitude(),
                                l.getLongitude(),
                                l.getAddress()
                        ))
                        .toList(),
                route.getDistanceKm(),
                route.getEstimatedTimeMin()
        );
    }
}
