package com.example.gotaximobile.fragments.profile;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

public class EditPersonalProfile extends Fragment {

    private TextInputLayout tilName, tilEmail, tilAddress, tilPhone;
    private ShapeableImageView profileImage;

    // Registers the photo picker
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> { if (uri != null) profileImage.setImageURI(uri); }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewEPP = inflater.inflate(R.layout.fragment_edit_personal_profile, container, false);

        MaterialToolbar toolbar = viewEPP.findViewById(R.id.topAppBar);

        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        profileImage = viewEPP.findViewById(R.id.edit_profile_image);
        tilName = viewEPP.findViewById(R.id.til_name);
        tilEmail = viewEPP.findViewById(R.id.til_email);
        tilAddress = viewEPP.findViewById(R.id.til_address);
        tilPhone = viewEPP.findViewById(R.id.til_phone);

        viewEPP.findViewById(R.id.photo_container).setOnClickListener(view -> mGetContent.launch("image/*"));
        viewEPP.findViewById(R.id.btn_save_profile).setOnClickListener(view -> validateAndSave());

        return viewEPP;
    }

    private void validateAndSave() {
        String name = tilName.getEditText().getText().toString().trim();
        String email = tilEmail.getEditText().getText().toString().trim();
        String address = tilAddress.getEditText().getText().toString().trim();
        String phone = tilPhone.getEditText().getText().toString().trim();

        boolean isValid = true;

        if (name.isEmpty()) { tilName.setError("Name is required"); isValid = false; } else tilName.setError(null);
        if (address.isEmpty()) { tilAddress.setError("Address is required"); isValid = false; } else tilAddress.setError(null);

        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Invalid email format");
            isValid = false;
        } else tilEmail.setError(null);

        String phoneRegex = "^[0-9]{7,15}$";
        if (phone.isEmpty()) {
            tilPhone.setError("Phone is required");
            isValid = false;
        } else if (!phone.matches(phoneRegex)) {
            tilPhone.setError("Enter 7-15 digits only");
            isValid = false;
        } else tilPhone.setError(null);

        if (isValid) {
            Toast.makeText(getContext(), "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}