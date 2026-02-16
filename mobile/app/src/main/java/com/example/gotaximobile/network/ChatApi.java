package com.example.gotaximobile.network;

import com.example.gotaximobile.models.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ChatApi {

    @GET("/api/me/chat/{chatId}/full")
    Call<List<Message>> loadMessages(
            @Header("Authorization") String token,
            @Path("chatId") String chatId
    );
}
