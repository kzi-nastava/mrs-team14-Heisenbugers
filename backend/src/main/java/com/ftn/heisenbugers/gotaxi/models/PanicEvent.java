package com.ftn.heisenbugers.gotaxi.models;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "panic_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PanicEvent extends BaseEntity {

    private boolean resolved;

    @OneToOne
    @JoinColumn(name = "ride_id")
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrator_id")
    private Administrator handledBy;
}
