package com.ftn.heisenbugers.gotaxi.models;

import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "prices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Price extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    private double startingPrice;
}
