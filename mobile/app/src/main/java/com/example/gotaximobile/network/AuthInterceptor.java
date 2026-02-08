package com.example.gotaximobile.network;

import com.example.gotaximobile.data.TokenStorage;
import com.example.gotaximobile.models.dtos.RegisterResponseDTO;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final TokenStorage storage;

    public AuthInterceptor(TokenStorage storage){
        this.storage = storage;
    }

    @Override
    public Response intercept (Chain chain) throws IOException{
        Request original = chain.request();

        String path = original.url().encodedPath();
        if (path.startsWith("/api/auth/")) {
            return chain.proceed(original);
        }

        String authHeader = storage.getAuthHeaderValue();
        if (authHeader == null || authHeader.isEmpty()) {
            return chain.proceed(original);
        }

        Request newReq = original.newBuilder()
                .header("Authorization", authHeader)
                .build();

        return chain.proceed(newReq);
    }
}


