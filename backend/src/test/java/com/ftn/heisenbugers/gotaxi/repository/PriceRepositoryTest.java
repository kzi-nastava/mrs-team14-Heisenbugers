package com.ftn.heisenbugers.gotaxi.repository;

import com.ftn.heisenbugers.gotaxi.models.Price;
import com.ftn.heisenbugers.gotaxi.models.enums.VehicleType;
import com.ftn.heisenbugers.gotaxi.repositories.PriceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
public class PriceRepositoryTest {

    @Autowired
    private PriceRepository priceRepository;

    @Test
    @DisplayName("Should return starting price for the given vehicle type")
    void shouldReturnStartingPriceForGivenVehicleType() {
        Price p = new Price();
        p.setVehicleType(VehicleType.STANDARD);
        p.setStartingPrice(200.0);
        priceRepository.save(p);

        double starting = priceRepository.getStartingPriceByVehicleType(VehicleType.STANDARD);

        assertThat(starting).isEqualTo(200.0);
    }

    @Test
    @DisplayName("Should throw EmptyResultDataAccessException when no price exists for vehicle type")
    void shouldThrowWhenNoPriceForVehicleType() {
        assertThatThrownBy(() -> priceRepository.getStartingPriceByVehicleType(VehicleType.LUXURY))
                .isInstanceOf(org.springframework.aop.AopInvocationException.class);
    }

    @Test
    @DisplayName("Should throw IncorrectResultSizeDataAccessException when multiple prices exist for same vehicle type")
    void shouldThrowWhenMultiplePricesExistForSameVehicleType() {
        Price p1 = new Price();
        p1.setVehicleType(VehicleType.VAN);
        p1.setStartingPrice(150.0);
        priceRepository.save(p1);

        Price p2 = new Price();
        p2.setVehicleType(VehicleType.VAN);
        p2.setStartingPrice(160.0);
        priceRepository.save(p2);

        assertThatThrownBy(() -> priceRepository.getStartingPriceByVehicleType(VehicleType.VAN))
                .isInstanceOf(IncorrectResultSizeDataAccessException.class);
    }

}
