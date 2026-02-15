package com.example.gotaximobile.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.gotaximobile.BuildConfig;
import com.example.gotaximobile.models.NotificationModel;
import com.example.gotaximobile.utils.NotificationUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class NotificationService {

    private StompClient stompClient;
    private final Context context;
    private final String baseUrl = BuildConfig.BASE_URL + "ws/websocket"; // emulator -> localhost
    private final String headerValue;

    public NotificationService(Context context, String headerValue) {
        this.context = context;
        this.headerValue = headerValue;
    }

    public void connect() {


        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", headerValue));

        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("Authorization", headerValue);

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, baseUrl, httpHeaders);

        stompClient.connect(headers);

        Disposable disposable = stompClient.topic("/user/queue/notifications")
                .subscribe(topicMessage -> {
                            NotificationModel notification =
                                    new Gson().fromJson(topicMessage.getPayload(), NotificationModel.class);

                            showSystemNotification(notification.getTitle(), notification.getMessage());
                        }/*

                        throwable -> {
                            Log.e("STOMP", "Subscription error", throwable);
                        }
                        */


                );
    }

    private void showSystemNotification(String title, String message) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, NotificationUtils.CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
    }
}

