package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.AdminRideDetailsDTO;
import com.example.gotaximobile.models.dtos.AdminRideListItemDTO;
import com.example.gotaximobile.models.dtos.AdminUserListItemDTO;
import com.example.gotaximobile.models.dtos.PriceDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminApi {

    @GET("api/admin/prices")
    Call<List<PriceDTO>> getPrices();

    @POST("api/admin/prices")
    Call<Void> savePrices(@Body List<PriceDTO> prices);


    @GET("api/admin/users/drivers")
    Call<List<AdminUserListItemDTO>> getDrivers();

    @GET("api/admin/users/passengers")
    Call<List<AdminUserListItemDTO>> getPassengers();

    @GET("api/admin/rides")
    Call<List<AdminRideListItemDTO>> searchRides(
            @Query("driverId") String driverId,
            @Query("passengerId") String passengerId,
            @Query("status") String status,
            @Query("from") String from,
            @Query("to") String to,
            @Query("sort") String sort
    );

    @GET("api/admin/rides/{rideId}")
    Call<AdminRideDetailsDTO> getRideDetails(@Path("rideId") String rideId);
}
