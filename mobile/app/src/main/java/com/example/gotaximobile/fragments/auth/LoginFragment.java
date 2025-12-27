package com.example.gotaximobile.fragments.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.activities.MainActivity;
import com.example.gotaximobile.activities.AuthActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

        btnSignIn.setOnClickListener(v -> {
            clearErrors();
            if (!validate()) return;

            //imitation of successful login
            startActivity(new Intent(requireContext(), MainActivity.class));
            requireActivity().finish();
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
