package com.example.gotaximobile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.Ride;
import com.example.gotaximobile.models.dtos.DriverRideHistoryDTO;
import com.example.gotaximobile.network.ProfileService;
import com.example.gotaximobile.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private ProfileService profileApi;
    private List<Ride> rides = new ArrayList<>();

    private RecyclerView recyclerView;
    private MyRideRecyclerViewAdapter adapter;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RideFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        View targetView = view.findViewById(R.id.list);

        profileApi = RetrofitClient.profileService(requireContext());
        queryRides();

        Button btnSortDate = view.findViewById(R.id.btnSortDate);
        Button btnSortPrice = view.findViewById(R.id.btnSortPrice);
        Button btnSortPlace = view.findViewById(R.id.btnSortPlace);

        btnSortDate.setSelected(true);

        buttons = new Button[]{btnSortDate, btnSortPrice, btnSortPlace};

        for (Button btn : buttons) {
            btn.setOnClickListener(v -> {
                selectButton(btn);
            });
        }

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


    private void queryRides() {
        profileApi.getDriverRideHistory().enqueue(new Callback<List<DriverRideHistoryDTO>>() {
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

}