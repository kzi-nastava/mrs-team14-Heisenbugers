package com.ftn.heisenbugers.gotaxi.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

/**
 * Location entity representing coordinates and address.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location extends BaseEntity {

    private double latitude;

    private double longitude;

    private String address;
}
