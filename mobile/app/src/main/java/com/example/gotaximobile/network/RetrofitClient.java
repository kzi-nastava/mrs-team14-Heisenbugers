package com.example.gotaximobile.network;

import android.content.Context;

import com.example.gotaximobile.data.TokenStorage;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    // private static final String BASE_URL = "http://10.0.2.2:8081/";
    private static final String BASE_URL = "http://192.168.0.16:8081/";


    public static Retrofit get(Context context) {
        if (retrofit != null)
            return retrofit;

        TokenStorage storage = new TokenStorage(context.getApplicationContext());

        HttpLoggingInterceptor log = new HttpLoggingInterceptor();
        log.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient ok = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(storage))
                .addInterceptor(log)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(ok)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;

    }

    public static AuthApi authApi(Context context) {
        return get(context).create(AuthApi.class);
    }

    public static ProfileService profileService(Context context) {
        return get(context).create(ProfileService.class);
    }

}
