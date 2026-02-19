package com.example.gotaximobile.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.RideHistoryDTO;
import com.example.gotaximobile.network.RetrofitClient;
import com.example.gotaximobile.network.UserService;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PassengerRideHistoryFragment extends Fragment implements SensorEventListener {

    private UserService userService;
    private ProgressBar progress;
    private PassengerRideHistoryAdapter adapter;

    private TextInputEditText etFrom, etTo;

    private String sortBy = "DATE";
    private String direction = "desc";


    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastShake = 0;
    private String sortField = "startedAt";
    private String sortDir = "desc";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userService = RetrofitClient.userService(requireContext());

        progress = view.findViewById(R.id.progress);
        etFrom = view.findViewById(R.id.etFrom);
        etTo = view.findViewById(R.id.etTo);

        RecyclerView rv = view.findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PassengerRideHistoryAdapter(new PassengerRideHistoryAdapter.OnRideClick(){
            @Override
            public void onClick(RideHistoryDTO item) {
                // Передадим данные в детали без сети (самый надёжный вариант)
                Bundle b = new Bundle();
                b.putString("start", item.getStartAddress());
                b.putString("end", item.getEndAddress());
                b.putString("startedAt", item.getStartedAt() != null ? item.getStartedAt().toString() : null);
                b.putString("endedAt", item.getEndedAt() != null ? item.getEndedAt().toString() : null);
                b.putDouble("price", item.getPrice());
                b.putBoolean("canceled", item.isCanceled());
                b.putBoolean("panic", item.isPanicTriggered());
                b.putString("rideId", item.getRideId() != null ? item.getRideId().toString() : null);

                Fragment details = new PassengerRideHistoryDetailFragment();
                details.setArguments(b);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, details)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onFavoriteClick(RideHistoryDTO item, int position) {
                toggleFavorite(item, position);
            }

        });
        rv.setAdapter(adapter);
        rv.setAdapter(adapter);

        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                getParentFragmentManager().popBackStack()
        );

        view.findViewById(R.id.btnSortDate).setOnClickListener(v -> {
            sortBy = "DATE";
            toggleDirection();
            load();
        });

        view.findViewById(R.id.btnSortPrice).setOnClickListener(v -> {
            sortBy = "PRICE";
            toggleDirection();
            load();
        });

        view.findViewById(R.id.btnSortStatus).setOnClickListener(v -> {
            sortBy = "STATUS";
            toggleDirection();
            load();
        });


        view.findViewById(R.id.btnApply).setOnClickListener(v -> load());
        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                getParentFragmentManager().popBackStack()
        );
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        load();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void load() {
        progress.setVisibility(View.VISIBLE);

        String from = text(etFrom); // "YYYY-MM-DD"
        String to = text(etTo);

        userService.getHistory(
                from.isEmpty() ? null : from,
                to.isEmpty() ? null : to,
                sortBy,
                direction
        ).enqueue(new Callback<List<RideHistoryDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<RideHistoryDTO>> call,
                                   @NonNull Response<List<RideHistoryDTO>> response) {
                progress.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                adapter.submit(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<RideHistoryDTO>> call, @NonNull Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String text(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > 2.7f) { // threshold
            long now = System.currentTimeMillis();
            if (now - lastShake < 800) return; // debounce
            lastShake = now;

            direction = "desc".equals(direction) ? "asc" : "desc";
            load();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    private void toggleDirection() {
        direction = "asc".equals(direction) ? "desc" : "asc";
    }

    private void toggleFavorite(RideHistoryDTO item, int position) {
        // 1. Determine new state
        boolean newState = !item.isFavorite();

        if(newState) {
            RetrofitClient.rideService(getContext()).addFavorite(item.getRideId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        item.setFavorite(newState);
                        adapter.notifyItemChanged(position);

                        String msg = "Added to favorites";
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update favorite", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            RetrofitClient.rideService(getContext()).deleteFavoriteFromRide(item.getRideId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        item.setFavorite(newState);
                        adapter.notifyItemChanged(position);

                        String msg = "Removed from favorites";
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update favorite", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
