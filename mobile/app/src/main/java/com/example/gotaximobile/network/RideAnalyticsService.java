package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.RideAnalyticsResponseDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RideAnalyticsService {

    @GET("api/analytics/rides")
    Call<RideAnalyticsResponseDTO> getRideAnalytics(
            @Query("start") String start,
            @Query("end") String end,
            @Query("role") String role,
            @Query("userId") String userId,
            @Query("aggregate") boolean aggregate
    );
}
