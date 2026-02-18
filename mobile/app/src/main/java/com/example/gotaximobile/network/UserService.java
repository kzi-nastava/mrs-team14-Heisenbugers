package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.RideHistoryDTO;
import com.example.gotaximobile.models.dtos.UserStateDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserService {

    @GET("api/users/state")
    Call<UserStateDTO> getUserState();

    @GET("api/users/history")
    Call<List<RideHistoryDTO>> getHistory(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("sortBy") String sortBy,
            @Query("direction") String direction
    );
}
