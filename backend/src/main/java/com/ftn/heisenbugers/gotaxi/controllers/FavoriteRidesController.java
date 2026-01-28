package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.FavoriteRouteDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.LocationDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.RouteDTO;
import com.ftn.heisenbugers.gotaxi.services.FavoriteRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/favorite-routes")
@RequiredArgsConstructor
public class FavoriteRidesController {

    private final FavoriteRouteService favoriteRouteService;

    @GetMapping
    public ResponseEntity<List<FavoriteRouteDTO>> getFavoriteRoutes(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
                favoriteRouteService.getFavorites(user.getId())
        );
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FavoriteRouteDTO> createFavoriteRoute(
            @RequestBody FavoriteRouteDTO request) {

        FavoriteRouteDTO route = new FavoriteRouteDTO();
        route.setId(request.getId());
        //route.setRoute(request.getRoute());

        return new ResponseEntity<>(route, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable UUID id) {
        favoriteRouteService.deleteFavorite(id);
        return ResponseEntity.noContent().build();
    }
}
