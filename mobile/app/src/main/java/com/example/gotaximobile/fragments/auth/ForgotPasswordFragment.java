package com.example.gotaximobile.fragments.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.activities.AuthActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordFragment extends Fragment {

    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;

    public ForgotPasswordFragment() {
        super(R.layout.fragment_forgot_password);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tilEmail = view.findViewById(R.id.tilEmail);
        etEmail = view.findViewById(R.id.etEmail);

        MaterialButton btnSend = view.findViewById(R.id.btnSendLink);
        MaterialButton btnBack = view.findViewById(R.id.btnBackToLogin);

        btnSend.setOnClickListener(v -> {
            tilEmail.setError(null);

            String email = getText(etEmail);
            if (TextUtils.isEmpty(email)) {
                tilEmail.setError("Email is required");
                etEmail.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.setError("Invalid email");
                etEmail.requestFocus();
                return;
            }

            Toast.makeText(requireContext(), "Reset link sent.", Toast.LENGTH_SHORT).show();
            ((AuthActivity) requireActivity()).openResetPassword();
        });

        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    private String getText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
