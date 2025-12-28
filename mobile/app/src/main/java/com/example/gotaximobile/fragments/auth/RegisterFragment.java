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

public class RegisterFragment extends Fragment {

    private TextInputLayout tilName, tilSurname, tilAddress, tilPhone, tilEmail, tilPassword, tilConfirm;
    private TextInputEditText etName, etSurname, etAddress, etPhone, etEmail, etPassword, etConfirm;

    public RegisterFragment() {
        super(R.layout.fragment_register);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindViews(view);

        MaterialButton btnAddPhoto = view.findViewById(R.id.btnAddPhoto);
        MaterialButton btnCreate = view.findViewById(R.id.btnCreateAccount);
        MaterialButton btnGoToLogin = view.findViewById(R.id.btnGoToLogin);

        btnAddPhoto.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Photo upload is optional.", Toast.LENGTH_SHORT).show()
        );

        btnCreate.setOnClickListener(v -> {
            clearErrors();
            if (!validate()) {
                Toast.makeText(requireContext(), "Please fix the highlighted fields.", Toast.LENGTH_SHORT).show();
                return;
            }


            Toast.makeText(requireContext(),
                    "Registered. Now activate via email link.",
                    Toast.LENGTH_LONG).show();

        });

        btnGoToLogin.setOnClickListener(v -> goToLogin());
    }

    private void goToLogin() {
        requireActivity().getSupportFragmentManager().popBackStack(null,
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ((AuthActivity) requireActivity()).openLogin(false);
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
