package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.DriverRideHistoryDTO;
import com.example.gotaximobile.models.dtos.GetProfileDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProfileService {
    @GET("api/profile/me")
    Call<GetProfileDTO> getProfileInfo();

    @GET("api/drivers/history")
    Call<List<DriverRideHistoryDTO>> getDriverRideHistory(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("sortBy") String sortBy,
            @Query("direction") String direction
    );
}
