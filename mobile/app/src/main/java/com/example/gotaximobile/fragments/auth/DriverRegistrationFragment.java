package com.example.gotaximobile.fragments.auth;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.CreateDriverDTO;
import com.example.gotaximobile.models.dtos.CreateVehicleDTO;
import com.example.gotaximobile.models.dtos.CreatedDriverDTO;
import com.example.gotaximobile.models.dtos.GetProfileDTO;
import com.example.gotaximobile.network.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.materialswitch.MaterialSwitch;
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

public class DriverRegistrationFragment extends Fragment {

    private ActivityResultLauncher<String> galleryLauncher;
    private ImageView ivProfilePic;
    private Uri selectedImageUri;
    private AutoCompleteTextView actvType;
    private String encodedImage = ""; // To store Base64 image

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedImageUri = uri;
                ivProfilePic.setImageURI(uri);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // This connects your Java code to the XML layout we created earlier
        return inflater.inflate(R.layout.fragment_driver_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        actvType = view.findViewById(R.id.actvVehicleType);

        String[] types = {"Standard", "Van", "Luxury"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, types);
        actvType.setAdapter(adapter);

        view.findViewById(R.id.cardProfilePic).setOnClickListener(v -> galleryLauncher.launch("image/*"));

        view.findViewById(R.id.btnRegister).setOnClickListener(v -> validateAndRegister(view));

        MaterialToolbar toolbar = view.findViewById(R.id.topAppBarDriverReg);
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });
    }

    private void validateAndRegister(View v) {
        boolean isValid = true;

        TextInputLayout tilFirstName = v.findViewById(R.id.tilFirstName);
        TextInputLayout tilLastName = v.findViewById(R.id.tilLastName);
        TextInputLayout tilEmail = v.findViewById(R.id.tilEmail);
        TextInputLayout tilPhone = v.findViewById(R.id.tilPhone);
        TextInputLayout tilAddress = v.findViewById(R.id.tilAddress);
        TextInputLayout tilVehicleModel = v.findViewById(R.id.tilVehicleModel);
        TextInputLayout tilVehicleType = v.findViewById(R.id.tilVehicleType);
        TextInputLayout tilLicensePlate = v.findViewById(R.id.tilPlate);
        TextInputLayout tilSeats = v.findViewById(R.id.tilSeats);

        String selectedType = actvType.getText().toString();

        String email = tilEmail.getEditText().getText().toString().trim();
        String phone = tilPhone.getEditText().getText().toString().trim();

        MaterialSwitch babyTransport = v.findViewById(R.id.switchBaby);
        MaterialSwitch petTransport = v.findViewById(R.id.switchPets);


        if (tilFirstName.getEditText().getText().toString().isEmpty()) {
            tilFirstName.setError("Required");
            isValid = false;
        } else tilFirstName.setError(null);

        if (tilLastName.getEditText().getText().toString().isEmpty()) {
            tilLastName.setError("Required");
            isValid = false;
        } else tilLastName.setError(null);

        if (tilAddress.getEditText().getText().toString().isEmpty()) {
            tilAddress.setError("Required");
            isValid = false;
        } else tilAddress.setError(null);

        if (tilVehicleModel.getEditText().getText().toString().isEmpty()) {
            tilVehicleModel.setError("Required");
            isValid = false;
        } else tilVehicleModel.setError(null);

        if (tilLicensePlate.getEditText().getText().toString().isEmpty()) {
            tilLicensePlate.setError("Required");
            isValid = false;
        } else tilLicensePlate.setError(null);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Invalid email format");
            isValid = false;
        } else tilEmail.setError(null);

        if (phone.length() < 6) {
            tilPhone.setError("Phone too short");
            isValid = false;
        } else tilPhone.setError(null);

        try {
            int seats = Integer.parseInt(tilSeats.getEditText().getText().toString());
            if (seats < 1 || seats > 8) {
                tilSeats.setError("Must be 1-8");
                isValid = false;
            } else tilSeats.setError(null);
        } catch (Exception e) {
            tilSeats.setError("Invalid number");
            isValid = false;
        }

        if (selectedType.isEmpty()) {
            tilVehicleType.setError("Please select a vehicle type");
            isValid = false;
        } else {
            boolean match = false;
            for (String s : new String[]{"Standard", "Van", "Luxury"}) {
                if (s.equals(selectedType)) {
                    match = true;
                    break;
                }
            }

            if (!match) {
                tilVehicleType.setError("Invalid selection");
                isValid = false;
            } else {
                tilVehicleType.setError(null);
            }
        }

        if (isValid) {
            CreateDriverDTO dto = new CreateDriverDTO(
                    tilEmail.getEditText().getText().toString(),
                    tilFirstName.getEditText().getText().toString(),
                    tilLastName.getEditText().getText().toString(),
                    tilPhone.getEditText().getText().toString(),
                    tilAddress.getEditText().getText().toString(),
                    null,
                    new CreateVehicleDTO(
                            tilVehicleModel.getEditText().getText().toString(),
                            actvType.getText().toString().toUpperCase(),
                            tilLicensePlate.getEditText().getText().toString(),
                            Integer.parseInt(tilSeats.getEditText().getText().toString()),
                            babyTransport.isChecked(),
                            petTransport.isChecked()
                    )
            );

            MultipartBody.Part body = null;
            if (selectedImageUri != null) {
                body = prepareImagePart(selectedImageUri);
            }

            registerDriver(dto, body);
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

    private void registerDriver(CreateDriverDTO driverDTO, MultipartBody.Part image) {
        RetrofitClient.authApi(getContext()).registerDriver(driverDTO, image).enqueue(new Callback<CreatedDriverDTO>() {
            @Override
            public void onResponse(@NonNull Call<CreatedDriverDTO> call, @NonNull Response<CreatedDriverDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CreatedDriverDTO profile = response.body();
                    Toast.makeText(getContext(), "Driver Registered Successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("API_ERROR", "Response failed: " + response.code());
                    Toast.makeText(getContext(), "There was an error!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreatedDriverDTO> call, @NonNull Throwable t) {
                Log.e("NETWORK_ERROR", Objects.requireNonNull(t.getMessage()));
                Toast.makeText(getContext(), "There was an error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}