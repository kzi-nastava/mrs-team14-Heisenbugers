package com.ftn.heisenbugers.gotaxi.utils;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OsrmRouteService {

    private static final String BASE_URL = "https://router.project-osrm.org/route/v1/driving/";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static List<Point> getRoutePoints(List<Point> waypoints) throws Exception {
        if (waypoints == null || waypoints.size() < 2) {
            throw new IllegalArgumentException("At least start and end points are required.");
        }

        StringBuilder coords = new StringBuilder();
        for (int i = 0; i < waypoints.size(); i++) {
            Point p = waypoints.get(i);
            coords.append(p.getLongitude())
                    .append(",")
                    .append(p.getLatitude());
            if (i < waypoints.size() - 1) {
                coords.append(";");
            }
        }

        String urlString = BASE_URL + coords + "?overview=full&geometries=geojson";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (InputStream is = conn.getInputStream()) {
            JsonNode root = MAPPER.readTree(is);
            JsonNode coordinates = root
                    .path("routes")
                    .get(0)
                    .path("geometry")
                    .path("coordinates");

            List<Point> result = new ArrayList<>();
            for (JsonNode coord : coordinates) {
                double lon = coord.get(0).asDouble();
                double lat = coord.get(1).asDouble();
                result.add(new Point(lat, lon));
            }
            return result;
        }
    }

    @Getter
    public static class Point {
        private final double latitude;
        private final double longitude;

        public Point(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

    }
}
