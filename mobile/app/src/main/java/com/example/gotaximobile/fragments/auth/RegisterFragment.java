package com.example.gotaximobile.fragments.auth;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.activities.AuthActivity;
import com.example.gotaximobile.models.dtos.RegisterResponseDTO;
import com.example.gotaximobile.network.AuthApi;
import com.example.gotaximobile.network.PartUtil;
import com.example.gotaximobile.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private TextInputLayout tilName, tilSurname, tilAddress, tilPhone, tilEmail, tilPassword, tilConfirm;
    private TextInputEditText etName, etSurname, etAddress, etPhone, etEmail, etPassword, etConfirm;


    private ImageView imgCamera;
    private Uri selectedImageUri;
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    if (imgCamera != null) {
                        imgCamera.setAlpha(1f);
                        imgCamera.setImageURI(uri);
                        imgCamera.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }
            });

    public RegisterFragment() {
        super(R.layout.fragment_register);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindViews(view);


        imgCamera = view.findViewById(R.id.imgCamera);

        MaterialButton btnAddPhoto = view.findViewById(R.id.btnAddPhoto);
        MaterialButton btnCreate = view.findViewById(R.id.btnCreateAccount);
        MaterialButton btnGoToLogin = view.findViewById(R.id.btnGoToLogin);

        btnAddPhoto.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        AuthApi api = RetrofitClient.authApi(requireContext());


        btnCreate.setOnClickListener(v -> {
            clearErrors();
            if (!validate()) {
                Toast.makeText(requireContext(), "Please fix the highlighted fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            MultipartBody.Part profileImagePart = null;
            if (selectedImageUri != null) {
                try {
                    String mimeType = requireContext()
                            .getContentResolver()
                            .getType(selectedImageUri);
                    if (mimeType == null) mimeType = "image/*";

                    byte[] bytes = readAllBytesFromUri(selectedImageUri);

                    RequestBody fileBody = RequestBody.create(
                            MediaType.parse(mimeType),
                            bytes
                    );

                    profileImagePart = MultipartBody.Part.createFormData(
                            "profileImage",
                            "avatar.jpg",
                            fileBody
                    );
                }catch (IOException e) {
                    e.printStackTrace();
                    profileImagePart = null;
                }
            }

            okhttp3.MultipartBody.Part imagePart = null;


            api.registerPassenger(
                    PartUtil.text(getText(etEmail)),
                    PartUtil.text(getText(etPassword)),
                    PartUtil.text(getText(etConfirm)),
                    PartUtil.text(getText(etName)),
                    PartUtil.text(getText(etSurname)),
                    PartUtil.text(getText(etPhone)),
                    PartUtil.text(getText(etAddress)),
                    profileImagePart
            ).enqueue(new retrofit2.Callback<RegisterResponseDTO>() {
                @Override
                public void onResponse(retrofit2.Call<RegisterResponseDTO> call, retrofit2.Response<RegisterResponseDTO> res)
                {
                    if (res.isSuccessful() && res.body() != null) {
                        toast(res.body().getMessage()); // "Check email to activate..."
                        ((AuthActivity) requireActivity()).openLogin(true);

                    }else {
                        String errorBody = "";
                        try {
                            if (res.errorBody() != null) {
                                errorBody = res.errorBody().string();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        toast("Register failed " + res.code() + ": " + errorBody);

                        System.out.println("Register error: " + errorBody);
                    }


                    toast("Register failed: " + res.code());
                }
                @Override
                public void onFailure(retrofit2.Call<RegisterResponseDTO> call, Throwable t) {
                    toast("Network error: " + t.getMessage());
                    t.printStackTrace();
                }
            });


            Toast.makeText(requireContext(),
                    "Registered. Now activate via email link.",
                    Toast.LENGTH_LONG).show();

        });

        btnGoToLogin.setOnClickListener(v -> goToLogin());
    }
    private void toast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void goToLogin() {
        requireActivity().getSupportFragmentManager().popBackStack(null,
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ((AuthActivity) requireActivity()).openLogin(false);
    }
    private byte[] readAllBytesFromUri(Uri uri) throws IOException {
        try (InputStream is = requireContext().getContentResolver().openInputStream(uri)) {
            if (is == null) return new byte[0];
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[8192];
            int nRead;
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        }
    }

    private void bindViews(View v) {
        tilName = v.findViewById(R.id.tilName);
        tilSurname = v.findViewById(R.id.tilSurname);
        tilAddress = v.findViewById(R.id.tilAddress);
        tilPhone = v.findViewById(R.id.tilPhone);
        tilEmail = v.findViewById(R.id.tilEmail);
        tilPassword = v.findViewById(R.id.tilPassword);
        tilConfirm = v.findViewById(R.id.tilConfirm);

        etName = v.findViewById(R.id.etName);
        etSurname = v.findViewById(R.id.etSurname);
        etAddress = v.findViewById(R.id.etAddress);
        etPhone = v.findViewById(R.id.etPhone);
        etEmail = v.findViewById(R.id.etEmail);
        etPassword = v.findViewById(R.id.etPassword);
        etConfirm = v.findViewById(R.id.etConfirm);
    }

    private void clearErrors() {
        tilName.setError(null);
        tilSurname.setError(null);
        tilAddress.setError(null);
        tilPhone.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirm.setError(null);
    }

    private boolean validate() {
        boolean ok = true;

        String name = getText(etName);
        String surname = getText(etSurname);
        String address = getText(etAddress);
        String phone = getText(etPhone);
        String email = getText(etEmail);
        String pass = getText(etPassword);
        String confirm = getText(etConfirm);

        if (TextUtils.isEmpty(name)) { tilName.setError("Required"); ok = false; }
        if (TextUtils.isEmpty(surname)) { tilSurname.setError("Required"); ok = false; }
        if (TextUtils.isEmpty(address)) { tilAddress.setError("Required"); ok = false; }
        if (TextUtils.isEmpty(phone)) { tilPhone.setError("Required"); ok = false; }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Required");
            ok = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Invalid email");
            ok = false;
        }

        if (TextUtils.isEmpty(pass)) {
            tilPassword.setError("Required");
            ok = false;
        } else if (pass.length() < 6) {
            tilPassword.setError("Min 6 characters");
            ok = false;
        }

        if (TextUtils.isEmpty(confirm)) {
            tilConfirm.setError("Required");
            ok = false;
        } else if (!confirm.equals(pass)) {
            tilConfirm.setError("Passwords do not match");
            ok = false;
        }

        return ok;
    }

    private String getText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
