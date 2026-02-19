package com.example.gotaximobile.network;

import android.content.Context;

import com.example.gotaximobile.BuildConfig;
import com.example.gotaximobile.adapters.LocalDateTimeAdapter;
import com.example.gotaximobile.data.TokenStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = BuildConfig.BASE_URL;
    //private static final String BASE_URL = "http://192.168.0.10:8081/";

    public static Retrofit get(Context context) {
        if (retrofit != null)
            return retrofit;

        TokenStorage storage = new TokenStorage(context.getApplicationContext());

        HttpLoggingInterceptor log = new HttpLoggingInterceptor();
        log.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient ok = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor(storage))
                .addInterceptor(log)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(ok)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;

    }

    public static AuthApi authApi(Context context) {
        return get(context).create(AuthApi.class);
    }

    public static ProfileService profileService(Context context) {
        return get(context).create(ProfileService.class);
    }

    public static RideService rideService(Context context) {
        return get(context).create(RideService.class);
    }

    public static UserService userService(Context context) {
        return get(context).create(UserService.class);
    }

    public static ChatApi chatApi(Context context) {
        return get(context).create(ChatApi.class);
    }
      
    public static AdminApi adminApi(Context context) {
        return get(context).create(AdminApi.class);
    }

    public static PublicService publicService(Context context) {
        return get(context).create(PublicService.class);
    }

    public static MapService mapService(Context context){
        return  get(context).create(MapService.class);
    }

    public static PriceService priceService(Context context){
        return  get(context).create(PriceService.class);
    }


    public static AdminPanicApi adminPanicApi(Context context) {
        return get(context).create(AdminPanicApi.class);
    }

    public static PanicApi panicApi(Context context) {
        return get(context).create(PanicApi.class);
    }

    public static RideAnalyticsService rideAnalyticsService(Context context){
        return  get(context).create(RideAnalyticsService.class);
    }
      
    public static DriverService driverService(Context context) {
        return get(context).create(DriverService.class);
    }
}
