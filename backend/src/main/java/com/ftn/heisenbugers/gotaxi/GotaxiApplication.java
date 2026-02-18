package com.ftn.heisenbugers.gotaxi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableAsync
public class GotaxiApplication {

    public static void main(String[] args) {
        SpringApplication.run(GotaxiApplication.class, args);
    }

}
