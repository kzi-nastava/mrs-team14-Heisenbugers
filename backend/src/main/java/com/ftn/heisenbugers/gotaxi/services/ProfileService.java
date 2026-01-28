package com.ftn.heisenbugers.gotaxi.services;

import com.ftn.heisenbugers.gotaxi.config.AuthContextService;
import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.DriverProfileRequest;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.Vehicle;
import com.ftn.heisenbugers.gotaxi.models.dtos.ChangePasswordDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.CreatedVehicleDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.GetProfileDTO;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import com.ftn.heisenbugers.gotaxi.repositories.DriverProfileRequestRepository;
import com.ftn.heisenbugers.gotaxi.repositories.DriverRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import com.ftn.heisenbugers.gotaxi.repositories.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DriverRepository driverRepository;
    private final ImageStorageService imageStorageService;
    private final DriverProfileRequestRepository driverProfileRequestRepository;

    @Value("${app.default.avatar-path:/images/default-avatar.png}")
    private String defaultAvatarPath;

    public ProfileService(UserRepository userRepository, PasswordEncoder passwordEncoder, DriverRepository driverRepository,
                          ImageStorageService imageStorageService, DriverProfileRequestRepository driverProfileRequestRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.driverRepository = driverRepository;
        this.imageStorageService = imageStorageService;
        this.driverProfileRequestRepository = driverProfileRequestRepository;
    }

    public GetProfileDTO getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return mapToDto(user);
    }

    public GetProfileDTO updateProfile(String email, GetProfileDTO request, MultipartFile image) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String profilePath = null;
        if (image != null && !image.isEmpty()) {
            profilePath = imageStorageService.saveProfileImage(image);

        }

        try{
            Driver currentDriver = AuthContextService.getCurrentDriver();
            Driver driver = driverRepository.findByUserEmailWithVehicle(currentDriver.getEmail())
                    .orElseThrow(() -> new EntityNotFoundException("Driver not found"));
            DriverProfileRequest changeRequest = new DriverProfileRequest();
            changeRequest.setEmail(request.getEmail());
            changeRequest.setFirstName(request.getFirstName());
            changeRequest.setLastName(request.getLastName());
            changeRequest.setPhone(request.getPhoneNumber());
            changeRequest.setAddress(request.getAddress());
            changeRequest.setProfileImageUrl(profilePath != null ? profilePath : driver.getProfileImageUrl());
            changeRequest.setModel(driver.getVehicle().getModel());
            changeRequest.setType(driver.getVehicle().getType());
            changeRequest.setLicensePlate(driver.getVehicle().getLicensePlate());
            changeRequest.setSeatCount(driver.getVehicle().getSeatCount());
            changeRequest.setBabyTransport(driver.getVehicle().isBabyTransport());
            changeRequest.setPetTransport(driver.getVehicle().isPetTransport());

            driverProfileRequestRepository.save(changeRequest);
        } catch (InvalidUserType e) {
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhone(request.getPhoneNumber());
            user.setAddress(request.getAddress());
            user.setProfileImageUrl(profilePath != null ? profilePath : user.getProfileImageUrl());
            User savedUser = userRepository.save(user);
            return mapToDto(savedUser);
        }

        return mapToDto(user);
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

        DriverProfileRequest changeRequest = new DriverProfileRequest();

        changeRequest.setEmail(driver.getEmail());
        changeRequest.setFirstName(driver.getFirstName());
        changeRequest.setLastName(driver.getLastName());
        changeRequest.setPhone(driver.getPhone());
        changeRequest.setAddress(driver.getAddress());
        changeRequest.setProfileImageUrl(driver.getProfileImageUrl());
        changeRequest.setModel(request.getVehicleModel());
        changeRequest.setType(request.getVehicleType());
        changeRequest.setLicensePlate(request.getLicensePlate());
        changeRequest.setSeatCount(request.getSeatCount());
        changeRequest.setBabyTransport(request.isBabyTransport());
        changeRequest.setPetTransport(request.isPetTransport());

        driverProfileRequestRepository.save(changeRequest);

        return mapToDto(driver.getVehicle());
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
        if(Objects.equals(user.getProfileImageUrl(), "/images/default-avatar.png")){
            dto.setProfileImageUrl("http://localhost:8081" + user.getProfileImageUrl());
        }else{
            dto.setProfileImageUrl("http://localhost:8081" + user.getProfileImageUrl());
        }
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