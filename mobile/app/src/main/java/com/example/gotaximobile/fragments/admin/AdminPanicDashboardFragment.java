package com.example.gotaximobile.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.MessageResponse;
import com.example.gotaximobile.models.dtos.PanicEventDTO;
import com.example.gotaximobile.network.AdminPanicApi;
import com.example.gotaximobile.network.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPanicDashboardFragment extends Fragment {

    private AdminPanicApi api;
    private AdminPanicAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_admin_panic_dashboard, container, false);

        api = RetrofitClient.adminPanicApi(requireContext());

        MaterialToolbar toolbar = v.findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(view ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        RecyclerView rv = v.findViewById(R.id.rvPanics);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));


        adapter = new AdminPanicAdapter(new AdminPanicAdapter.Listener() {
            @Override
            public void onResolve(PanicEventDTO event) {
                resolvePanic(event);
            }

            @Override
            public void onOpenDetails(PanicEventDTO event) {
                // пока просто заглушка, потом сделаем детальный экран
                Toast.makeText(requireContext(), "Open details: " + event.getId(), Toast.LENGTH_SHORT).show();

                AdminPanicDetailFragment f = AdminPanicDetailFragment.newInstance(event);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, f)
                        .addToBackStack(null)
                        .commit();
            }
        });

        rv.setAdapter(adapter);

        rv.setAdapter(adapter);

        loadPanics();

        return v;
    }

    private void loadPanics() {
        api.getActivePanics().enqueue(new Callback<List<PanicEventDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<PanicEventDTO>> call,
                                   @NonNull Response<List<PanicEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setItems(response.body());
                } else {
                    Toast.makeText(requireContext(), "Failed to load panics", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PanicEventDTO>> call,
                                  @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resolvePanic(PanicEventDTO event) {
        if (event.getId() == null) return;

        api.resolve(event.getId().toString()).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call,
                                   @NonNull Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Panic resolved", Toast.LENGTH_SHORT).show();
                    loadPanics(); // перезагрузить список, чтобы событие пропало
                } else {
                    Toast.makeText(requireContext(), "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call,
                                  @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
