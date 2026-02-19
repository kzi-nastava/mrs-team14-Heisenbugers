package com.example.gotaximobile.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.gotaximobile.BuildConfig;
import com.example.gotaximobile.R;
//import com.example.gotaximobile.fragments.ride.EstimateRideFragment;
import com.example.gotaximobile.fragments.ride.EstimateRideBottomSheet;
import com.example.gotaximobile.fragments.ride.RideBookingBottomSheet;
import com.example.gotaximobile.models.MapPin;
import com.example.gotaximobile.models.dtos.AssignedRideDTO;
import com.example.gotaximobile.models.dtos.FavoriteRouteDTO;
import com.example.gotaximobile.models.dtos.GetProfileDTO;
import com.example.gotaximobile.models.dtos.LocationDTO;
import com.example.gotaximobile.models.dtos.PassengerInfoDTO;
import com.example.gotaximobile.models.dtos.PriceDTO;
import com.example.gotaximobile.models.dtos.RideDTO;
import com.example.gotaximobile.models.dtos.VehicleInfoDTO;
import com.example.gotaximobile.network.RetrofitClient;
import com.example.gotaximobile.network.RideService;
import com.example.gotaximobile.data.TokenStorage;
import com.example.gotaximobile.viewmodels.RideBookingViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;


import org.osmdroid.util.GeoPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private static final Logger log = LoggerFactory.getLogger(HomeFragment.class);
    private com.google.android.material.card.MaterialCardView cardEstimateInfo;
    private android.widget.TextView tvEstimatePrice, tvEstimateDistance, tvEstimateEta,
            tvDriverStart, tvDriverDestination, tvDriverDistance, tvDriverTime, tvNoPassengers;

    private ChipGroup cgPassengers;

    private LinearLayout containerDriverStops;

    private Button startRideButton;
    private Button cancelRideButton;

    private RideService rideService;
    private List<VehicleInfoDTO> vehicles;
    private List<MapPin> mapPins;
    private MapFragment mapFragment;
    private BottomSheetBehavior<View> driverSheetBehavior;
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

        View driverSheet = view.findViewById(R.id.driverBottomSheet);
        driverSheetBehavior = BottomSheetBehavior.from(driverSheet);
        driverSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        if ("DRIVER".equals(role)) {
            checkForActiveRide();
        }

        if (cardEstimateInfo != null) {
            cardEstimateInfo.setVisibility(View.GONE);
        }

        cardEstimateInfo = view.findViewById(R.id.cardEstimateInfo);
        tvEstimatePrice = view.findViewById(R.id.tvEstimatePrice);
        tvDriverStart = view.findViewById(R.id.tvDriverStart);
        tvDriverDestination = view.findViewById(R.id.tvDriverDestination);
        tvDriverDistance = view.findViewById(R.id.tvDriverDistance);
        tvDriverTime = view.findViewById(R.id.tvDriverTime);
        tvEstimateDistance = view.findViewById(R.id.tvEstimateDistance);
        tvEstimateEta = view.findViewById(R.id.tvEstimateEta);
        tvNoPassengers = view.findViewById(R.id.noPassengers);

        cgPassengers = view.findViewById(R.id.cgPassengers);

        containerDriverStops = view.findViewById(R.id.containerDriverStops);

        startRideButton = view.findViewById(R.id.btnStartRide);
        cancelRideButton = view.findViewById(R.id.btnCancelRide);

        if (!isGuest) {
            cardEstimateInfo.setVisibility(View.GONE);
        }
        mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);

        if (mapFragment != null) {
            mapFragment.setRouteInfoListener((durationSeconds, distanceKm) -> {
                int durationMinutes = (int) (durationSeconds / 60);

                if(Objects.equals(role, "PASSENGER")){
                    cardEstimateInfo.setVisibility(View.VISIBLE);
                }
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

        getParentFragmentManager().setFragmentResultListener("favorite_selected", this, (key, bundle) -> {
            FavoriteRouteDTO favorite = (FavoriteRouteDTO) bundle.getSerializable("selected_favorite");
            if (favorite == null) return;

            viewModel.clearAll();
            viewModel.startPoint = new GeoPoint(favorite.startAddress.latitude, favorite.startAddress.longitude);
            viewModel.endPoint = new GeoPoint(favorite.endAddress.latitude, favorite.endAddress.longitude);
            viewModel.startAddress = favorite.startAddress.address;
            viewModel.endAddress = favorite.endAddress.address;


            mapFragment.clearRouteMarkers();
            mapFragment.addRoutePin(viewModel.startPoint, viewModel.startAddress, "start");
            mapFragment.addRoutePin(viewModel.endPoint, viewModel.endAddress, "end");


            RideBookingBottomSheet bookingSheet = new RideBookingBottomSheet();
            bookingSheet.show(getChildFragmentManager(), "booking_sheet");
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

    private void checkForActiveRide() {
        RetrofitClient.rideService(getContext()).getAssignedRide().enqueue(new Callback<AssignedRideDTO>() {
            @Override
            public void onResponse(@NonNull Call<AssignedRideDTO> call, @NonNull Response<AssignedRideDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showRideOnSheet(response.body());
                } else {
                    driverSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
            @Override
            public void onFailure(@NonNull Call<AssignedRideDTO> call, @NonNull Throwable t) {  }
        });
    }

    private void showRideOnSheet(AssignedRideDTO ride) {
        driverSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        tvDriverStart.setText(ride.start.address);
        tvDriverDestination.setText(ride.end.address);

        containerDriverStops.removeAllViews();
        for (LocationDTO stop : ride.stops) {
            View stopView = getLayoutInflater().inflate(R.layout.item_ride_stop, containerDriverStops, false);
            TextView tvStopName = stopView.findViewById(R.id.tvStopName);
            tvStopName.setText(stop.getAddress());
            containerDriverStops.addView(stopView);
        }

        cgPassengers.removeAllViews();
        if (ride.passengers.isEmpty()){
            tvNoPassengers.setVisibility(View.VISIBLE);
        }
        for (PassengerInfoDTO p : ride.passengers) {
            Chip chip = new Chip(requireContext());
            chip.setText(p.getEmail());

            chip.setChipIconVisible(true);
            chip.setCheckable(false);
            chip.setClickable(true);

            Glide.with(this)
                    .load(p.getProfileImageUrl().replace("http://localhost:8081/", BuildConfig.BASE_URL))
                    .placeholder(R.drawable.ic_person)
                    .circleCrop()
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            chip.setChipIcon(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            chip.setChipIcon(placeholder);
                        }
                    });

            cgPassengers.addView(chip);
        }

        tvDriverDistance.setText(String.format(Locale.getDefault(), "%.1f km", ride.distanceKm));
        tvDriverTime.setText(ride.estimatedTimeMin + " min");

        if (mapFragment != null) {
            GeoPoint start = new GeoPoint(ride.start.getLatitude(), ride.start.getLongitude());
            mapFragment.addRoutePin(start, ride.start.address, "start");
            GeoPoint end = new GeoPoint(ride.end.getLatitude(), ride.end.getLongitude());
            mapFragment.addRoutePin(end, ride.end.address, "end");

            List<GeoPoint> stops = new ArrayList<>();
            for(LocationDTO stop : ride.stops) {
                GeoPoint stopPoint = new GeoPoint(stop.getLatitude(), stop.getLongitude());
                mapFragment.addRoutePin(stopPoint, stop.address, "stop");
            }
        }

        startRideButton.setOnClickListener(v -> startRide(ride.rideId));

        // add call for cancel ride
        //cancelRideButton.setOnClickListener(v -> cancelRide());
    }

    private void startRide(UUID id) {
        RetrofitClient.rideService(getContext()).startRide(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Ride started!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "There was an error!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {  }
        });
    }

}
