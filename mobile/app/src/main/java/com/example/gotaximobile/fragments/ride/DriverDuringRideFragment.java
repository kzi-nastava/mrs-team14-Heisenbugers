package com.example.gotaximobile.fragments.ride;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.fragments.MapFragment;
import com.example.gotaximobile.fragments.chat.ChatFragment;
import com.example.gotaximobile.models.MapPin;
import com.example.gotaximobile.models.dtos.LocationDTO;
import com.example.gotaximobile.models.dtos.MessageResponse;
import com.example.gotaximobile.models.dtos.PanicRequestDTO;
import com.example.gotaximobile.models.dtos.RideDTO;
import com.example.gotaximobile.models.dtos.StopRideRequestDTO;
import com.example.gotaximobile.models.dtos.UserStateDTO;
import com.example.gotaximobile.network.PanicApi;
import com.example.gotaximobile.network.RetrofitClient;
import com.example.gotaximobile.network.RideService;
import com.example.gotaximobile.network.UserService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverDuringRideFragment extends Fragment {

    private RideService rideService;
    private UserService userService;
    private PanicApi panicApi;

    private UUID rideId;
    private RideDTO ride;
    private MapFragment mapFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_driver_during_ride, container, false);


        if (getArguments() != null) {
            String idStr = getArguments().getString("rideId");
            if (idStr != null) {
                rideId = UUID.fromString(idStr);
            }
        }

        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.chat_container, new ChatFragment())
                    .commit();
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rideService = RetrofitClient.rideService(requireContext());
        userService = RetrofitClient.userService(requireContext());
        panicApi = RetrofitClient.panicApi(requireContext());

        mapFragment = (MapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);

        MaterialButton btnPanic = view.findViewById(R.id.btnPanic);
        btnPanic.setOnClickListener(v -> onPanicClicked(btnPanic));

        MaterialButton btnStop = view.findViewById(R.id.btnStopRide);
        btnStop.setOnClickListener(v -> onStopRideClicked(btnStop));

        fetchCurrentRide();
    }

    private void fetchCurrentRide() {
        userService.getState().enqueue(new Callback<UserStateDTO>() {
            @Override
            public void onResponse(@NonNull Call<UserStateDTO> call,
                                   @NonNull Response<UserStateDTO> resp) {
                if (!resp.isSuccessful() || resp.body() == null || resp.body().getRideId() == null) {
                    Toast.makeText(requireContext(), "No active ride", Toast.LENGTH_SHORT).show();
                    return;
                }

                rideId = resp.body().getRideId();
                Log.d("DRIVER_RIDE", "rideId=" + rideId);

                loadRide();
            }

            @Override
            public void onFailure(@NonNull Call<UserStateDTO> call,
                                  @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Failed to load ride state", Toast.LENGTH_SHORT).show();
                Log.e("DRIVER_RIDE", "state error", t);
            }
        });
    }

    private void loadRide() {
        if (rideId == null) return;

        rideService.getRide(rideId).enqueue(new Callback<RideDTO>() {
            @Override
            public void onResponse(@NonNull Call<RideDTO> call,
                                   @NonNull Response<RideDTO> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(),
                            "Failed to load ride: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                ride = response.body();
                drawRouteOnMap();
            }

            @Override
            public void onFailure(@NonNull Call<RideDTO> call,
                                  @NonNull Throwable t) {
                Log.e("DRIVER_RIDE", Objects.requireNonNull(t.getMessage()));
                Toast.makeText(requireContext(), "Network error (ride)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawRouteOnMap() {
        if (mapFragment == null || ride == null) return;

        List<GeoPoint> stops = new ArrayList<>();

        GeoPoint startPoint = new GeoPoint(
                ride.startLocation.latitude,
                ride.startLocation.longitude
        );
        mapFragment.addPin(new MapPin(startPoint.getLatitude(),
                startPoint.getLongitude(),
                R.drawable.ic_map_pin,
                "Start"));

        if (ride.route != null && !ride.route.isEmpty()) {
            for (int i = 1; i < ride.route.size() - 1; ++i) {
                LocationDTO loc = ride.route.get(i);
                GeoPoint stopPoint = new GeoPoint(loc.latitude, loc.longitude);
                stops.add(stopPoint);
                mapFragment.addPin(new MapPin(stopPoint.getLatitude(),
                        stopPoint.getLongitude(),
                        R.drawable.ic_map_pin,
                        loc.address));
            }
        }

        GeoPoint endPoint = new GeoPoint(
                ride.endLocation.latitude,
                ride.endLocation.longitude
        );
        mapFragment.addPin(new MapPin(endPoint.getLatitude(),
                endPoint.getLongitude(),
                R.drawable.ic_map_pin,
                "End"));

        mapFragment.drawRoute(startPoint, endPoint, stops);
    }

    private void onPanicClicked(MaterialButton btn) {
        if (rideId == null) {
            Toast.makeText(requireContext(), "No active ride", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder =
                new AlertDialog.Builder(new ContextThemeWrapper(requireContext(),
                        androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dialog));

        builder.setTitle("PANIC");
        builder.setMessage("Send PANIC alert to admins?");

        final EditText msgInput = new EditText(requireContext());
        msgInput.setHint("Message (optional)");
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        msgInput.setPadding(pad, pad, pad, pad);
        msgInput.setTextColor(MaterialColors.getColor(msgInput,
                com.google.android.material.R.attr.colorOnSurface));
        msgInput.setHintTextColor(MaterialColors.getColor(msgInput,
                com.google.android.material.R.attr.colorOnSurfaceVariant));

        builder.setView(msgInput);

        builder.setNegativeButton("Cancel", (d, w) -> d.dismiss());
        builder.setPositiveButton("SEND", (d, w) -> {
            btn.setEnabled(false);

            String msg = msgInput.getText() != null
                    ? msgInput.getText().toString().trim()
                    : "";
            if (msg.isEmpty()) msg = "PANIC button pressed!";

            panicApi.panic(rideId.toString(), new PanicRequestDTO(msg))
                    .enqueue(new Callback<MessageResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<MessageResponse> call,
                                               @NonNull Response<MessageResponse> response) {
                            btn.setEnabled(true);

                            if (!response.isSuccessful()) {
                                Toast.makeText(requireContext(),
                                        "Failed: " + response.code(),
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String text = (response.body() != null &&
                                    response.body().getMessage() != null)
                                    ? response.body().getMessage()
                                    : "Panic sent";

                            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(@NonNull Call<MessageResponse> call,
                                              @NonNull Throwable t) {
                            btn.setEnabled(true);
                            Toast.makeText(requireContext(),
                                    "Network error",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        builder.show();
    }


    private void onStopRideClicked(MaterialButton btn) {
        if (rideId == null) {
            Toast.makeText(requireContext(), "No active ride", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(new ContextThemeWrapper(requireContext(),
                androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dialog))
                .setTitle("Stop ride")
                .setMessage("Stop the ride at current location and recalculate price?")
                .setNegativeButton("Cancel", (d, w) -> d.dismiss())
                .setPositiveButton("STOP", (d, w) -> callStopRide(btn))
                .show();
    }

    private void callStopRide(MaterialButton btn) {
        if (rideId == null) {
            Toast.makeText(requireContext(), "No active ride", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ride == null || ride.endLocation == null) {
            Toast.makeText(requireContext(), "Ride details not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }

        btn.setEnabled(false);

        double lat = ride.endLocation.latitude;
        double lon = ride.endLocation.longitude;
        String addr = ride.endLocation.address != null
                ? ride.endLocation.address
                : "Stopped location";

        StopRideRequestDTO body = new StopRideRequestDTO(lat, lon, addr);

        rideService.stopRide(rideId, body).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call,
                                   @NonNull Response<MessageResponse> response) {
                btn.setEnabled(true);

                if (response.isSuccessful()) {
                    String text = (response.body() != null && response.body().getMessage() != null)
                            ? response.body().getMessage()
                            : "Ride stopped";

                    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show();


                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(requireContext(),
                            "Failed: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call,
                                  @NonNull Throwable t) {
                btn.setEnabled(true);
                Toast.makeText(requireContext(),
                        "Network error",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
