package com.example.gotaximobile.fragments.admin;

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
import com.example.gotaximobile.fragments.MapFragment;
import com.example.gotaximobile.models.dtos.AdminRideDetailsDTO;
import com.example.gotaximobile.models.dtos.LocationDTO;
import com.example.gotaximobile.models.dtos.PassengerInfoDTO;
import com.example.gotaximobile.models.dtos.TrafficViolationDTO;
import com.example.gotaximobile.network.AdminApi;
import com.example.gotaximobile.network.RetrofitClient;
import com.google.android.material.chip.Chip;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRideHistoryDetailFragment extends Fragment {

    private static final String ARG_RIDE_ID = "rideId";

    public static AdminRideHistoryDetailFragment newInstance(String rideId) {
        Bundle b = new Bundle();
        b.putString(ARG_RIDE_ID, rideId);
        AdminRideHistoryDetailFragment f = new AdminRideHistoryDetailFragment();
        f.setArguments(b);
        return f;
    }

    private AdminApi adminApi;
    private ProgressBar progress;

    private TextView tvRoute, tvPrice, tvDriver, tvTimes, tvPassengers, tvViolations, tvRating;
    private Chip chipStatus, chipCanceled, chipPanic;

    private MapFragment mapFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_ride_history_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adminApi = RetrofitClient.adminApi(requireContext());

        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                getParentFragmentManager().popBackStack()
        );

        progress = view.findViewById(R.id.progress);

        tvRoute = view.findViewById(R.id.tvRoute);
        chipStatus = view.findViewById(R.id.chipStatus);
        chipCanceled = view.findViewById(R.id.chipCanceled);
        chipPanic = view.findViewById(R.id.chipPanic);

        tvPrice = view.findViewById(R.id.tvPrice);
        tvDriver = view.findViewById(R.id.tvDriver);
        tvTimes = view.findViewById(R.id.tvTimes);
        tvPassengers = view.findViewById(R.id.tvPassengers);
        tvViolations = view.findViewById(R.id.tvViolations);
        tvRating = view.findViewById(R.id.tvRating);

        // attach osmdroid MapFragment into container (only once)
        if (savedInstanceState == null) {
            mapFragment = new MapFragment();
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.map_container, mapFragment)
                    .commit();
        } else {
            Fragment f = getChildFragmentManager().findFragmentById(R.id.map_container);
            if (f instanceof MapFragment) mapFragment = (MapFragment) f;
        }

        String rideId = getArguments() != null ? getArguments().getString(ARG_RIDE_ID) : null;
        if (rideId == null || rideId.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Missing rideId", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
            return;
        }

        loadDetails(rideId);
    }

    private void loadDetails(String rideId) {
        progress.setVisibility(View.VISIBLE);

        adminApi.getRideDetails(rideId).enqueue(new Callback<AdminRideDetailsDTO>() {
            @Override
            public void onResponse(@NonNull Call<AdminRideDetailsDTO> call,
                                   @NonNull Response<AdminRideDetailsDTO> response) {
                progress.setVisibility(View.GONE);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                bind(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<AdminRideDetailsDTO> call, @NonNull Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bind(AdminRideDetailsDTO d) {
        tvRoute.setText(safeAddr(d.getStart()) + " → " + safeAddr(d.getDestination()));

        chipStatus.setText(d.getStatus() != null ? d.getStatus().toString() : "—");

        chipCanceled.setVisibility(d.isCanceled() ? View.VISIBLE : View.GONE);
        chipPanic.setVisibility(d.isPanicTriggered() ? View.VISIBLE : View.GONE);

        tvPrice.setText(d.getPrice() != null ? (d.getPrice().toString() + " RSD") : "—");

        tvDriver.setText("Driver: " + (d.getDriverName() != null ? d.getDriverName() : "—"));

        tvTimes.setText(fmt(d.getStartedAt()) + "  →  " + (d.getEndedAt() != null ? fmt(d.getEndedAt()) : "Ongoing"));

        tvPassengers.setText(formatPassengers(d.getPassengers()));
        tvViolations.setText(formatViolations(d.getTrafficViolations()));

        if (d.getRating() != null) {
            tvRating.setText("Driver: " + d.getRating().getDriverScore() +
                    " | Vehicle: " + d.getRating().getVehicleScore() +
                    (d.getRating().getComment() != null ? ("\n" + d.getRating().getComment()) : ""));
        } else {
            tvRating.setText("—");
        }

        // ---- MAP ROUTE ----
        drawMapRoute(d);
    }

    private void drawMapRoute(AdminRideDetailsDTO d) {
        if (mapFragment == null) return;

        List<LocationDTO> pl = d.getPolyline();
        List<GeoPoint> points = toGeoPoints(pl);

        if (points.size() >= 2) {
            mapFragment.drawRouteFromPoints(points);
            return;
        }

        GeoPoint start = toGeoPoint(d.getStart());
        GeoPoint end = toGeoPoint(d.getDestination());
        List<GeoPoint> stops = toGeoPoints(d.getStops());

        if (start != null && end != null) {
            mapFragment.drawRoute(start, end, stops);
        }
    }

    private List<GeoPoint> toGeoPoints(List<LocationDTO> locs) {
        List<GeoPoint> out = new ArrayList<>();
        if (locs == null) return out;
        for (LocationDTO l : locs) {
            GeoPoint gp = toGeoPoint(l);
            if (gp != null) out.add(gp);
        }
        return out;
    }

   private GeoPoint toGeoPoint(LocationDTO l) {
        if (l == null) return null;
try {
            double lat = l.getLatitude();
            double lng = l.getLongitude();
            return new GeoPoint(lat, lng);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String safeAddr(LocationDTO l) {
        return (l == null || l.getAddress() == null) ? "" : l.getAddress();
    }

    private String fmt(java.time.LocalDateTime dt) {
        if (dt == null) return "—";
        String s = dt.toString().replace('T', ' ');
        return s.length() >= 16 ? s.substring(0, 16) : s;
    }

    private String formatPassengers(List<PassengerInfoDTO> ps) {
        if (ps == null || ps.isEmpty()) return "—";
        StringBuilder sb = new StringBuilder();
        for (PassengerInfoDTO p : ps) {
            if (p == null) continue;
            String name = ((p.getFirstName() != null ? p.getFirstName() : "") + " " +
                    (p.getLastName() != null ? p.getLastName() : "")).trim();
            if (name.isEmpty()) name = p.getEmail() != null ? p.getEmail() : "Passenger";
            sb.append("• ").append(name).append("\n");
        }
        return sb.toString().trim();
    }

    private String formatViolations(List<TrafficViolationDTO> vs) {
        if (vs == null || vs.isEmpty()) return "—";
        StringBuilder sb = new StringBuilder();
        for (TrafficViolationDTO v : vs) {
            if (v == null) continue;
            sb.append("• ").append(v.getTitle() != null ? v.getTitle() : "Violation");
            if (v.getDescription() != null && !v.getDescription().isEmpty()) {
                sb.append(": ").append(v.getDescription());
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }
}
