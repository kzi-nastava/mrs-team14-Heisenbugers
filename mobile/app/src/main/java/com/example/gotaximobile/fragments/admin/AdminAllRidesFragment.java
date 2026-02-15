package com.example.gotaximobile.fragments.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.AdminRideDTO;
import com.example.gotaximobile.network.RetrofitClient;
import com.example.gotaximobile.network.RideService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAllRidesFragment extends Fragment {

    private EditText searchEditText;
    private RecyclerView ridesRecyclerView;
    private TextView noRidesTextView;
    private AdminRideAdapter adapter;
    private List<AdminRideDTO> rideList = new ArrayList<>();
    private List<AdminRideDTO> filteredList = new ArrayList<>();

    private RideService rideService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_all_rides, container, false);

        rideService = RetrofitClient.rideService(requireContext());

        searchEditText = view.findViewById(R.id.searchEditText);
        ridesRecyclerView = view.findViewById(R.id.ridesRecyclerView);
        noRidesTextView = view.findViewById(R.id.noRidesTextView);

        adapter = new AdminRideAdapter(filteredList, ride -> openRideDetails(ride));
        ridesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ridesRecyclerView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                applyFilter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        loadRides();
        return view;
    }

    private void applyFilter(String term) {
        filteredList.clear();
        for (AdminRideDTO r : rideList) {
            if ((r.driver.firstName + " " + r.driver.lastName + " " +
                    r.ride.startAddress + " " + r.ride.destinationAddress + " " +
                    r.ride.startedAt + " " + (r.ride.endedAt != null ? r.ride.endedAt : ""))
                    .toLowerCase().contains(term.toLowerCase())) {
                filteredList.add(r);
            }
        }
        noRidesTextView.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
    }

    private void openRideDetails(AdminRideDTO ride) {
        RideDetailFragment fragment = RideDetailFragment.newInstance(ride);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment) // make sure you have a container in your activity layout
                .addToBackStack(null)
                .commit();

    }

    private void loadRides() {
        rideList.clear();

        rideService.getAllRides().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<AdminRideDTO>> call,
                                   @NonNull Response<List<AdminRideDTO>> response) {
                rideList = response.body();
                applyFilter(searchEditText.getText().toString());

            }

            @Override
            public void onFailure(@NonNull Call<List<AdminRideDTO>> call, @NonNull Throwable t) {

            }
        });

    }
}
