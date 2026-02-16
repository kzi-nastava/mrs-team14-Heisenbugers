package com.ftn.heisenbugers.gotaxi.models.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RideAnalyticsResponseDTO {
    private List<DailyItemDTO> daily;
    private TotalsDTO totals;
}
