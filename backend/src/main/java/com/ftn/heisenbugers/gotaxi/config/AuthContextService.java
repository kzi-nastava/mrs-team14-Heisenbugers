package com.ftn.heisenbugers.gotaxi.config;

import com.ftn.heisenbugers.gotaxi.models.Administrator;
import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Passenger;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.security.InvalidUserType;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class AuthContextService {

    public static User getCurrentUser() throws InvalidUserType {
        Object sub = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        if (sub instanceof User user) {
            return user;
        } else {
            throw new InvalidUserType("Subject is not a User");
        }
    }

    public static Driver getCurrentDriver() throws InvalidUserType {
        Object sub = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        if (sub instanceof Driver driver) {
            return driver;
        } else {
            throw new InvalidUserType("Subject is not a Driver");
        }
    }

    public static Administrator getCurrentAdmin() throws InvalidUserType {
        Object sub = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        if (sub instanceof Administrator admin) {
            return admin;
        } else {
            throw new InvalidUserType("Subject is not an Admin");
        }
    }

    public static Passenger getCurrentPassenger() throws InvalidUserType {
        Object sub = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        if (sub instanceof Passenger passenger) {
            return passenger;
        } else {
            throw new InvalidUserType("Subject is not a Passenger");
        }
    }
}
