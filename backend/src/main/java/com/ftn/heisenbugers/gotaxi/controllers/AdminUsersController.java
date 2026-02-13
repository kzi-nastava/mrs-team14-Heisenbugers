package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Passenger;
import com.ftn.heisenbugers.gotaxi.models.dtos.AdminUserListItemDTO;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUsersController {

    private final UserRepository userRepository;

    public AdminUsersController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/drivers")
    public ResponseEntity<List<AdminUserListItemDTO>> drivers() {
        List<Driver> drivers = userRepository.findAllDrivers();
        return ResponseEntity.ok(drivers.stream().map(u ->
                new AdminUserListItemDTO(
                        u.getId(),
                        u.getFirstName() + " " + u.getLastName(),
                        u.getEmail(),
                        u.getProfileImageUrl()
                )
        ).collect(Collectors.toList()));
    }

    @GetMapping("/passengers")
    public ResponseEntity<List<AdminUserListItemDTO>> passengers() {
        List<Passenger> passengers = userRepository.findAllPassengers();
        return ResponseEntity.ok(passengers.stream().map(u ->
                new AdminUserListItemDTO(
                        u.getId(),
                        u.getFirstName() + " " + u.getLastName(),
                        u.getEmail(),
                        u.getProfileImageUrl()
                )
        ).collect(Collectors.toList()));
    }
}
