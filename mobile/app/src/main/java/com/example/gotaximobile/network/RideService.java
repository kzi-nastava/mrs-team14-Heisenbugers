package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.RideDTO;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RideService {

    @GET("api/rides/{id}")
    Call<RideDTO> getRide(@Path("id") UUID id);
}
