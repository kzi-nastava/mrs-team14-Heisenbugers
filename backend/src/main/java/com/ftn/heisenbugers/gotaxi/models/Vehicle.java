package com.ftn.heisenbugers.gotaxi.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Vehicle entity associated with drivers.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle extends BaseEntity {

    @NotBlank
    private String model;

    @Enumerated(EnumType.STRING)
    @NotNull
    private VehicleType type;

    @NotBlank
    @Column(unique = true)
    private String licensePlate;

    @NotNull
    private int seatCount;

    @NotNull
    @Builder.Default
    private boolean babyTransport = false;

    @NotNull
    @Builder.Default
    private boolean petTransport = false;

    @OneToOne(mappedBy = "vehicle")
    @JsonBackReference
    private Driver driver;
}