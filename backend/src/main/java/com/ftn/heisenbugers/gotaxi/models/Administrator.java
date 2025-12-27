package com.ftn.heisenbugers.gotaxi.models;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Administrator extends User {
    // Additional admin-specific fields can be added
}
