package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.ChangePasswordDTO;
import com.example.gotaximobile.models.dtos.CreateVehicleDTO;
import com.example.gotaximobile.models.dtos.DriverProfileRequestDetailDTO;
import com.example.gotaximobile.models.dtos.DriverRequestListDTO;
import com.example.gotaximobile.models.dtos.DriverRideHistoryDTO;
import com.example.gotaximobile.models.dtos.GetProfileDTO;

import okhttp3.MultipartBody;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProfileService {
    @GET("api/profile/me")
    Call<GetProfileDTO> getProfileInfo();

    @GET("api/profile/me/driver")
    Call<Integer> getDriverActiveHours();

    @GET("api/profile/me/vehicle")
    Call<CreateVehicleDTO> getDriverVehicle();

    @Multipart
    @PUT("api/profile/me")
    Call<GetProfileDTO> updatePersonalInfromation(
            @Part("data") GetProfileDTO profileDTO,
            @Part MultipartBody.Part profileImage
    );

    @PUT("api/profile/me/vehicle")
    Call<CreateVehicleDTO> updateVehicle(@Body CreateVehicleDTO body);

    @PUT("api/profile/me/password")
    Call<Void> changePassword(@Body ChangePasswordDTO body);

    @GET("api/driver-requests")
    Call<List<DriverRequestListDTO>> getDriverRequests();

    @GET("api/driver-requests/{id}")
    Call<DriverProfileRequestDetailDTO> getDriverRequest(@Path("id") UUID id);

    @POST("api/driver-requests/{id}/approve")
    Call<Void> approveRequest(@Path("id") UUID id);

    @POST("api/driver-requests/{id}/reject")
    Call<Void> rejectRequest(@Path("id") UUID id);

    @GET("api/drivers/history")
    Call<List<DriverRideHistoryDTO>> getDriverRideHistory(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("sortBy") String sortBy,
            @Query("direction") String direction
    );
}
