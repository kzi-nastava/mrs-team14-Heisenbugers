package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.PriceDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AdminApi {

    @GET("api/admin/prices")
    Call<List<PriceDTO>> getPrices();

    @POST("api/admin/prices")
    Call<Void> savePrices(@Body List<PriceDTO> prices);
}
