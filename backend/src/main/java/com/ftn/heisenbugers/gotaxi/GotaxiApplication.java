package com.ftn.heisenbugers.gotaxi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GotaxiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GotaxiApplication.class, args);
	}

}
