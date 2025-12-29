package com.ftn.heisenbugers.gotaxi.controller;

import com.ftn.heisenbugers.gotaxi.models.dtos.FavoriteRouteDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.LocationDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.RouteDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/favorite-routes")
public class FavoriteRidesController {
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<FavoriteRouteDTO>> getFavoriteRoutes() {

        Collection<FavoriteRouteDTO> routes = new ArrayList<>();

        FavoriteRouteDTO route = new FavoriteRouteDTO();
        route.setId(1L);
        route.setRoute(new RouteDTO(2.1, 32, "putanja", new LocationDTO(23.125, 48.251, "Bulevar"), new LocationDTO(22.125, 44.251, "Ulica"), new ArrayList<LocationDTO>()));

        FavoriteRouteDTO route2 = new FavoriteRouteDTO();
        route2.setId(2L);
        route2.setRoute(new RouteDTO(3.4, 52, "putanja2", new LocationDTO(23.125, 48.251, "Bulevar"), new LocationDTO(22.125, 44.251, "Ulica"), new ArrayList<LocationDTO>()));

        routes.add(route);
        routes.add(route2);

        return new ResponseEntity<>(routes, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FavoriteRouteDTO> createFavoriteRoute(
            @RequestBody FavoriteRouteDTO request) {

        FavoriteRouteDTO route = new FavoriteRouteDTO();
        route.setId(request.getId());
        route.setRoute(request.getRoute());

        return new ResponseEntity<>(route, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFavoriteRoute(@PathVariable Long id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
