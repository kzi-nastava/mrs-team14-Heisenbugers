package com.example.gotaximobile.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.Ride;
import com.example.gotaximobile.models.dtos.DriverRideHistoryDTO;
import com.example.gotaximobile.models.enums.SortDirection;
import com.example.gotaximobile.network.ProfileService;
import com.example.gotaximobile.network.RetrofitClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 */
public class RideFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private Button[] buttons;
    private int activatedButtonIndex = 0;
    private SortDirection sortDirection = SortDirection.ASCENDING;
    private ProfileService profileApi;
    private List<Ride> rides = new ArrayList<>();

    private RecyclerView recyclerView;
    private MyRideRecyclerViewAdapter adapter;

    // For shake-sort
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener shakeListener;
    private static final float SHAKE_THRESHOLD = 2.5f;
    private long lastShakeTime = 0;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RideFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(shakeListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(shakeListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        shakeListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float gX = x / SensorManager.GRAVITY_EARTH;
                float gY = y / SensorManager.GRAVITY_EARTH;
                float gZ = z / SensorManager.GRAVITY_EARTH;

                float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

                if (gForce > SHAKE_THRESHOLD) {
                    long now = System.currentTimeMillis();
                    if (lastShakeTime + 1000 < now) {
                        lastShakeTime = now;

                        // DO SOMETHING
                        Toast.makeText(requireContext(), "Shaken!", Toast.LENGTH_SHORT).show();
                        nextSortOption();

                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }

    private void nextSortOption() {
        if (buttons == null || buttons.length == 0) return;

        // toggle direction

        // re-trigger current active sort
        getButtonHandlers().get(activatedButtonIndex).accept(buttons[activatedButtonIndex]);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        View targetView = view.findViewById(R.id.list);

        profileApi = RetrofitClient.profileService(requireContext());
        queryRides(null, null, null, null);

        Button btnSortDate = view.findViewById(R.id.btnSortDate);
        Button btnSortPrice = view.findViewById(R.id.btnSortPrice);
        Button btnSortPlace = view.findViewById(R.id.btnSortPlace);

        btnSortDate.setSelected(true);

        buttons = new Button[]{btnSortDate, btnSortPrice, btnSortPlace};
        List<Consumer<View>> handlers = getButtonHandlers();
        buttons[0].setOnClickListener(v -> {
            handlers.get(0).accept(v);
            selectButton(buttons[0]);
        });
        buttons[1].setOnClickListener(v -> {
            handlers.get(1).accept(v);
            selectButton(buttons[1]);
        });
        buttons[2].setOnClickListener(v -> {
            handlers.get(2).accept(v);
            selectButton(buttons[2]);
        });

        // Set the adapter
        if (targetView instanceof RecyclerView) {
            recyclerView = initRecycleContainer(targetView);
            DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(divider);
        }
        return view;
    }

    @NonNull
    private RecyclerView initRecycleContainer(View targetView) {
        Context context = targetView.getContext();
        RecyclerView recyclerView = (RecyclerView) targetView;
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        adapter = (new MyRideRecyclerViewAdapter(rides, ride -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("ride", ride);
            DriverHistoryOneRideFragment fragment = new DriverHistoryOneRideFragment();
            fragment.setArguments(bundle);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }));
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }


    private void queryRides(String startDate, String endDate, String sortBy, SortDirection direction) {
        getHistory(startDate, endDate, sortBy, direction).enqueue(new Callback<List<DriverRideHistoryDTO>>() {
            public void onResponse(@NonNull Call<List<DriverRideHistoryDTO>> call,
                                   @NonNull Response<List<DriverRideHistoryDTO>> response) {
                assert response.body() != null;
                rides.clear();
                List<Ride> newHistory = response.body().stream().map(Ride::new)
                        .collect(Collectors.toList());
                rides.addAll(newHistory);
                adapter.notifyDataSetChanged(); // refresh list
            }

            @Override
            public void onFailure(@NonNull Call<List<DriverRideHistoryDTO>> call,
                                  @NonNull Throwable t) {
                Log.e("NETWORK_ERROR", Objects.requireNonNull(t.getMessage()));
            }
        });
    }


    private void selectButton(Button selected) {
        for (Button b : buttons) {
            b.setSelected(b == selected);
        }
    }

    private List<Consumer<View>> getButtonHandlers() {
        return Arrays.asList(
                view -> {
                    if (buttons[0].isSelected()) {
                        sortDirection = toggleSort(sortDirection);
                    } else {
                        sortDirection = SortDirection.ASCENDING;
                    }
                    activatedButtonIndex = 0;
                    queryRides(null, null, "DATE", sortDirection);
                },
                view -> {
                    if (buttons[1].isSelected()) {
                        sortDirection = toggleSort(sortDirection);
                    } else {
                        sortDirection = SortDirection.ASCENDING;
                    }
                    activatedButtonIndex = 1;
                    queryRides(null, null, "PRICE", sortDirection);
                },
                view -> {
                    if (buttons[2].isSelected()) {
                        sortDirection = toggleSort(sortDirection);
                    } else {
                        sortDirection = SortDirection.ASCENDING;
                    }
                    activatedButtonIndex = 2;
                    queryRides(null, null, "DESTINATION", sortDirection);
                }
        );
    }

    private Call<List<DriverRideHistoryDTO>> getHistory(
            String startDate, String endDate, String sortBy, SortDirection direction) {
        if (sortBy == null) sortBy = "DATE";
        if (direction == null) direction = SortDirection.ASCENDING;
        return profileApi.getDriverRideHistory(startDate, endDate, sortBy, direction.getKey());
    }

    private SortDirection toggleSort(SortDirection dir) {
        return dir == SortDirection.ASCENDING ? SortDirection.DESCENDING : SortDirection.ASCENDING;
    }

}