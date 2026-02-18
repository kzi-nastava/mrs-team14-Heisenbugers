package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.RideEstimateRequestDTO;
import com.example.gotaximobile.models.dtos.RideEstimateResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PublicService {
    @POST("api/public/ride-estimates")
    Call<RideEstimateResponseDTO> estimateRide(@Body RideEstimateRequestDTO body);
}