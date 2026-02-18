package com.ftn.heisenbugers.gotaxi.controllers;

import com.ftn.heisenbugers.gotaxi.models.Price;
import com.ftn.heisenbugers.gotaxi.models.dtos.PriceDTO;
import com.ftn.heisenbugers.gotaxi.repositories.PriceRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin/prices")
public class PriceController {

    private final PriceRepository priceRepository;

    public PriceController(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    @GetMapping("")
    public List<PriceDTO> getPrices() {
        List<Price> prices = priceRepository.findAll();
        return prices.stream()
                .map(p -> new PriceDTO(p.getVehicleType(), p.getStartingPrice()))
                .toList();
    }

    @PostMapping("")
    public void updatePrices(@RequestBody ArrayList<PriceDTO> priceDTOs) {
        List<Price> prices = priceDTOs.stream()
                .map(dto -> new Price(dto.vehicleType, dto.startingPrice))
                .toList();
        priceRepository.saveAll(prices);
    }
}
