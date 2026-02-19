package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Price;
import com.ftn.heisenbugers.gotaxi.models.dtos.PriceDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import com.ftn.heisenbugers.gotaxi.repositories.PriceRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prices")
@AllArgsConstructor
public class PriceUserController {

    private final PriceRepository priceRepository;

    @GetMapping("")
    public List<PriceDTO> getPrices() {
        List<Price> prices = priceRepository.findAll();

        Map<VehicleType, Price> latestPrices = prices.stream()
                .collect(Collectors.toMap(
                        Price::getVehicleType,
                        Function.identity(),
                        (p1, p2) -> p1.getCreatedAt().isAfter(p2.getCreatedAt()) ? p1 : p2
                ));

        return latestPrices.values().stream()
                .map(p -> new PriceDTO(p.getVehicleType(), p.getStartingPrice()))
                .toList();
    }
}
