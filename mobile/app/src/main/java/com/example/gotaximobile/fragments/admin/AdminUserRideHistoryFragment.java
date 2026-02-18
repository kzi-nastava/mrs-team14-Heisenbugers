package com.example.gotaximobile.fragments.admin;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.gotaximobile.models.dtos.AdminRideListItemDTO;
import com.example.gotaximobile.network.AdminApi;
import com.example.gotaximobile.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserRideHistoryFragment extends Fragment {

    private static final String ARG_USER_ID = "userId";
    private static final String ARG_MODE = "mode"; // "driver" or "passenger"

    public static AdminUserRideHistoryFragment newInstance(String userId, String mode) {
        Bundle b = new Bundle();
        b.putString(ARG_USER_ID, userId);
        b.putString(ARG_MODE, mode);
        AdminUserRideHistoryFragment f = new AdminUserRideHistoryFragment();
        f.setArguments(b);
        return f;
    }

    private AdminApi adminApi;
    private ProgressBar progress;

    private TextInputEditText etFrom, etTo;
    private String sortField = "startedAt";
    private String sortDir = "desc";

    private String userId;
    private String mode;

    private AdminRideHistoryAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_user_ride_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adminApi = RetrofitClient.adminApi(requireContext());

        userId = getArguments() != null ? getArguments().getString(ARG_USER_ID) : null;
        mode = getArguments() != null ? getArguments().getString(ARG_MODE) : null;

        progress = view.findViewById(R.id.progress);
        etFrom = view.findViewById(R.id.etFrom);
        etTo = view.findViewById(R.id.etTo);

        RecyclerView rv = view.findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new AdminRideHistoryAdapter(item -> {
            Bundle b = new Bundle();
            b.putString("rideId", item.getRideId().toString());

            Fragment detail = new com.example.gotaximobile.fragments.admin.AdminRideHistoryDetailFragment();
            detail.setArguments(b);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detail)
                    .addToBackStack(null)
                    .commit();

        });
        rv.setAdapter(adapter);

        view.findViewById(R.id.btnApply).setOnClickListener(v -> load());
        view.findViewById(R.id.btnSortDate).setOnClickListener(v -> { sortField = "startedAt"; toggleDir(); load(); });
        view.findViewById(R.id.btnSortPrice).setOnClickListener(v -> { sortField = "price"; toggleDir(); load(); });
        view.findViewById(R.id.btnSortStatus).setOnClickListener(v -> { sortField = "status"; toggleDir(); load(); });

        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                getParentFragmentManager().popBackStack()
        );

        load();
    }

    private void toggleDir() { sortDir = "asc".equals(sortDir) ? "desc" : "asc"; }

    private void load() {
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(mode)) return;

        String fromIso = toIsoStartOfDay(text(etFrom)); // createdAt filter
        String toIso = toIsoEndOfDay(text(etTo));

        String driverId = "driver".equals(mode) ? userId : null;
        String passengerId = "passenger".equals(mode) ? userId : null;

        progress.setVisibility(View.VISIBLE);

        adminApi.searchRides(
                driverId,
                passengerId,
                null,
                fromIso,
                toIso,
                sortField + "," + sortDir
        ).enqueue(new Callback<List<AdminRideListItemDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<AdminRideListItemDTO>> call,
                                   @NonNull Response<List<AdminRideListItemDTO>> response) {
                progress.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                adapter.submit(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<AdminRideListItemDTO>> call, @NonNull Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error:"+t.getClass().getSimpleName()+" "+ t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String text(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }


    private String toIsoStartOfDay(String date) {
        if (date.isEmpty()) return null;
        try {
            LocalDate d = LocalDate.parse(date);
            return d.atStartOfDay().toString(); // "2026-02-18T00:00"
        } catch (Exception e) { return null; }
    }

    private String toIsoEndOfDay(String date) {
        if (date.isEmpty()) return null;
        try {
            LocalDate d = LocalDate.parse(date);
            return d.atTime(23,59,59).toString();
        } catch (Exception e) { return null; }
    }
}
