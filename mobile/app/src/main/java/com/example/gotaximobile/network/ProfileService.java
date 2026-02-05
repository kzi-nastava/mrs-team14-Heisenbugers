package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.GetProfileDTO;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ProfileService {
    @GET("api/profile/me")
    Call<GetProfileDTO> getProfileInfo();
}
