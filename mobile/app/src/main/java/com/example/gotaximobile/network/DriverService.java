package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.DriverWorkingDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface DriverService {
    @GET("/api/driver/me/working")
    Call<DriverWorkingDTO> getMyWorkingState();

    @PUT("/api/driver/me/working")
    Call<DriverWorkingDTO> setMyWorkingState(@Body DriverWorkingDTO body);

}
