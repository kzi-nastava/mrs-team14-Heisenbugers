package com.example.gotaximobile.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

public class NotificationUtils {

    public static final String CHANNEL_ID = "ride_notifications";

    public static void createChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Ride Notifications",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Notifications from server");

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }
}
