
package com.example.gotaximobile.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.fragments.MapFragment;
import com.example.gotaximobile.models.MapPin;
import com.example.gotaximobile.models.dtos.MessageResponse;
import com.example.gotaximobile.models.dtos.PanicEventDTO;
import com.example.gotaximobile.network.AdminPanicApi;
import com.example.gotaximobile.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPanicDetailFragment extends Fragment {

    private String panicId;
    private String route;
    private String message;
    private String createdAt;
    private Double lat;
    private Double lng;

    public static AdminPanicDetailFragment newInstance(PanicEventDTO e) {
        AdminPanicDetailFragment f = new AdminPanicDetailFragment();
        Bundle b = new Bundle();
        if (e.getId() != null) b.putString("panicId", e.getId().toString());
        b.putString("start", e.getStartAddress());
        b.putString("end", e.getEndAddress());
        b.putString("message", e.getMessage());
        b.putString("createdAt", e.getCreatedAt());
        if (e.getVehicleLat() != null) b.putDouble("lat", e.getVehicleLat());
        if (e.getVehicleLng() != null) b.putDouble("lng", e.getVehicleLng());
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_panic_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle a = getArguments();
        if (a != null) {
            panicId = a.getString("panicId");
            String start = a.getString("start");
            String end = a.getString("end");
            route = (start != null ? start : "Unknown start") +
                    " → " +
                    (end != null ? end : "Unknown destination");
            message = a.getString("message");
            createdAt = a.getString("createdAt");
            if (a.containsKey("lat")) lat = a.getDouble("lat");
            if (a.containsKey("lng")) lng = a.getDouble("lng");
        }

        TextView tvRoute = view.findViewById(R.id.tvRoute);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        TextView tvCreatedAt = view.findViewById(R.id.tvCreatedAt);

        tvRoute.setText(route != null ? route : "");
        tvMessage.setText(message != null ? message : "");
        tvCreatedAt.setText(createdAt != null ? createdAt : "");

        // back
        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                getParentFragmentManager().popBackStack()
        );


        ExtendedFloatingActionButton btnResolve = view.findViewById(R.id.btnResolvePanic);
        btnResolve.setOnClickListener(v -> {
            if (panicId == null) {
                Toast.makeText(requireContext(), "Missing panic id", Toast.LENGTH_SHORT).show();
                return;
            }
            AdminPanicApi api = RetrofitClient.adminPanicApi(requireContext());
            api.resolve(panicId).enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(@NonNull Call<MessageResponse> call,
                                       @NonNull Response<MessageResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Panic resolved", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(requireContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MessageResponse> call,
                                      @NonNull Throwable t) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });


        MapFragment mapFragment =
                (MapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null && lat != null && lng != null) {
            MapPin pin = new MapPin(lat, lng, R.drawable.ic_car_map, "Vehicle");
            mapFragment.addPin(pin);
        }
    }
}
