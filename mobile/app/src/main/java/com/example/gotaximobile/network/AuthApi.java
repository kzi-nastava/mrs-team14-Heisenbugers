package com.example.gotaximobile.network;

import com.example.gotaximobile.models.dtos.ForgotPasswordRequestDTO;
import com.example.gotaximobile.models.dtos.ForgotPasswordResponseDTO;
import com.example.gotaximobile.models.dtos.LoginRequestDTO;
import com.example.gotaximobile.models.dtos.LoginResponseDTO;
import com.example.gotaximobile.models.dtos.MessageResponse;
import com.example.gotaximobile.models.dtos.RegisterResponseDTO;
import com.example.gotaximobile.models.dtos.ResetPasswordRequestDTO;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;
public interface AuthApi {
    @POST("api/auth/login")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO body);

    @Multipart
    @POST("api/auth/register")
    Call<RegisterResponseDTO> registerPassenger(
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("confirmPassword") RequestBody confirmPassword,
            @Part("firstName") RequestBody firstName,
            @Part("lastName") RequestBody lastName,
            @Part("phone") RequestBody phone,
            @Part("address") RequestBody address,
            @Part MultipartBody.Part profileImage // можно null
    );

    @POST("api/auth/forgot-password")
    Call<MessageResponse> forgotPassword(@Body ForgotPasswordRequestDTO body);

    @POST("api/auth/reset-password")
    Call<MessageResponse> resetPassword(@Body ResetPasswordRequestDTO body);

    @DELETE("api/auth/session")
    Call<MessageResponse> logout();

    @GET("api/auth/activate")
    Call<MessageResponse> activateAccount(@Query("token") String token);



}
