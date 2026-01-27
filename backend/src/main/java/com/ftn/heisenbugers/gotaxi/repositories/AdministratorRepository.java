package com.ftn.heisenbugers.gotaxi.repositories;

import com.ftn.heisenbugers.gotaxi.models.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdministratorRepository extends JpaRepository<Administrator, UUID> {}
