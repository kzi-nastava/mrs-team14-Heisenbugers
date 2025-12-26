package com.example.gotaximobile.fragments.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gotaximobile.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;

public class EditVehicle extends Fragment {

    private TextInputLayout tilModel, tilType, tilPlateNo, tilSeats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewEPP = inflater.inflate(R.layout.fragment_edit_vehicle, container, false);

        MaterialToolbar toolbar = viewEPP.findViewById(R.id.topAppBar);

        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });


        tilModel = viewEPP.findViewById(R.id.til_model);
        tilType = viewEPP.findViewById(R.id.til_type);
        tilPlateNo = viewEPP.findViewById(R.id.til_plateNo);
        tilSeats = viewEPP.findViewById(R.id.til_seats);

        viewEPP.findViewById(R.id.btn_save_profile).setOnClickListener(view -> validateAndSave());

        return viewEPP;
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
            Toast.makeText(getContext(), "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}