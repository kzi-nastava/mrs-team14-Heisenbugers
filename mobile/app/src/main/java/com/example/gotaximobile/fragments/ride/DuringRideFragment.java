package com.example.gotaximobile.fragments.ride;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.fragments.MapFragment;
import com.example.gotaximobile.models.MapPin;
import com.example.gotaximobile.models.dtos.LocationDTO;
import com.example.gotaximobile.models.dtos.RideDTO;
import com.example.gotaximobile.models.dtos.RideTrackingDTO;
import com.example.gotaximobile.network.RetrofitClient;
import com.example.gotaximobile.network.RideService;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.textfield.TextInputEditText;

import com.example.gotaximobile.models.dtos.MessageResponse;
import com.example.gotaximobile.models.dtos.PanicRequestDTO;
import com.example.gotaximobile.network.PanicApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DuringRideFragment extends Fragment {

    private RideDTO ride;
    private RideTrackingDTO rideTracking;
    private RideService rideService;
    private UUID rideId;
    private MapFragment mapFragment;
    private PanicApi panicApi;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_during_ride, container, false);

        if (getArguments() != null) {
            String idString = getArguments().getString("rideId");
            if (idString != null) {
                rideId = UUID.fromString(idString);
            }
        }
        assert rideId != null;

        rideService = RetrofitClient.rideService(requireContext());
        panicApi = RetrofitClient.panicApi(requireContext());
        Log.d("PANIC", "rideId=" + rideId);

        rideService.getRide(rideId).enqueue(new Callback<RideDTO>() {
            @Override
            public void onResponse(@NonNull Call<RideDTO> call,
                                   @NonNull Response<RideDTO> response) {
                ride = response.body();
                populateRideData();
            }

            @Override
            public void onFailure(@NonNull Call<RideDTO> call, @NonNull Throwable t) {
                Log.e("NETWORK_ERROR", Objects.requireNonNull(t.getMessage()));

            }
        });

        updateTracking();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);

        assert mapFragment != null;
        mapFragment.setRouteInfoListener((durationSec, distanceKm) -> {
            long minutes = Math.round(durationSec / 60.0);

            TextView etaView = requireView().findViewById(R.id.eta);
            TextView distanceView = requireView().findViewById(R.id.eta_distance);

            etaView.setText(minutes + " min");
            distanceView.setText(String.format("%.1f km", distanceKm));
        });

        // Note input and icon
        TextView noteLabel = view.findViewById(R.id.noteLabel);
        ImageButton noteButton = view.findViewById(R.id.noteButton);

        noteButton.setOnClickListener(v -> openModal());
        noteLabel.setOnClickListener(v -> openModal());


        //panic
        FloatingActionButton btnPanic = view.findViewById(R.id.btnPanic);

        btnPanic.setOnClickListener(v -> {
            if (rideId == null) {
                Toast.makeText(requireContext(), "Missing rideId", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(new ContextThemeWrapper(requireContext(),
                            androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dialog));
            builder.setTitle("PANIC");
            builder.setMessage("Send PANIC alert to admins?");


            final EditText msgInput = new EditText(requireContext());
            msgInput.setHint("Message");
            int pad = (int) (16 * getResources().getDisplayMetrics().density);
            msgInput.setPadding(pad, pad, pad, pad);
            builder.setView(msgInput);

            builder.setNegativeButton("Cancel", (d, w) -> d.dismiss());
            builder.setPositiveButton("SEND", (d, w) -> {
                v.setEnabled(false);

                String msg = msgInput.getText() != null ? msgInput.getText().toString().trim() : "";
                if (msg.isEmpty()) msg = "PANIC button pressed!";

                panicApi.panic(rideId.toString(), new PanicRequestDTO(msg))
                        .enqueue(new Callback<MessageResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<MessageResponse> call,
                                                   @NonNull Response<MessageResponse> response) {
                                v.setEnabled(true);

                                if (!response.isSuccessful()) {
                                    Toast.makeText(requireContext(), "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String text = (response.body() != null && response.body().getMessage() != null)
                                        ? response.body().getMessage()
                                        : "Panic sent";
                                Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                                v.setEnabled(true);
                                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                            }
                        });
            });

            builder.show();
        });


    }

    private void updateTracking() {
        rideService.getRideTracking(rideId).enqueue(new Callback<RideTrackingDTO>() {
            @Override
            public void onResponse(@NonNull Call<RideTrackingDTO> call,
                                   @NonNull Response<RideTrackingDTO> response) {
                rideTracking = response.body();
                assert rideTracking != null;
                MapPin carPin = new MapPin(
                        rideTracking.vehicleLatitude,
                        rideTracking.vehicleLongitude,
                        R.drawable.ic_car_map,
                        "You are here"
                );
                carPin.snapToRoad = true;
                mapFragment.addPin(carPin);
            }

            @Override
            public void onFailure(@NonNull Call<RideTrackingDTO> call, @NonNull Throwable t) {
                Log.e("NETWORK_ERROR", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void openModal() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(new ContextThemeWrapper(requireContext(),
                        androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dialog));
        builder.setTitle("Add a note");

        // Create a vertical LinearLayout to hold title and description fields
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        // Title input
        final EditText titleInput = new EditText(requireContext());
        titleInput.setHint("Title");
        titleInput.setTextColor(MaterialColors.getColor(titleInput,
                com.google.android.material.R.attr.colorOnSurface));
        titleInput.setHintTextColor(
                MaterialColors.getColor(titleInput,
                        com.google.android.material.R.attr.colorOnSurfaceVariant));
        titleInput.setTextSize(16);
        layout.addView(titleInput);

        // Description input
        final EditText descriptionInput = new EditText(requireContext());
        descriptionInput.setHint("Description");
        descriptionInput.setTextSize(14);
        descriptionInput.setGravity(android.view.Gravity.TOP);

        descriptionInput.setTextColor(
                MaterialColors.getColor(descriptionInput, com.google.android.material.R.attr.colorOnSurface));
        descriptionInput.setHintTextColor(
                MaterialColors.getColor(descriptionInput, com.google.android.material.R.attr.colorOnSurfaceVariant));
        layout.addView(descriptionInput);

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String title = titleInput.getText().toString();
            String description = descriptionInput.getText().toString();
            sendNotes(title, description);
        });

        builder.setNegativeButton("Cancel",
                (dialog, which) -> dialog.dismiss());

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendNotes(String title, String description) {
        if (title == null || title.isEmpty()) {
            Toast.makeText(requireContext(), "Title can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        body.put("desc", description);
        rideService.reportRide(rideId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Toast.makeText(requireContext(), "Report sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Failed to send report", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void populateRideData() {
        TextInputEditText startField = requireView().findViewById(R.id.start_field);
        TextInputEditText finishField = requireView().findViewById(R.id.finish_field);

        startField.setText(ride.startLocation.address);
        finishField.setText(ride.endLocation.address);

        if (mapFragment != null) {
            List<GeoPoint> stops = new ArrayList<>();

            // Start pin
            GeoPoint startPoint = new GeoPoint(ride.startLocation.latitude, ride.startLocation.longitude);
            mapFragment.addPin(new MapPin(startPoint.getLatitude(), startPoint.getLongitude(), R.drawable.ic_map_pin, "Start"));

            // Stops along the route

            if (ride.route != null && !ride.route.isEmpty()) {
                for (int i = 1; i < ride.route.size() - 1; ++i) {
                    LocationDTO loc = ride.route.get(i);
                    GeoPoint stopPoint = new GeoPoint(loc.latitude, loc.longitude);
                    stops.add(stopPoint);
                    mapFragment.addPin(new MapPin(stopPoint.getLatitude(), stopPoint.getLongitude(), R.drawable.ic_map_pin, loc.address));
                }
            }


            // End pin
            GeoPoint endPoint = new GeoPoint(ride.endLocation.latitude, ride.endLocation.longitude);
            mapFragment.addPin(new MapPin(endPoint.getLatitude(), endPoint.getLongitude(), R.drawable.ic_map_pin, "End"));

            mapFragment.drawRoute(startPoint, endPoint, stops);
        }
    }


}
