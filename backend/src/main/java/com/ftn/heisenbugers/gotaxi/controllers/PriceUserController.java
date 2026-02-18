package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Price;
import com.ftn.heisenbugers.gotaxi.models.dtos.PriceDTO;
import com.ftn.heisenbugers.gotaxi.repositories.PriceRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/prices")
@AllArgsConstructor
public class PriceUserController {

    private final PriceRepository priceRepository;

    @GetMapping("")
    public List<PriceDTO> getPrices() {
        List<Price> prices = priceRepository.findAll();
        return prices.stream()
                .map(p -> new PriceDTO(p.getVehicleType(), p.getStartingPrice()))
                .toList();
    }
}
