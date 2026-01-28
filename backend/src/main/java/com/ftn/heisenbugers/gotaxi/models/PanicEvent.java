package com.ftn.heisenbugers.gotaxi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "panic_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PanicEvent extends BaseEntity {

    @NotNull
    private boolean resolved;

    @OneToOne
    @JoinColumn(name = "ride_id")
    @NotNull
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrator_id")
    private Administrator handledBy;
}
