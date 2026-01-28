package com.ftn.heisenbugers.gotaxi.models;

import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverProfileRequest extends BaseEntity{
    @Builder.Default
    private boolean approved = false;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String email;

    private String phone;

    private String address;

    private String profileImageUrl;

    @NotBlank
    private String model;

    @Enumerated(EnumType.STRING)
    @NotNull
    private VehicleType type;

    @NotBlank
    private String licensePlate;

    @NotNull
    private int seatCount;

    @NotNull
    @Builder.Default
    private boolean babyTransport = false;

    @NotNull
    @Builder.Default
    private boolean petTransport = false;
}
