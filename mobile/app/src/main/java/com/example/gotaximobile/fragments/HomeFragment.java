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
//import com.example.gotaximobile.fragments.ride.EstimateRideFragment;
import com.example.gotaximobile.models.MapPin;
import com.example.gotaximobile.models.dtos.VehicleInfoDTO;
import com.example.gotaximobile.network.RetrofitClient;
import com.example.gotaximobile.network.RideService;
import com.example.gotaximobile.data.TokenStorage;



import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private com.google.android.material.card.MaterialCardView cardEstimateInfo;
    private android.widget.TextView tvEstimatePrice, tvEstimateDistance, tvEstimateEta;

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

/*
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);


    }*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TokenStorage storage = new TokenStorage(requireContext().getApplicationContext());
        boolean isGuest = !storage.isLoggedIn();

        View fab = view.findViewById(R.id.fabEstimate);
        fab.setVisibility(isGuest ? View.VISIBLE : View.GONE);

        if (cardEstimateInfo != null) {
            cardEstimateInfo.setVisibility(View.GONE);
        }

        cardEstimateInfo = view.findViewById(R.id.cardEstimateInfo);
        tvEstimatePrice = view.findViewById(R.id.tvEstimatePrice);
        tvEstimateDistance = view.findViewById(R.id.tvEstimateDistance);
        tvEstimateEta = view.findViewById(R.id.tvEstimateEta);

        if (!isGuest) {
            cardEstimateInfo.setVisibility(View.GONE);
        }
        mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);

        view.findViewById(R.id.fabEstimate).setOnClickListener(v -> {
            if (!isGuest) return;
            if (cardEstimateInfo != null) {
                cardEstimateInfo.setVisibility(View.GONE);
            }

            new com.example.gotaximobile.fragments.ride.EstimateRideBottomSheet()
                    .show(getParentFragmentManager(), "estimate_sheet");
        });

        getParentFragmentManager().setFragmentResultListener("estimate_result", this, (key, bundle) -> {
            if (!isGuest) return;

            String price = bundle.getString("price");
            int timeMin = bundle.getInt("timeMin");
            double distKm = bundle.getDouble("distKm");
            ArrayList<double[]> points = (ArrayList<double[]>) bundle.getSerializable("routePoints");


            if (points != null && mapFragment != null) {
                List<org.osmdroid.util.GeoPoint> geo = new java.util.ArrayList<>();
                for (double[] p : points) geo.add(new org.osmdroid.util.GeoPoint(p[0], p[1]));
                mapFragment.drawRouteFromPoints(geo);
            }

            cardEstimateInfo.setVisibility(View.VISIBLE);
            tvEstimatePrice.setText("Price: " + price + " RSD");
            tvEstimateDistance.setText("Distance: " + String.format("%.2f", distKm) + " km");
            tvEstimateEta.setText("Time: " + timeMin + " min");


            /*android.widget.Toast.makeText(requireContext(),
                    "ETA: " + timeMin + " min | " + price + " | " + String.format("%.2f", distKm) + " km",
                    android.widget.Toast.LENGTH_LONG).show();*/
        });
    }



    private void queryVehicles() {
        rideService.getAllVehicles().enqueue(new Callback<List<VehicleInfoDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<VehicleInfoDTO>> call,
                                   @NonNull Response<List<VehicleInfoDTO>> response) {
                vehicles = response.body();
                assert vehicles != null;
                //mapPins = vehicles.stream().map(VehicleInfoDTO::toMapPin).toList();
                mapPins = new ArrayList<>();
                for (VehicleInfoDTO v : vehicles) {
                    mapPins.add(v.toMapPin());
                }
                if (mapFragment == null) return;
                updateVehiclePins();
            }

            @Override
            public void onFailure(@NonNull Call<List<VehicleInfoDTO>> call, @NonNull Throwable t) {
                Log.e("NETWORK_ERROR", Objects.requireNonNull(t.getMessage()));
            }
        });
    }


    private void updateVehiclePins() {
        if (mapFragment == null || mapPins == null) return;
        mapFragment.setPins(mapPins);
    }



}
