package com.example.gotaximobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.fragments.ride.RateRideDialogFragment;
import com.example.gotaximobile.models.dtos.LocationDTO;
import com.example.gotaximobile.models.dtos.RideDetailsDTO;
import com.example.gotaximobile.models.dtos.RideHistoryDTO;
import com.example.gotaximobile.network.RetrofitClient;
import com.example.gotaximobile.network.RideService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import org.osmdroid.util.GeoPoint;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRideHistoryDetailFragment extends Fragment {

    private RideService rideService;
    private ProgressBar progress;

    private MapFragment mapFragment;

    public static PassengerRideHistoryDetailFragment newInstance(RideHistoryDTO r) {
        Bundle b = new Bundle();
        b.putString("rideId", r.getRideId() != null ? r.getRideId().toString() : null);

        b.putString("start", r.getStartAddress());
        b.putString("end", r.getEndAddress());
        b.putString("startedAt", r.getStartedAt() != null ? r.getStartedAt().toString() : null);
        b.putString("endedAt", r.getEndedAt() != null ? r.getEndedAt().toString() : null);
        b.putDouble("price", r.getPrice());
        b.putBoolean("canceled", r.isCanceled());
        b.putBoolean("panic", r.isPanicTriggered());
        b.putBoolean("favorite", r.isFavorite());
        b.putBoolean("rated", r.isRated());

        PassengerRideHistoryDetailFragment f = new PassengerRideHistoryDetailFragment();
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_history_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rideService = RetrofitClient.rideService(requireContext());

        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                getParentFragmentManager().popBackStack()
        );

        progress = view.findViewById(R.id.progress);

        // --- attach MapFragment only once ---
        if (savedInstanceState == null) {
            mapFragment = new MapFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.map_container, mapFragment)
                    .commit();
        } else {
            Fragment f = getChildFragmentManager().findFragmentById(R.id.map_container);
            if (f instanceof MapFragment) mapFragment = (MapFragment) f;
        }

        // --- read arguments for UI (quick bind without network) ---
        Bundle a = getArguments();

        String rideId = a != null ? a.getString("rideId") : null;

        String start = a != null ? a.getString("start") : "";
        String end = a != null ? a.getString("end") : "";
        String startedAt = a != null ? a.getString("startedAt") : null;
        String endedAt = a != null ? a.getString("endedAt") : null;

        double price = a != null ? a.getDouble("price", 0) : 0;
        boolean canceled = a != null && a.getBoolean("canceled", false);
        boolean panic = a != null && a.getBoolean("panic", false);
        boolean favorite = a != null && a.getBoolean("favorite", false);
        boolean rated = a != null && a.getBoolean("rated", false);

        TextView tvRoute = view.findViewById(R.id.tvRoute);
        TextView tvTimes = view.findViewById(R.id.tvTimes);
        TextView tvPrice = view.findViewById(R.id.tvPrice);

        TextView tvDriver = view.findViewById(R.id.tvDriver);
        TextView tvPassengers = view.findViewById(R.id.tvPassengers);
        TextView tvViolations = view.findViewById(R.id.tvViolations);
        TextView tvRating = view.findViewById(R.id.tvRating);

        Chip chipStatus = view.findViewById(R.id.chipStatus);
        Chip chipCanceled = view.findViewById(R.id.chipCanceled);
        Chip chipPanic = view.findViewById(R.id.chipPanic);

        // --- bind basic info ---
        tvRoute.setText(safe(start) + " → " + safe(end));

        String s1 = fmt(startedAt);
        String s2 = (endedAt == null || endedAt.trim().isEmpty()) ? "Ongoing" : fmt(endedAt);
        tvTimes.setText(s1 + "  -  " + s2);

        tvPrice.setText(String.format("%.2f RSD", price));

        if (canceled) chipStatus.setText("CANCELED");
        else if (endedAt == null || endedAt.trim().isEmpty()) chipStatus.setText("ONGOING");
        else chipStatus.setText("FINISHED");

        chipCanceled.setVisibility(canceled ? View.VISIBLE : View.GONE);
        chipPanic.setVisibility(panic ? View.VISIBLE : View.GONE);

        // placeholders
        tvDriver.setText("Driver: —");
        tvPassengers.setText("—");
        tvViolations.setText("—");
        tvRating.setText((rated ? "Rated ✓" : "Not rated") + (favorite ? " | Favorite ★" : ""));

        // --- load ride details for map (and optionally driver) ---
        if (rideId != null && !rideId.trim().isEmpty()) {
            loadRideDetailsAndDraw(rideId, tvDriver);
        } else {
            // без rideId карту не построить
            // (не обязательно показывать Toast, но полезно в дебаге)
            // Toast.makeText(requireContext(), "Missing rideId for map", Toast.LENGTH_SHORT).show();
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        String startedAtF = LocalDateTime.parse(startedAt, DateTimeFormatter.ISO_DATE_TIME).format(fmt);
        String endedAtF = LocalDateTime.parse(endedAt, DateTimeFormatter.ISO_DATE_TIME).format(fmt);

        MaterialButton rateButton = view.findViewById(R.id.rate_ride_button);
        rateButton.setOnClickListener(v -> {
            RateRideDialogFragment dialog = RateRideDialogFragment.newInstance(
                    start,
                    end,
                    startedAtF + " - " + endedAtF,
                    String.format("%.2f", price),
                    rideId
            );
            dialog.show(getParentFragmentManager(), "rateRideDialog");
        });
    }

    private void loadRideDetailsAndDraw(String rideId, TextView tvDriver) {
        progress.setVisibility(View.VISIBLE);

        rideService.getRideDetails(rideId).enqueue(new Callback<RideDetailsDTO>() {
            @Override
            public void onResponse(@NonNull Call<RideDetailsDTO> call,
                                   @NonNull Response<RideDetailsDTO> response) {
                progress.setVisibility(View.GONE);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Ride details failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                RideDetailsDTO d = response.body();

                // driver name (optional)
                if (d.getDriver() != null) {
                    String name = (safe(d.getDriver().getFirstName()) + " " + safe(d.getDriver().getLastName())).trim();
                    tvDriver.setText("Driver: " + (name.isEmpty() ? "—" : name));
                }

                drawMap(d);
            }

            @Override
            public void onFailure(@NonNull Call<RideDetailsDTO> call, @NonNull Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Network error (ride details)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawMap(RideDetailsDTO d) {
        if (mapFragment == null || d == null) return;

        // 1) draw polyline/route if exists
        List<LocationDTO> route = d.getRoute();
        List<GeoPoint> pts = toGeoPoints(route);
        if (pts.size() >= 2) {
            mapFragment.drawRouteFromPoints(pts);
            return;
        }

        // 2) fallback: start/end
        LocationDTO s = d.getStartLocation();
        LocationDTO e = d.getEndLocation();
        if (s != null && e != null) {
            mapFragment.drawRoute(
                    new GeoPoint(s.getLatitude(), s.getLongitude()),
                    new GeoPoint(e.getLatitude(), e.getLongitude()),
                    null
            );
        }
    }

    private List<GeoPoint> toGeoPoints(List<LocationDTO> locs) {
        List<GeoPoint> out = new ArrayList<>();
        if (locs == null) return out;
        for (LocationDTO l : locs) {
            if (l == null) continue;
            out.add(new GeoPoint(l.getLatitude(), l.getLongitude()));
        }
        return out;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String fmt(String iso) {
        if (iso == null) return "—";
        String x = iso.replace('T', ' ');
        return x.length() >= 16 ? x.substring(0, 16) : x;
    }
}
