package com.example.gotaximobile.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.MapPin;
import com.example.gotaximobile.models.dtos.VehicleInfoDTO;
import com.example.gotaximobile.network.RetrofitClient;
import com.example.gotaximobile.network.RideService;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RideService rideService;
    private List<VehicleInfoDTO> vehicles;
    private List<MapPin> mapPins;
    private MapFragment mapFragment;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rideService = RetrofitClient.rideService(requireContext());
        queryVehicles();
        return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);


    }

    private void queryVehicles() {
        rideService.getAllVehicles().enqueue(new Callback<List<VehicleInfoDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<VehicleInfoDTO>> call,
                                   @NonNull Response<List<VehicleInfoDTO>> response) {
                vehicles = response.body();
                assert vehicles != null;
                mapPins = vehicles.stream().map(VehicleInfoDTO::toMapPin).toList();
                updateVehiclePins();
            }

            @Override
            public void onFailure(@NonNull Call<List<VehicleInfoDTO>> call, @NonNull Throwable t) {
                Log.e("NETWORK_ERROR", Objects.requireNonNull(t.getMessage()));
            }
        });
    }


    private void updateVehiclePins() {
        mapFragment.setPins(mapPins);
    }


}
