package com.example.gotaximobile.fragments.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.PriceDTO;
import com.example.gotaximobile.network.AdminApi;
import com.example.gotaximobile.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPriceFragment extends Fragment {

    private TextInputEditText etStandard, etLuxury, etVan;
    private double oldStandard, oldLuxury, oldVan;

    private AdminApi api;

    public AdminPriceFragment() {
        super(R.layout.fragment_admin_price);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etStandard = view.findViewById(R.id.etStandard);
        etLuxury = view.findViewById(R.id.etLuxury);
        etVan = view.findViewById(R.id.etVan);
        MaterialButton btnSave = view.findViewById(R.id.btnSave);

        api = RetrofitClient.adminApi(requireContext());

        loadPrices();

        btnSave.setOnClickListener(v -> savePrices());
    }

    private void loadPrices() {
        api.getPrices().enqueue(new Callback<List<PriceDTO>>() {
            @Override
            public void onResponse(Call<List<PriceDTO>> call, Response<List<PriceDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (PriceDTO p : response.body()) {
                        switch (p.getVehicleType()) {
                            case "STANDARD":
                                oldStandard = p.getStartingPrice();
                                etStandard.setText(String.valueOf(oldStandard));
                                break;
                            case "LUXURY":
                                oldLuxury = p.getStartingPrice();
                                etLuxury.setText(String.valueOf(oldLuxury));
                                break;
                            case "VAN":
                                oldVan = p.getStartingPrice();
                                etVan.setText(String.valueOf(oldVan));
                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PriceDTO>> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to load prices", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePrices() {

        List<PriceDTO> updated = new ArrayList<>();

        double standard = Double.parseDouble(etStandard.getText().toString());
        double luxury = Double.parseDouble(etLuxury.getText().toString());
        double van = Double.parseDouble(etVan.getText().toString());

        if (standard != oldStandard)
            updated.add(new PriceDTO("STANDARD", standard));
        if (luxury != oldLuxury)
            updated.add(new PriceDTO("LUXURY", luxury));
        if (van != oldVan)
            updated.add(new PriceDTO("VAN", van));

        if (updated.isEmpty()) {
            Toast.makeText(requireContext(), "No changes to save", Toast.LENGTH_SHORT).show();
            return;
        }

        api.savePrices(updated).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(requireContext(), "Prices saved", Toast.LENGTH_SHORT).show();
                oldStandard = standard;
                oldLuxury = luxury;
                oldVan = van;
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to save prices", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

