package com.example.gotaximobile.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.fragments.admin.AdminPanicAdapter;
import com.example.gotaximobile.models.dtos.MessageResponse;
import com.example.gotaximobile.models.dtos.PanicEventDTO;
import com.example.gotaximobile.network.AdminPanicApi;
import com.example.gotaximobile.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPanicFragment extends Fragment {

    private AdminPanicApi api;
    private AdminPanicAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_admin_panic_list, container, false);

        api = RetrofitClient.adminPanicApi(requireContext());

        RecyclerView rv = v.findViewById(R.id.recyclerPanic);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AdminPanicAdapter(new AdminPanicAdapter.Listener() {
            @Override
            public void onResolve(PanicEventDTO event) {
                resolvePanic(event);
            }

            @Override
            public void onOpenDetails(PanicEventDTO event) {
                AdminPanicDetailFragment f = AdminPanicDetailFragment.newInstance(event);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, f)
                        .addToBackStack(null)
                        .commit();
            }
        });

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
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PanicEventDTO>> call,
                                  @NonNull Throwable t) { }
        });
    }

    private void resolvePanic(PanicEventDTO e) {
        if (e.getId() == null) return;
        api.resolve(e.getId().toString()).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call,
                                   @NonNull Response<MessageResponse> response) {
                loadPanics();
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call,
                                  @NonNull Throwable t) { }
        });
    }
}
