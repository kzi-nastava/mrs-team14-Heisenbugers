package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.dtos.DriverProfileRequestDetailDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.DriverProfileRequestListDTO;
import com.ftn.heisenbugers.gotaxi.services.DriverProfileRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/driver-requests")
@RequiredArgsConstructor
public class DriverProfileRequestController {

    private final DriverProfileRequestService service;

    @GetMapping
    public ResponseEntity<List<DriverProfileRequestListDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverProfileRequestDetailDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable UUID id) {
        service.approve(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable UUID id) {
        service.reject(id);
        return ResponseEntity.ok().build();
    }
}