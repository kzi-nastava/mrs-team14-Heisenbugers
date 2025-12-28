package com.example.gotaximobile.fragments.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.gotaximobile.R;
import com.google.android.material.textfield.TextInputLayout;

public class ManagePassword extends Fragment {

    private TextInputLayout tilOld, tilNew, tilConfirm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_password, container, false);

        tilOld = view.findViewById(R.id.til_old_password);
        tilNew = view.findViewById(R.id.til_new_password);
        tilConfirm = view.findViewById(R.id.til_confirm_password);
        Button btnUpdate = view.findViewById(R.id.btn_update_password);

        btnUpdate.setOnClickListener(v -> validateAndSubmit());

        return view;
    }

    private void validateAndSubmit() {
        String oldPass = tilOld.getEditText().getText().toString().trim();
        String newPass = tilNew.getEditText().getText().toString().trim();
        String confirmPass = tilConfirm.getEditText().getText().toString().trim();

        boolean isValid = true;

        if (oldPass.isEmpty()) {
            tilOld.setError("Required");
            isValid = false;
        } else tilOld.setError(null);

        if (newPass.isEmpty()) {
            tilNew.setError("Required");
            isValid = false;
        } else if (newPass.length() < 6) {
            tilNew.setError("Password must be at least 6 characters");
            isValid = false;
        } else tilNew.setError(null);

        if (confirmPass.isEmpty()) {
            tilConfirm.setError("Required");
            isValid = false;
        } else if (!confirmPass.equals(newPass)) {
            tilConfirm.setError("Passwords do not match");
            isValid = false;
        } else tilConfirm.setError(null);

        if (isValid) {
            Toast.makeText(getContext(), "Password Updated Successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}