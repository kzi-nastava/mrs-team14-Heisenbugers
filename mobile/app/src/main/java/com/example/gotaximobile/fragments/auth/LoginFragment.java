package com.example.gotaximobile.fragments.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.activities.MainActivity;
import com.example.gotaximobile.activities.AuthActivity;
import com.example.gotaximobile.data.TokenStorage;
import com.example.gotaximobile.models.dtos.LoginRequestDTO;
import com.example.gotaximobile.models.dtos.LoginResponseDTO;
import com.example.gotaximobile.network.AuthApi;
import com.example.gotaximobile.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;

    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);

        MaterialButton btnSignIn = view.findViewById(R.id.btnSignIn);
        MaterialButton btnForgot = view.findViewById(R.id.btnForgotPassword);
        MaterialButton btnRegister = view.findViewById(R.id.btnGoToRegister);

        AuthApi api = RetrofitClient.authApi(requireContext());
        TokenStorage storage = new TokenStorage(requireContext());

        btnSignIn.setOnClickListener(v -> {
            clearErrors();
            if (!validate()) return;

            //imitation of successful login
            //startActivity(new Intent(requireContext(), MainActivity.class));
            //requireActivity().finish();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            api.login(new LoginRequestDTO(email, pass)).enqueue(new retrofit2.Callback<LoginResponseDTO>(){
                @Override
                public void onResponse(retrofit2.Call<LoginResponseDTO> call, retrofit2.Response<LoginResponseDTO> res) {
                    if (res.isSuccessful() && res.body() != null) {
                        storage.save(res.body());


                        //LoginResponseDTO response = res.body();
                        //String role = response.getRole();
                        //String userId = response.getUserId() != null ? response.getUserId().toString() : null;

                        //Intent intent = new Intent(requireContext(), MainActivity.class);
                        //intent.putExtra("USER_ROLE", role);
                        //intent.putExtra("USER_ID", userId);
                        //startActivity(intent);

                        startActivity(new Intent(requireContext(),MainActivity.class));
                        requireActivity().finish();

                        toast("Login successful!");
                        return;
                    }
                    String errorMsg = "Login failed";
                    if (res.code() == 401) {
                        errorMsg = "Invalid email or password";
                    } else if (res.code() == 403) {
                        errorMsg = "Account not activated";
                    } else if (res.errorBody() != null) {
                        try {
                            errorMsg = res.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    toast(errorMsg);

                }
                @Override
                public void onFailure(retrofit2.Call<LoginResponseDTO> call, Throwable t) {
                    toast("Network error: " + t.getMessage());
                    t.printStackTrace();
                }
            });
        });

        btnForgot.setOnClickListener(v -> ((AuthActivity) requireActivity()).openForgotPassword());
        btnRegister.setOnClickListener(v -> ((AuthActivity) requireActivity()).openRegister());
    }



    private void clearErrors() {
        tilEmail.setError(null);
        tilPassword.setError(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((com.example.gotaximobile.activities.AuthActivity) requireActivity()).showExitToMainArrow();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((com.example.gotaximobile.activities.AuthActivity) requireActivity()).restoreDefaultArrowBehavior();
    }

    private void toast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean validate() {
        String email = getText(etEmail);
        String pass = getText(etPassword);

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Invalid email");
            etEmail.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(pass)) {
            tilPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }

    private String getText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
