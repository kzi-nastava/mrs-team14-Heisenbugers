package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.GeocodeSuggestionDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MapService {
    @GET("api/geocode/search")
    Call<List<GeocodeSuggestionDTO>> searchStreet(@Query("q") String street);
}
