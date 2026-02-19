package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.BlockableUserDTO;
import com.example.gotaximobile.models.dtos.IsBlockedDTO;
import com.example.gotaximobile.models.dtos.RideHistoryDTO;
import com.example.gotaximobile.models.dtos.UserStateDTO;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
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

    @GET("api/users/blockable")
    Call<List<BlockableUserDTO>> getBlockableUsers();

    @POST("api/users/{id}/block")
    Call<Void> blockUser(@Path("id") String id, @Body String note);

    @POST("api/users/{id}/unblock")
    Call<Void> unblockUser(@Path("id") String id);

    @GET("api/users/is-blocked")
    Call<IsBlockedDTO> checkIsUserBlocked();
}
