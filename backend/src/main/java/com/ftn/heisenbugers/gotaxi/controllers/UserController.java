package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Passenger;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.RideHistoryDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideSort;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import com.ftn.heisenbugers.gotaxi.services.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<List<User>> getRideTracking() {
        List<User> users = userRepository.findAll();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/history")
    public ResponseEntity<List<RideHistoryDTO>> getHistory(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                           @RequestParam(defaultValue = "DATE") RideSort sortBy,
                                                           @RequestParam(defaultValue = "asc") String direction) {
        Object sub = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        if (!(sub instanceof Passenger passenger)) {
            return ResponseEntity.badRequest().build();
        }
        List<RideHistoryDTO> history = userService.getUserHistory(passenger, startDate, endDate, sortBy, direction);

        return ResponseEntity.ok(history);
    }
}
