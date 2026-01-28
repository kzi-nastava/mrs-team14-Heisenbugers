package com.ftn.heisenbugers.gotaxi.utils;

import java.util.Base64;

public class GeoHasher {
    // Encode latitude and longitude list to a compact string
    public static String geohash(double[][] coordinates) {
        StringBuilder binary = new StringBuilder();
        for (double[] coord : coordinates) {
            binary.append(encodeCoord(coord[0], -90, 90, 32)); // latitude
            binary.append(encodeCoord(coord[1], -180, 180, 32)); // longitude
        }
        // Convert binary string to bytes
        int byteLength = (binary.length() + 7) / 8;
        byte[] bytes = new byte[byteLength];
        for (int i = 0; i < binary.length(); i++) {
            if (binary.charAt(i) == '1') bytes[i / 8] |= (byte) (1 << (7 - (i % 8)));
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String encodeCoord(double value, double min, double max, int bits) {
        StringBuilder bin = new StringBuilder();
        for (int i = 0; i < bits; i++) {
            double mid = (min + max) / 2;
            if (value >= mid) {
                bin.append('1');
                min = mid;
            } else {
                bin.append('0');
                max = mid;
            }
        }
        return bin.toString();
    }

    public static double[][] decodeGeohash(String hash, int numCoords) {
        byte[] bytes = Base64.getUrlDecoder().decode(hash);
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            for (int i = 7; i >= 0; i--) {
                binary.append((b >> i) & 1);
            }
        }

        double[][] coords = new double[numCoords][2];
        int bitsPerCoord = 32;
        int index = 0;
        for (int i = 0; i < numCoords; i++) {
            coords[i][0] = decodeCoord(binary.substring(index, index + bitsPerCoord), -90, 90);
            index += bitsPerCoord;
            coords[i][1] = decodeCoord(binary.substring(index, index + bitsPerCoord), -180, 180);
            index += bitsPerCoord;
        }
        return coords;
    }

    private static double decodeCoord(String bin, double min, double max) {
        for (char c : bin.toCharArray()) {
            double mid = (min + max) / 2;
            if (c == '1') {
                min = mid;
            } else {
                max = mid;
            }
        }
        return (min + max) / 2;
    }

}
