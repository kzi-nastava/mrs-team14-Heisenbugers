package com.ftn.heisenbugers.gotaxi.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "trafficviolations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrafficViolation extends BaseEntity {

    @NotNull
    private String title;

    private String description;

    @ManyToOne
    @JoinColumn(name = "ride_id")
    @NotNull
    private Ride ride;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    @NotNull
    private User reporter;

}
