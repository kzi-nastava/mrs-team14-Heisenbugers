package com.ftn.heisenbugers.gotaxi.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
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

    @ManyToMany(mappedBy = "passengers", fetch = FetchType.EAGER)
    private List<Ride> rides;
}
