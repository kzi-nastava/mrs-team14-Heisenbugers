package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.MessageResponse;
import com.example.gotaximobile.models.dtos.PanicRequestDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PanicApi {
    @POST("api/rides/{rideId}/panic")
    Call<MessageResponse> panic(@Path("rideId") String rideId,
                                @Body PanicRequestDTO body);
}
