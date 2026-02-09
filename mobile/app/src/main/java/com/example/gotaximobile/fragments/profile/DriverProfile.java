package com.example.gotaximobile.fragments.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.CreateVehicleDTO;
import com.example.gotaximobile.models.dtos.GetProfileDTO;
import com.example.gotaximobile.network.RetrofitClient;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverProfile extends Fragment {

    private ProfileCardView activeTodayCard, modelCard, typeCard, plateNoCard, seatsCard, babiesAllowedCard, petsAllowedCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_profile, container, false);

        activeTodayCard = view.findViewById(R.id.card_active_hours);
        modelCard = view.findViewById(R.id.card_model);
        typeCard = view.findViewById(R.id.card_type);
        plateNoCard = view.findViewById(R.id.card_plate_no);
        seatsCard = view.findViewById(R.id.card_seats);
        babiesAllowedCard = view.findViewById(R.id.card_babies_allowed);
        petsAllowedCard = view.findViewById(R.id.card_pets_allowed);

        fetchActiveHours();
        fetchVehicleInfo();

        return view;
    }

    private void fetchActiveHours(){
        RetrofitClient.profileService(getContext()).getDriverActiveHours().enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int time = response.body();
                    activeTodayCard.setData("Active In Last 24H", String.valueOf(time), R.drawable.ic_active_time);
                } else {
                    Log.e("API_ERROR", "Response failed: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Log.e("NETWORK_ERROR", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void fetchVehicleInfo(){
        RetrofitClient.profileService(getContext()).getDriverVehicle().enqueue(new Callback<CreateVehicleDTO>() {
            @Override
            public void onResponse(@NonNull Call<CreateVehicleDTO> call, @NonNull Response<CreateVehicleDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CreateVehicleDTO vehicle = response.body();
                    updateView(vehicle);
                } else {
                    Log.e("API_ERROR", "Response failed: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreateVehicleDTO> call, @NonNull Throwable t) {
                Log.e("NETWORK_ERROR", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void updateView(CreateVehicleDTO vehicle) {
        modelCard.setData("Model", vehicle.vehicleModel, R.drawable.ic_car);
        typeCard.setData("Type", vehicle.vehicleType, R.drawable.ic_type);
        plateNoCard.setData("Plate No.", vehicle.licensePlate, R.drawable.ic_license_plate);
        seatsCard.setData("Seats", String.valueOf(vehicle.seatCount), R.drawable.ic_car_seat);
        babiesAllowedCard.setData("Baby Allowed", vehicle.babyTransport ? "Yes" : "No", R.drawable.ic_baby);
        petsAllowedCard.setData("Pets Allowed", vehicle.petTransport ? "Yes" : "No", R.drawable.ic_pets);

        Bundle bundle = new Bundle();
        bundle.putString("model", vehicle.vehicleModel);
        bundle.putString("type", vehicle.vehicleType);
        bundle.putString("plate", vehicle.licensePlate);
        bundle.putString("seats", String.valueOf(vehicle.seatCount));
        bundle.putBoolean("babies", vehicle.babyTransport);
        bundle.putBoolean("pets", vehicle.petTransport);

        getParentFragmentManager().setFragmentResult("vehicleInfo", bundle);

    }
}