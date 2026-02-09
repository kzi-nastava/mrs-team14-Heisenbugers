package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.RideDTO;
import com.example.gotaximobile.models.dtos.RideTrackingDTO;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RideService {

    @GET("api/rides/{id}")
    Call<RideDTO> getRide(@Path("id") UUID id);

    @GET("api/rides/{id}/tracking")
    Call<RideTrackingDTO> getRideTracking(@Path("id") UUID id);


}
