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

    @PostMapping("/{id}")
    public ResponseEntity<Void> approve(@PathVariable UUID id) {
        favoriteRouteService.addFavorite(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable UUID id) {
        favoriteRouteService.deleteFavorite(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/ride")
    public ResponseEntity<Void> deleteFavoriteFromRide(@PathVariable UUID id) {
        favoriteRouteService.deleteFavoriteFromRide(id);
        return ResponseEntity.noContent().build();
    }
}
