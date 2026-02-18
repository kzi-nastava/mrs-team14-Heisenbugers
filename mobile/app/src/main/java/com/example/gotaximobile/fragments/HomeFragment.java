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
import com.example.gotaximobile.fragments.ride.EstimateRideBottomSheet;
import com.example.gotaximobile.fragments.ride.RideBookingBottomSheet;
import com.example.gotaximobile.models.MapPin;
import com.example.gotaximobile.models.dtos.GetProfileDTO;
import com.example.gotaximobile.models.dtos.PriceDTO;
import com.example.gotaximobile.models.dtos.VehicleInfoDTO;
import com.example.gotaximobile.network.RetrofitClient;
import com.example.gotaximobile.network.RideService;
import com.example.gotaximobile.data.TokenStorage;
import com.example.gotaximobile.viewmodels.RideBookingViewModel;


import org.osmdroid.util.GeoPoint;

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
    private RideBookingViewModel viewModel;

    private List<PriceDTO> pricesList;
    private String selectedType;

    public HomeFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rideService = RetrofitClient.rideService(requireContext());
        queryVehicles();
        fetchPrices();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new androidx.lifecycle.ViewModelProvider(this).get(RideBookingViewModel.class);

        TokenStorage storage = new TokenStorage(requireContext().getApplicationContext());
        boolean isGuest = !storage.isLoggedIn();
        String role = storage.getRole();

        View fab = view.findViewById(R.id.fabEstimate);
        fab.setVisibility(isGuest || "PASSENGER".equals(role) ? View.VISIBLE : View.GONE);

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

        if (mapFragment != null) {
            mapFragment.setRouteInfoListener((durationSeconds, distanceKm) -> {
                // Convert seconds to minutes for display
                int durationMinutes = (int) (durationSeconds / 60);

                // Show the card and update values
                cardEstimateInfo.setVisibility(View.VISIBLE);
                tvEstimateDistance.setText("Distance: " + String.format("%.2f", + distanceKm) + " km");
                tvEstimateEta.setText("Time: " + durationMinutes + " min");

                viewModel.distanceKm = distanceKm;
                viewModel.durationMinutes = durationMinutes;

                if (selectedType != null){
                    tvEstimatePrice.setText("Price: " + calculatePrice(selectedType) + " RSD");
                }

            });
        }

        view.findViewById(R.id.fabEstimate).setOnClickListener(v -> {
            if ("PASSENGER".equals(role)) {
                RideBookingBottomSheet bookingSheet = new RideBookingBottomSheet();
                bookingSheet.show(getChildFragmentManager(), "booking_sheet");
            } else if (isGuest) {
                new EstimateRideBottomSheet().show(getParentFragmentManager(), "estimate_sheet");
            }

//            new com.example.gotaximobile.fragments.ride.EstimateRideBottomSheet()
//                    .show(getParentFragmentManager(), "estimate_sheet");
        });

        getChildFragmentManager().setFragmentResultListener("pin_selected", this, (key, bundle) -> {
            double lat = bundle.getDouble("lat");
            double lon = bundle.getDouble("lon");
            String name = bundle.getString("name");
            String tag = bundle.getString("tag");
            GeoPoint point = new GeoPoint(lat, lon);

            if ("start".equals(tag)) {
                viewModel.startPoint = point;
                viewModel.startAddress = name;
            } else if ("end".equals(tag)) {
                viewModel.endPoint = point;
                viewModel.endAddress = name;
            } else if ("stop".equals(tag)) {
                viewModel.addStop(point, name);
            }

            if (mapFragment != null) {
                mapFragment.addRoutePin(point, name, tag);
            }
        });

        getChildFragmentManager().setFragmentResultListener("recalculate_route", this, (key, bundle) -> {
            String name = bundle.getString("name");
            String tag = bundle.getString("tag");

            if (mapFragment != null) {
                mapFragment.removeRoutePin(name, tag);
            }
        });

        getChildFragmentManager().setFragmentResultListener("calculate_price", this, (key, bundle) -> {
            String type = bundle.getString("type");
            selectedType = type;

            tvEstimatePrice.setText("Price: " + calculatePrice(type) + " RSD");
        });

        getChildFragmentManager().setFragmentResultListener("clear_map", this, (key, bundle) -> {
            viewModel.clearAll();
            if (mapFragment != null) {
                mapFragment.clearRouteMarkers();
            }

            if (cardEstimateInfo != null) {
                cardEstimateInfo.setVisibility(View.GONE);
            }
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

    private void fetchPrices() {
        RetrofitClient.priceService(getContext()).getPrices().enqueue(new Callback<List<PriceDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<PriceDTO>> call, @NonNull Response<List<PriceDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pricesList = response.body();
                } else {
                    Log.e("API_ERROR", "Response failed: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PriceDTO>> call, @NonNull Throwable t) {
                Log.e("NETWORK_ERROR", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private double calculatePrice(String type){
        PriceDTO matchingPrice = pricesList.stream()
                .filter(priceDTO -> Objects.equals(priceDTO.getVehicleType(), type.toUpperCase()))
                .findFirst()
                .orElse(null);

        if (matchingPrice != null) {
            double startingPrice = matchingPrice.getStartingPrice();

            return Math.round((viewModel.distanceKm * 120) + startingPrice);
        }

        return 0.0;
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
