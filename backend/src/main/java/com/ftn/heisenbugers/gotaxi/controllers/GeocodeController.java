package com.ftn.heisenbugers.gotaxi.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/geocode")
public class GeocodeController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/reverse")
    public ResponseEntity<String> reverse(
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        String url = "https://nominatim.openstreetmap.org/reverse" +
                "?format=json&lat=" + lat + "&lon=" + lon;

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "GoTaxi/1.0 (gotaximrs@gmail.com)");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );
    }

    @GetMapping("/search")
    public ResponseEntity<String> search(
            @RequestParam String q
    ) {
        System.out.println(q);
        String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + q;

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "GoTaxi/1.0 (contact@yourdomain.com)");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );
    }
}