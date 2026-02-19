package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.MessageResponse;
import com.example.gotaximobile.models.dtos.PanicEventDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AdminPanicApi {
    @GET("api/admin/panic/active")
    Call<List<PanicEventDTO>> getActivePanics();

    @POST("api/admin/panic/{panicId}/resolve")
    Call<MessageResponse> resolve(@Path("panicId") String panicId);
}
