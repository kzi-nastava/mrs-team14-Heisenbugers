package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.UserStateDTO;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UserService {

    @GET("api/users/state")
    Call<UserStateDTO> getUserState();
}
