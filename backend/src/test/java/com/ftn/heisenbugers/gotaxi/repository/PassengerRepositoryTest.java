package com.ftn.heisenbugers.gotaxi.repository;

import com.ftn.heisenbugers.gotaxi.models.Passenger;
import com.ftn.heisenbugers.gotaxi.repositories.PassengerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class PassengerRepositoryTest {

    @Autowired
    private PassengerRepository passengerRepository;

    @Test
    @DisplayName("Should find passenger when a valid email is provided")
    void shouldFindPassengerByEmail() {
        String email = "passenger@gmail.com";
        Passenger passenger = new Passenger(email, "hash", "Passenger", "Test", "0601234566", "Address 1" );
        passengerRepository.save(passenger);

        Optional<Passenger> foundPassenger = passengerRepository.findByEmail(email);

        assertThat(foundPassenger).isPresent();

        assertThat(foundPassenger.get()).usingRecursiveComparison().ignoringFields("id").isEqualTo(passenger);
    }

    @Test
    @DisplayName("Should return empty Optional when email does not exist")
    void shouldReturnEmptyWhenEmailNotFound() {
        String nonExistentEmail = "nonexistent@example.com";

        Optional<Passenger> result = passengerRepository.findByEmail(nonExistentEmail);

        assertThat(result).isEmpty();
    }

}
