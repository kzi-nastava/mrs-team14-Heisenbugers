package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.ChangePasswordDTO;
import com.example.gotaximobile.models.dtos.CreateVehicleDTO;
import com.example.gotaximobile.models.dtos.DriverRideHistoryDTO;
import com.example.gotaximobile.models.dtos.GetProfileDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ProfileService {
    @GET("api/profile/me")
    Call<GetProfileDTO> getProfileInfo();

    @GET("api/profile/me/driver")
    Call<Integer> getDriverActiveHours();

    @GET("api/profile/me/vehicle")
    Call<CreateVehicleDTO> getDriverVehicle();

    @PUT("api/profile/me/password")
    Call<Void> changePassword(@Body ChangePasswordDTO body);
    
    @GET("api/drivers/history")
    Call<List<DriverRideHistoryDTO>> getDriverRideHistory(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("sortBy") String sortBy,
            @Query("direction") String direction
    );
}
