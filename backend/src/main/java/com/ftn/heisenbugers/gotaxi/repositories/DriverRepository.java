package com.ftn.heisenbugers.gotaxi.repositories;

import com.ftn.heisenbugers.gotaxi.models.Driver;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {




    @Modifying
    @Transactional
    @Query("""
update Driver d
set d.activeHoursLast24h =
  case when d.activeHoursLast24h < 1440 then d.activeHoursLast24h + 1 else 1440 end
where d.working = true
""")
    int incrementActiveMinutesCapped();

    @Query("""
        select d from Driver d
        join fetch d.vehicle
        where d.id = :id
    """)
    Optional<Driver> findByIdWithVehicle(UUID id);

    List<Driver> findByWorkingTrue();

    @Query("""
        SELECT d FROM Driver d
        JOIN FETCH d.vehicle
        WHERE d.email = :email
    """)
    Optional<Driver> findByUserEmailWithVehicle(String email);
}
