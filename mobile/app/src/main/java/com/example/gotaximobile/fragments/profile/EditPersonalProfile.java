package com.example.gotaximobile.fragments.profile;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gotaximobile.BuildConfig;
import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.GetProfileDTO;
import com.example.gotaximobile.network.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditPersonalProfile extends Fragment {

    private TextInputLayout tilName, tilEmail, tilAddress, tilPhone;
    private ShapeableImageView profileImage;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    profileImage.setImageURI(uri);
                }
            }
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

        if (getArguments() != null) {
            String existingName = getArguments().getString("name");
            String existingEmail = getArguments().getString("email");
            String existingAddress = getArguments().getString("address");
            String existingPhone = getArguments().getString("phone");
            String existingImage = getArguments().getString("profilePhoto");

            if (existingImage != null) {
                String correctedUrl = existingImage.replace("http://localhost:8081/", BuildConfig.BASE_URL);

                Glide.with(this)
                        .load(correctedUrl)
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(profileImage);
            }

            if (tilName.getEditText() != null) tilName.getEditText().setText(existingName);
            if (tilEmail.getEditText() != null) tilEmail.getEditText().setText(existingEmail);
            if (tilAddress.getEditText() != null) tilAddress.getEditText().setText(existingAddress);
            if (tilPhone.getEditText() != null) tilPhone.getEditText().setText(existingPhone);
        }

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
            String fullName = tilName.getEditText().getText().toString().trim();
            String[] nameParts = fullName.split(" ", 2);
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            GetProfileDTO dto = new GetProfileDTO(
                    null,
                    tilEmail.getEditText().getText().toString(),
                    firstName,
                    lastName,
                    tilPhone.getEditText().getText().toString(),
                    tilAddress.getEditText().getText().toString(),
                    null
            );

            MultipartBody.Part body = null;
            if (selectedImageUri != null) {
                body = prepareImagePart(selectedImageUri);
            }

            updateProfileData(dto, body);
        }
    }

    private MultipartBody.Part prepareImagePart(Uri fileUri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(fileUri);
            File tempFile = new File(requireContext().getCacheDir(), "upload_image.jpg");

            OutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            RequestBody requestFile = RequestBody.create(tempFile, MediaType.parse(Objects.requireNonNull(requireContext().getContentResolver().getType(fileUri))));

            return MultipartBody.Part.createFormData("image", tempFile.getName(), requestFile);
        } catch (IOException e) {
            Log.e("IMAGE_UPLOAD", "Error creating image part", e);
            return null;
        }
    }

    private void updateProfileData(GetProfileDTO profileDTO, MultipartBody.Part image) {
        RetrofitClient.profileService(getContext()).updatePersonalInfromation(profileDTO, image).enqueue(new Callback<GetProfileDTO>() {
            @Override
            public void onResponse(@NonNull Call<GetProfileDTO> call, @NonNull Response<GetProfileDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetProfileDTO profile = response.body();
                    Toast.makeText(getContext(), "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                    //updateView(profile);
                } else {
                    Log.e("API_ERROR", "Response failed: " + response.code());
                    Toast.makeText(getContext(), "There was an error!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetProfileDTO> call, @NonNull Throwable t) {
                Log.e("NETWORK_ERROR", Objects.requireNonNull(t.getMessage()));
                Toast.makeText(getContext(), "There was an error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}