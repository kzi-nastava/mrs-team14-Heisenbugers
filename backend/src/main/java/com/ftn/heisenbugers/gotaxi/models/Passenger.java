package com.ftn.heisenbugers.gotaxi.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Passenger extends User {

    @OneToMany(mappedBy = "passengers", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ride> rides;
}
