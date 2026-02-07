package com.example.gotaximobile.models.enums;

public enum SortDirection {
    ASCENDING("asc"),
    DESCENDING("desc");

    final String key;

    SortDirection(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
