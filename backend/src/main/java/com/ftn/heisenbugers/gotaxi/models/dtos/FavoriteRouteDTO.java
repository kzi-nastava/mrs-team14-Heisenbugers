package com.ftn.heisenbugers.gotaxi.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class FavoriteRouteDTO {
    @Getter @Setter
    private Long Id;
    @Getter @Setter
    private RouteDTO route;

}
