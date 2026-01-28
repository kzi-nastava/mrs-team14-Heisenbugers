package com.ftn.heisenbugers.gotaxi.services;

import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.dtos.DriverProfileDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.DriverProfileRequestDetailDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.DriverProfileRequestListDTO;
import com.ftn.heisenbugers.gotaxi.repositories.DriverProfileRequestRepository;
import com.ftn.heisenbugers.gotaxi.repositories.DriverRepository;
import org.springframework.stereotype.Service;
import com.ftn.heisenbugers.gotaxi.models.DriverProfileRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverProfileRequestService {
    private final DriverProfileRequestRepository repository;
    private final DriverRepository driverRepository;

    public List<DriverProfileRequestListDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(r -> DriverProfileRequestListDTO.builder()
                        .id(r.getId())
                        .firstName(r.getFirstName())
                        .lastName(r.getLastName())
                        .email(r.getEmail())
                        .build())
                .toList();
    }

    public DriverProfileRequestDetailDTO getById(UUID id) {
        DriverProfileRequest request = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        return DriverProfileRequestDetailDTO.builder()
                .id(request.getId())
                .approved(request.isApproved())
                .submittedBy("Driver")
                .submittedAt(LocalDateTime.now())
                .oldProfile(buildOldProfile(request))
                .newProfile(buildNewProfile(request))
                .build();
    }

    public void approve(UUID id) {
        DriverProfileRequest request = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        request.setApproved(true);
        repository.save(request);

        Optional<Driver> driverOptional = driverRepository.findByUserEmailWithVehicle(request.getEmail());
        Driver driver = driverOptional.get();
        driver.setFirstName(request.getFirstName());
        driver.setLastName(request.getLastName());
        driver.setPhone(request.getPhone());
        driver.setAddress(request.getAddress());
        driver.setProfileImageUrl(request.getProfileImageUrl());
        driver.getVehicle().setModel(request.getModel());
        driver.getVehicle().setType(request.getType());
        driver.getVehicle().setLicensePlate(request.getLicensePlate());
        driver.getVehicle().setSeatCount(request.getSeatCount());
        driver.getVehicle().setBabyTransport(request.isBabyTransport());
        driver.getVehicle().setPetTransport(request.isPetTransport());

        driverRepository.save(driver);
    }


    public void reject(UUID id) {
        DriverProfileRequest request = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        request.setApproved(false);
        repository.save(request);
    }

    private DriverProfileDTO buildOldProfile(DriverProfileRequest r) {
        Optional<Driver> driver = driverRepository.findByUserEmailWithVehicle(r.getEmail());
        return DriverProfileDTO.builder()
                .firstName(driver.get().getFirstName())
                .lastName(driver.get().getLastName())
                .phone(driver.get().getPhone())
                .address(driver.get().getAddress())
                .profileImageUrl("http://localhost:8081" + driver.get().getProfileImageUrl())
                .model(driver.get().getVehicle().getModel())
                .type(driver.get().getVehicle().getType())
                .licensePlate(driver.get().getVehicle().getLicensePlate())
                .seatCount(driver.get().getVehicle().getSeatCount())
                .babyTransport(driver.get().getVehicle().isBabyTransport())
                .petTransport(driver.get().getVehicle().isPetTransport())
                .build();
    }

    private DriverProfileDTO buildNewProfile(DriverProfileRequest r) {
        return DriverProfileDTO.builder()
                .firstName(r.getFirstName())
                .lastName(r.getLastName())
                .phone(r.getPhone())
                .address(r.getAddress())
                .profileImageUrl("http://localhost:8081" + r.getProfileImageUrl())
                .model(r.getModel())
                .type(r.getType())
                .licensePlate(r.getLicensePlate())
                .seatCount(r.getSeatCount())
                .babyTransport(r.isBabyTransport())
                .petTransport(r.isPetTransport())
                .build();
    }
}
