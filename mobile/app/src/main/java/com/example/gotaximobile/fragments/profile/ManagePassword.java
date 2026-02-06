package com.example.gotaximobile.fragments.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.ChangePasswordDTO;
import com.example.gotaximobile.models.dtos.GetProfileDTO;
import com.example.gotaximobile.network.RetrofitClient;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
            ChangePasswordDTO body = new ChangePasswordDTO(oldPass, newPass, confirmPass);
            sendRequest(body);
        }
    }

    private void sendRequest(ChangePasswordDTO body) {
        RetrofitClient.profileService(getContext()).changePassword(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Password Updated Successfully!", Toast.LENGTH_SHORT).show();
                    clearInputs();
                } else {
                    Log.e("API_ERROR", "Response failed: " + response.code());
                    Toast.makeText(getContext(), "Old password is incorrect!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("NETWORK_ERROR", Objects.requireNonNull(t.getMessage()));
                Toast.makeText(getContext(), "There was an error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearInputs(){
        tilOld.getEditText().setText("");
        tilNew.getEditText().setText("");
        tilConfirm.getEditText().setText("");
    }
}