package com.ftn.heisenbugers.gotaxi.services;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.Vehicle;
import com.ftn.heisenbugers.gotaxi.models.dtos.ChangePasswordDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.CreatedVehicleDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.GetProfileDTO;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.repositories.DriverRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import com.ftn.heisenbugers.gotaxi.repositories.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DriverRepository driverRepository;

    public ProfileService(UserRepository userRepository, PasswordEncoder passwordEncoder, DriverRepository driverRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.driverRepository = driverRepository;
    }

    public GetProfileDTO getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return mapToDto(user);
    }

    public GetProfileDTO updateProfile(String email, GetProfileDTO request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        //user.setProfileImageUrl(request.getProfileImageUrl());
        //need to new image upload
        User savedUser = userRepository.save(user);

        return mapToDto(savedUser);
    }

    public CreatedVehicleDTO getMyVehicle() throws InvalidUserType {
        User user = AuthContextService.getCurrentDriver();

        Driver driver = driverRepository.findByIdWithVehicle(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

        return mapToDto(driver.getVehicle());
    }

    public CreatedVehicleDTO updateMyVehicle(CreatedVehicleDTO request) throws InvalidUserType {
        User user = AuthContextService.getCurrentDriver();

        Driver driver = driverRepository.findByIdWithVehicle(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

        driver.getVehicle().setModel(request.getVehicleModel());
        driver.getVehicle().setType(request.getVehicleType());
        driver.getVehicle().setLicensePlate(request.getLicensePlate());
        driver.getVehicle().setSeatCount(request.getSeatCount());
        driver.getVehicle().setBabyTransport(request.isBabyTransport());
        driver.getVehicle().setPetTransport(request.isPetTransport());

        Driver savedDriver = userRepository.save(driver);

        return mapToDto(savedDriver.getVehicle());
    }


    public void changePassword(String email, ChangePasswordDTO request) {

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));


        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private GetProfileDTO mapToDto(User user) {
        GetProfileDTO dto = new GetProfileDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhone());
        dto.setAddress(user.getAddress());
        //dto.setProfileImageUrl(user.getProfileImageUrl());
        //new image upload
        return dto;
    }

    private CreatedVehicleDTO mapToDto(Vehicle vehicle) {
        CreatedVehicleDTO dto = new CreatedVehicleDTO();
        dto.setId(vehicle.getId());
        dto.setVehicleModel(vehicle.getModel());
        dto.setVehicleType(vehicle.getType());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setSeatCount(vehicle.getSeatCount());
        dto.setBabyTransport(vehicle.isBabyTransport());
        dto.setPetTransport(vehicle.isPetTransport());
        return dto;
    }
}