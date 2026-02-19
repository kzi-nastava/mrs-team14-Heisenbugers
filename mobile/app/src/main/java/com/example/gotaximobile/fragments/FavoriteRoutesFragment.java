package com.example.gotaximobile.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gotaximobile.R;
import com.example.gotaximobile.activities.MainActivity;
import com.example.gotaximobile.adapters.FavoriteRoutesAdapter;
import com.example.gotaximobile.models.dtos.FavoriteRouteDTO;
import com.example.gotaximobile.network.RetrofitClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteRoutesFragment extends Fragment {
    private RecyclerView recyclerView;
    private FavoriteRoutesAdapter adapter;
    private List<FavoriteRouteDTO> favoriteRoutes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_routes, container, false);
        recyclerView = view.findViewById(R.id.rvFavoriteRoutes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadFavorites();
        return view;
    }

    private void loadFavorites() {
        RetrofitClient.rideService(getContext()).getFavoriteRoutes().enqueue(new Callback<List<FavoriteRouteDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<FavoriteRouteDTO>> call, @NonNull Response<List<FavoriteRouteDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favoriteRoutes = response.body();
                    setupAdapter();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<FavoriteRouteDTO>> call, @NonNull Throwable t) { /* Toast error */ }
        });
    }

    private void deleteFavorite(UUID id){
        RetrofitClient.rideService(getContext()).deleteFavorite(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Route removed from favorites!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) { /* Toast error */ }
        });
    }

    private void setupAdapter() {
        adapter = new FavoriteRoutesAdapter(favoriteRoutes, new FavoriteRoutesAdapter.OnRouteClickListener() {
            @Override
            public void onUse(FavoriteRouteDTO route) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("selected_favorite", route);

                getParentFragmentManager().setFragmentResult("favorite_selected", bundle);

                //((MainActivity)requireActivity()).navigateToHome();
            }

            @Override
            public void onDelete(FavoriteRouteDTO route) {
                deleteFavorite(route.Id);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}