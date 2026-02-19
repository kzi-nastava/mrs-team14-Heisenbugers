package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.AdminRideDTO;
import com.example.gotaximobile.models.dtos.AssignedRideDTO;
import com.example.gotaximobile.models.dtos.FavoriteRouteDTO;
import com.example.gotaximobile.models.dtos.RideDTO;
import com.example.gotaximobile.models.dtos.RideRequestDTO;
import com.example.gotaximobile.models.dtos.RideDetailsDTO;
import com.example.gotaximobile.models.dtos.RideTrackingDTO;
import com.example.gotaximobile.models.dtos.VehicleInfoDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RideService {

    @GET("api/rides/{id}")
    Call<RideDTO> getRide(@Path("id") UUID id);

    @GET("api/rides/{id}/tracking")
    Call<RideTrackingDTO> getRideTracking(@Path("id") UUID id);

    @POST("api/rides/{id}/report")
    Call<Void> reportRide(
            @Path("id") UUID id,
            @Body Map<String, Object> body
    );

    @POST("api/rides/{id}/rate")
    Call<Map<String, String>> rateRide(
            @Path("id") UUID id,
            @Body Map<String, Object> body
    );

    @GET("api/public/vehicles")
    Call<List<VehicleInfoDTO>> getAllVehicles();

    @GET("api/admin/rides/all")
    Call<List<AdminRideDTO>> getAllRides();

    @POST("api/rides")
    Call<Void> createRide(@Body RideRequestDTO body);
    @POST("api/rides/{id}/cancel")
    Call<com.example.gotaximobile.models.dtos.MessageResponse> cancelRide(
            @Path("id") java.util.UUID id,
            @Body com.example.gotaximobile.models.dtos.CancelRideRequestDTO body
    );

    @GET("api/rides/{rideId}")
    Call<RideDetailsDTO> getRideDetails(@Path("rideId") String rideId);

    @GET("api/rides/me/active")
    Call<AssignedRideDTO> getAssignedRide();

    @POST("api/rides/{id}/start")
    Call<Void> startRide(@Path("id") UUID id);

    @GET("api/favorite-routes")
    Call<List<FavoriteRouteDTO>> getFavoriteRoutes();

    @DELETE("api/favorite-routes/{id}")
    Call<Void> deleteFavorite(@Path("id") UUID id);

    @POST("api/favorite-routes/{id}")
    Call<Void> addFavorite(@Path("id") UUID id);

    @DELETE("api/favorite-routes/{id}/ride")
    Call<Void> deleteFavoriteFromRide(@Path("id") UUID id);

}
