package com.example.gotaximobile.fragments.driverProfileRequests;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gotaximobile.BuildConfig;
import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.DriverProfileDTO;
import com.example.gotaximobile.models.dtos.DriverProfileRequestDetailDTO;
import com.example.gotaximobile.network.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverRequestDetailsFragment extends Fragment {

    private LinearLayout detailsContainer;
    private String requestId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_request_details, container, false);

        detailsContainer = view.findViewById(R.id.details_container);
        MaterialToolbar toolbar = view.findViewById(R.id.topAppBarDRD);

        toolbar.setNavigationOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        if (getArguments() != null) {
            requestId = getArguments().getString("requestId");
            fetchDetails();
        }

        view.findViewById(R.id.btn_approve).setOnClickListener(v -> handleDecision(true));
        view.findViewById(R.id.btn_reject).setOnClickListener(v -> handleDecision(false));

        return view;
    }

    private void fetchDetails() {
        RetrofitClient.profileService(getContext()).getDriverRequest(UUID.fromString(requestId)).enqueue(new Callback<DriverProfileRequestDetailDTO>() {
            @Override
            public void onResponse(@NonNull Call<DriverProfileRequestDetailDTO> call, @NonNull Response<DriverProfileRequestDetailDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    renderDiff(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<DriverProfileRequestDetailDTO> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error loading details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addImageDiff(String oldUrl, String newUrl) {
        View row = getLayoutInflater().inflate(R.layout.item_profile_image_diff, detailsContainer, false);

        View layoutChanged = row.findViewById(R.id.layout_images_changed);
        View layoutSame = row.findViewById(R.id.layout_image_same);


        if (Objects.equals(oldUrl, newUrl)) {
            layoutChanged.setVisibility(View.GONE);
            layoutSame.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(newUrl.replace("http://localhost:8081/", BuildConfig.BASE_URL))
                    .placeholder(R.drawable.ic_profile)
                    .into((ImageView) row.findViewById(R.id.img_same));
        } else {
            layoutChanged.setVisibility(View.VISIBLE);
            layoutSame.setVisibility(View.GONE);

            Glide.with(this).load(oldUrl.replace("http://localhost:8081/", BuildConfig.BASE_URL)).into((ImageView) row.findViewById(R.id.img_old));
            Glide.with(this).load(newUrl.replace("http://localhost:8081/", BuildConfig.BASE_URL)).into((ImageView) row.findViewById(R.id.img_new));
        }

        detailsContainer.addView(row, 0);
    }

    private void renderDiff(DriverProfileRequestDetailDTO data) {
        detailsContainer.removeAllViews();
        DriverProfileDTO oldP = data.oldProfile;
        DriverProfileDTO newP = data.newProfile;

        addImageDiff(oldP.profileImageUrl, newP.profileImageUrl);

        addSectionHeader("Personal Information");
        addDiffRow("First Name", oldP.firstName, newP.firstName);
        addDiffRow("Last Name", oldP.lastName, newP.lastName);
        addDiffRow("Phone", oldP.phone, newP.phone);
        addDiffRow("Address", oldP.address, newP.address);

        addSectionHeader("Vehicle Details");
        addDiffRow("Model", oldP.model, newP.model);
        addDiffRow("Plate", oldP.licensePlate, newP.licensePlate);
        addDiffRow("Seats", String.valueOf(oldP.seatCount), String.valueOf(newP.seatCount));
        addDiffRow("Pets Allowed", formatBool(oldP.petTransport), formatBool(newP.petTransport));
        addDiffRow("Babies Allowed", formatBool(oldP.babyTransport), formatBool(newP.babyTransport));
    }

    private void addDiffRow(String label, String oldVal, String newVal) {
        View row = getLayoutInflater().inflate(R.layout.item_profile_diff, detailsContainer, false);
        TextView tvLabel = row.findViewById(R.id.label);
        TextView tvOld = row.findViewById(R.id.old_value);
        TextView tvNew = row.findViewById(R.id.new_value);

        tvLabel.setText(label);
        tvNew.setText(newVal);

        if (!Objects.equals(oldVal, newVal)) {
            tvOld.setVisibility(View.VISIBLE);
            tvOld.setText("Was: " + oldVal);
            tvOld.setPaintFlags(tvOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvNew.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_primary));
        }
        detailsContainer.addView(row);
    }

    private void addSectionHeader(String title) {
        TextView header = new TextView(getContext());
        header.setText(title);
        header.setPadding(0, 32, 0, 16);
        header.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleMedium);
        detailsContainer.addView(header);
    }

    private String formatBool(boolean b) { return b ? "Yes" : "No"; }

    private void handleDecision(boolean approved) {
        if (approved){
            RetrofitClient.profileService(getContext()).approveRequest(UUID.fromString(requestId)).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Approved!", Toast.LENGTH_SHORT).show();
                        requireActivity().getOnBackPressedDispatcher().onBackPressed();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "Action failed", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            RetrofitClient.profileService(getContext()).rejectRequest(UUID.fromString(requestId)).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Rejected!", Toast.LENGTH_SHORT).show();
                        requireActivity().getOnBackPressedDispatcher().onBackPressed();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "Action failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}