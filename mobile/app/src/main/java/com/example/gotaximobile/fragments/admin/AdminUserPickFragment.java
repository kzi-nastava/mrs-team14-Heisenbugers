package com.example.gotaximobile.fragments.admin;

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
import com.example.gotaximobile.models.dtos.AdminUserListItemDTO;
import com.example.gotaximobile.network.AdminApi;
import com.example.gotaximobile.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserPickFragment extends Fragment {

    private static final String ARG_DRIVERS = "drivers";
    private boolean drivers;

    public static AdminUserPickFragment newInstance(boolean drivers) {
        Bundle b = new Bundle();
        b.putBoolean(ARG_DRIVERS, drivers);
        AdminUserPickFragment f = new AdminUserPickFragment();
        f.setArguments(b);
        return f;
    }

    private AdminApi adminApi;
    private ProgressBar progress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_user_pick, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        drivers = getArguments() != null && getArguments().getBoolean(ARG_DRIVERS, false);
        adminApi = RetrofitClient.adminApi(requireContext());
        progress = view.findViewById(R.id.progress);

        RecyclerView rv = view.findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        AdminUserAdapter adapter = new AdminUserAdapter(user -> {
            AdminUserRideHistoryFragment next =
                    AdminUserRideHistoryFragment.newInstance(user.getId().toString(), drivers ? "driver" : "passenger");

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, next)
                    .addToBackStack(null)
                    .commit();;
        });
        rv.setAdapter(adapter);

        progress.setVisibility(View.VISIBLE);
        Call<List<AdminUserListItemDTO>> call = drivers ? adminApi.getDrivers() : adminApi.getPassengers();
        call.enqueue(new Callback<List<AdminUserListItemDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<AdminUserListItemDTO>> call,
                                   @NonNull Response<List<AdminUserListItemDTO>> response) {
                progress.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                adapter.submit(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<AdminUserListItemDTO>> call, @NonNull Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
