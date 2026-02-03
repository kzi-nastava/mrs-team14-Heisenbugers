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
        String token = storage.getToken();

        if(token == null || token.isEmpty()){
            return chain.proceed(original);
        }
        Request withAuth = original.newBuilder()
                .addHeader("Authorization", "Bearer"+token)
                .build();
    return chain.proceed(withAuth);
    }

}
