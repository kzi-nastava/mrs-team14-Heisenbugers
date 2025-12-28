package com.example.gotaximobile.fragments.placeholder;

import com.example.gotaximobile.models.Driver;
import com.example.gotaximobile.models.Ride;
import com.example.gotaximobile.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlaceholderHistoryList {
    public static final List<Ride> ITEMS = new ArrayList<Ride>();
    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createPlaceholderItem(i));
        }
    }

    private static void addItem(Ride item) {
        ITEMS.add(item);
    }

    private static Ride createPlaceholderItem(int i) {
        if (i % 2 == 1)
            return new Ride(
                    createDummyDriver(),
                    "123 Main St, Downtown",
                    "456 Park Ave, Uptown",
                    new Date(),
                    new Date(System.currentTimeMillis() + 1800000),
                    350.00,
                    4.5,
                    5.0,
                    false,
                    createDummyUsers(),
                    new ArrayList<>(),
                    false
            );
        else
            return new Ride(
                    createDummyDriver(),
                    "Airport Terminal A",
                    "Grand Hotel & Resort",
                    new Date(System.currentTimeMillis() - 7200000), // 2 hours ago
                    new Date(System.currentTimeMillis() - 6600000), // 1 hour 50 mins ago
                    280.50,
                    4.2,
                    5.0,
                    false,
                    createDummyUsers(),
                    createDummyViolations(),
                    true  // Panic button was pressed
            );

    }

    private static Driver createDummyDriver() {
        return new Driver("Driver", "Driveric");
    }

    private static List<User> createDummyUsers() {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("User1", "Useric"));
        users.add(new User("User2", "Useric"));
        users.add(new User("User3", "Useric"));
        return users;
    }

    private static List<String> createDummyViolations() {
        ArrayList<String> violations = new ArrayList<>();
        violations.add("Some violation 1");
        violations.add("Some violation 2");
        violations.add("Some violation 3");
        return violations;
    }
}
