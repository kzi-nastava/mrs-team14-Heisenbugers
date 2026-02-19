package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.PriceDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PriceService {
    @GET("api/prices")
    Call<List<PriceDTO>> getPrices();
}
