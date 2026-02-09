package com.example.gotaximobile.fragments.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.CreateVehicleDTO;
import com.example.gotaximobile.models.dtos.GetProfileDTO;
import com.example.gotaximobile.network.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditVehicle extends Fragment {

    private TextInputLayout tilModel, tilType, tilPlateNo, tilSeats;
    private MaterialSwitch switchBabies, switchPets;
    private AutoCompleteTextView actType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewEV = inflater.inflate(R.layout.fragment_edit_vehicle, container, false);

        MaterialToolbar toolbar = viewEV.findViewById(R.id.topAppBar);

        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        tilModel = viewEV.findViewById(R.id.til_model);
        tilType = viewEV.findViewById(R.id.til_type);
        tilPlateNo = viewEV.findViewById(R.id.til_plateNo);
        tilSeats = viewEV.findViewById(R.id.til_seats);

        if (getArguments() != null) {
            String existingModel = getArguments().getString("model");
            String existingType = getArguments().getString("type");
            String existingPlate = getArguments().getString("plate");
            String existingSeats = getArguments().getString("seats");
            boolean babies = getArguments().getBoolean("babies");
            boolean pets = getArguments().getBoolean("pets");

            if (tilModel.getEditText() != null) tilModel.getEditText().setText(existingModel);
            if (tilPlateNo.getEditText() != null) tilPlateNo.getEditText().setText(existingPlate);
            if (tilSeats.getEditText() != null) tilSeats.getEditText().setText(existingSeats);

            switchBabies = viewEV.findViewById(R.id.switch_babies);
            switchPets = viewEV.findViewById(R.id.switch_pets);
            if (switchBabies != null) switchBabies.setChecked(babies);
            if (switchPets != null) switchPets.setChecked(pets);
        }

        actType = viewEV.findViewById(R.id.act_type);

        String[] typeOptions = {"Standard", "Luxury", "Van"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.list_item,
                typeOptions
        );

        actType.setAdapter(adapter);

        if (getArguments() != null) {
            String existingType = getArguments().getString("type");
            if (existingType != null) {
                actType.setText(existingType, false);
            }
        }

        viewEV.findViewById(R.id.btn_save_profile).setOnClickListener(view -> validateAndSave());

        return viewEV;
    }

    private void validateAndSave() {
        String model = tilModel.getEditText().getText().toString().trim();
        String type = tilType.getEditText().getText().toString().trim();
        String plateNo = tilPlateNo.getEditText().getText().toString().trim();
        String seats = tilSeats.getEditText().getText().toString().trim();

        boolean isValid = true;

        if (model.isEmpty()) { tilModel.setError("Model is required"); isValid = false; } else tilModel.setError(null);
        if (plateNo.isEmpty()) { tilPlateNo.setError("Plate Number is required"); isValid = false; } else tilPlateNo.setError(null);

        if (type.isEmpty()) {
            tilType.setError("Type is required");
            isValid = false;
        } else tilType.setError(null);

        if (seats.isEmpty()) {
            tilSeats.setError("Seats is required");
            isValid = false;
        }  else tilSeats.setError(null);

        if (isValid) {
            updateVehicleData(new CreateVehicleDTO(
                    model, type, plateNo, Integer.parseInt(seats), switchBabies.isChecked(), switchPets.isChecked()
            ));
        }
    }

    private void updateVehicleData(CreateVehicleDTO vehicleDTO) {
        RetrofitClient.profileService(getContext()).updateVehicle(vehicleDTO).enqueue(new Callback<CreateVehicleDTO>() {
            @Override
            public void onResponse(@NonNull Call<CreateVehicleDTO> call, @NonNull Response<CreateVehicleDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CreateVehicleDTO vehicle = response.body();
                    Toast.makeText(getContext(), "Vehicle Information sent to administrator for review.", Toast.LENGTH_SHORT).show();
                    //updateView(profile);
                } else {
                    Log.e("API_ERROR", "Response failed: " + response.code());
                    Toast.makeText(getContext(), "There was an error!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreateVehicleDTO> call, @NonNull Throwable t) {
                Log.e("NETWORK_ERROR", Objects.requireNonNull(t.getMessage()));
                Toast.makeText(getContext(), "There was an error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}