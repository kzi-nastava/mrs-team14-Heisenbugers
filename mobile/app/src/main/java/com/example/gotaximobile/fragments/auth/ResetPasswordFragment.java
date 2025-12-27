package com.example.gotaximobile.fragments.auth;

import android.os.Bundle;
import android.text.TextUtils;
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

public class ResetPasswordFragment extends Fragment {

    private TextInputLayout tilPassword, tilConfirm;
    private TextInputEditText etPassword, etConfirm;

    public ResetPasswordFragment() {
        super(R.layout.fragment_reset_password);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tilPassword = view.findViewById(R.id.tilPassword);
        tilConfirm = view.findViewById(R.id.tilConfirm);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirm = view.findViewById(R.id.etConfirm);

        MaterialButton btnSave = view.findViewById(R.id.btnSave);
        MaterialButton btnBack = view.findViewById(R.id.btnBackToLogin);

        btnSave.setOnClickListener(v -> {
            clearErrors();
            if (!validate()) return;

            Toast.makeText(requireContext(), "Password changed (UI). You can login now.", Toast.LENGTH_LONG).show();

            requireActivity().getSupportFragmentManager().popBackStack(null,
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ((AuthActivity) requireActivity()).openLogin(false);
        });

        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack(null,
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ((AuthActivity) requireActivity()).openLogin(false);
        });
    }

    private void clearErrors() {
        tilPassword.setError(null);
        tilConfirm.setError(null);
    }

    private boolean validate() {
        String pass = getText(etPassword);
        String confirm = getText(etConfirm);

        if (TextUtils.isEmpty(pass)) {
            tilPassword.setError("Required");
            etPassword.requestFocus();
            return false;
        }
        if (pass.length() < 6) {
            tilPassword.setError("Min 6 characters");
            etPassword.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(confirm)) {
            tilConfirm.setError("Required");
            etConfirm.requestFocus();
            return false;
        }
        if (!confirm.equals(pass)) {
            tilConfirm.setError("Passwords do not match");
            etConfirm.requestFocus();
            return false;
        }
        return true;
    }

    private String getText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
