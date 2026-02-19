package com.example.gotaximobile.services;

import android.util.Log;

import com.example.gotaximobile.BuildConfig;
import com.example.gotaximobile.models.Message;
import com.example.gotaximobile.models.dtos.MessageOutDTO;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompHeader;

public class ChatWebSocketService {

    private StompClient stompClient;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final Gson gson = new Gson();

    private final String WS_URL = BuildConfig.BASE_URL + "ws/websocket";

    public void connect(String jwtToken, String chatId, MessageListener listener) {

        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("Authorization", "Bearer " + jwtToken);

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_URL, httpHeaders);

        stompClient.withClientHeartbeat(10000).withServerHeartbeat(10000);

        disposables.add(
                stompClient.lifecycle().subscribe(event -> {
                    if (event.getType() == LifecycleEvent.Type.OPENED) {
                        Log.d("STOMP", "Connected");

                        subscribe(chatId, listener);
                    } else if (event.getType() == LifecycleEvent.Type.ERROR) {
                        Log.e("STOMP", "Error", event.getException());
                    }
                })
        );

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", "Bearer " + jwtToken));

        stompClient.connect(headers);
    }

    private void subscribe(String chatId, MessageListener listener) {

        String destinationPath =
                chatId != null ? "/topic/admin/chat/" + chatId : "/user/queue/messages";

        disposables.add(
                stompClient.topic(destinationPath)
                        .subscribe(topicMessage -> {

                            MessageOutDTO dto = gson.fromJson(
                                    topicMessage.getPayload(),
                                    MessageOutDTO.class
                            );
                            Message message = new Message(dto.content, dto.from, LocalDateTime.parse(dto.sentAt));
                            listener.onMessageReceived(message);

                        }, throwable -> Log.e("STOMP", "Subscribe error", throwable))
        );
    }

    public void sendMessage(Message message, String chatId) {

        MessageOutDTO outDTO = new MessageOutDTO();
        outDTO.chatId = chatId;
        outDTO.content = message.content;
        outDTO.from = message.from;
        outDTO.sentAt = message.sentAt.toString();


        String payload = gson.toJson(outDTO);

        stompClient.send("/app/sendMessage", payload)
                .subscribe(
                        () -> Log.d("STOMP", "Sent"),
                        throwable -> Log.e("STOMP", "Send error", throwable)
                );
    }

    public void disconnect() {
        disposables.dispose();
        if (stompClient != null) {
            stompClient.disconnect();
        }
    }

    public interface MessageListener {
        void onMessageReceived(Message message);
    }
}

