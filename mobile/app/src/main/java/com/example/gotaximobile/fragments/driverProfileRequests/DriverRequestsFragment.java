package com.example.gotaximobile.fragments.driverProfileRequests;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gotaximobile.R;
import com.example.gotaximobile.adapters.DriverRequestAdapter;
import com.example.gotaximobile.models.dtos.DriverRequestListDTO;
import com.example.gotaximobile.network.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverRequestsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DriverRequestAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_requests, container, false);

        recyclerView = view.findViewById(R.id.rv_driver_requests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchRequests();

        return view;
    }

    private void fetchRequests() {
        RetrofitClient.profileService(getContext()).getDriverRequests().enqueue(new Callback<List<DriverRequestListDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<DriverRequestListDTO>> call, @NonNull Response<List<DriverRequestListDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new DriverRequestAdapter(response.body(), request -> {
                        DriverRequestDetailsFragment detailsFragment = new DriverRequestDetailsFragment();
                        Bundle args = new Bundle();
                        args.putString("requestId", request.id.toString());
                        detailsFragment.setArguments(args);

                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, detailsFragment)
                                .addToBackStack(null)
                                .commit();
                    });
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<DriverRequestListDTO>> call, @NonNull Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View activityToolbar = requireActivity().findViewById(R.id.top_app_bar);
        if (activityToolbar != null) activityToolbar.setVisibility(View.GONE);

        MaterialToolbar toolbar = view.findViewById(R.id.topAppBarDR);
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });
    }


}