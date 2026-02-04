package com.example.gotaximobile.fragments.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.gotaximobile.R;
import com.example.gotaximobile.activities.AuthActivity;
import com.example.gotaximobile.models.dtos.MessageResponse;
import com.example.gotaximobile.models.dtos.ResetPasswordRequestDTO;
import com.example.gotaximobile.network.AuthApi;
import com.example.gotaximobile.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordFragment extends Fragment {

    public static final String ARG_TOKEN = "arg_token";
    private TextInputLayout tilPassword, tilConfirm;
    private TextInputEditText etPassword, etConfirm;
    private AuthApi authApi;
    private String token;
    MaterialButton btnSave, btnBack;

    public ResetPasswordFragment() {
        super(R.layout.fragment_reset_password);
    }

    public static ResetPasswordFragment newInstance(String token) {
        ResetPasswordFragment f = new ResetPasswordFragment();
        Bundle b = new Bundle();
        b.putString(ARG_TOKEN, token);
        f.setArguments(b);
        return f;
    }


    @Override
    public void  onCreate(@NonNull Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        authApi = RetrofitClient.authApi(requireContext());

        if(getArguments() != null)
            token = getArguments().getString(ARG_TOKEN);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tilPassword = view.findViewById(R.id.tilPassword);
        tilConfirm = view.findViewById(R.id.tilConfirm);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirm = view.findViewById(R.id.etConfirm);

        btnSave = view.findViewById(R.id.btnSave);
        btnBack = view.findViewById(R.id.btnBackToLogin);

        btnSave.setOnClickListener(v -> {
            clearErrors();
            if (!validate()) return;

            if(TextUtils.isEmpty(token)){
                Toast.makeText(requireContext(),"Reset token is missing. Open reset link from email.",
                Toast.LENGTH_LONG).show();
                return;
            }

            String pass = getText(etPassword);
            String confirm = getText(etConfirm);

            setLoading(true);

            authApi.resetPassword(new ResetPasswordRequestDTO(token,pass,confirm))
                    .enqueue(new Callback<MessageResponse>() {
                        @Override
                        public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                            setLoading(false);

                            if(response.isSuccessful()){
                                Toast.makeText(requireContext(),
                                        "Password updated. You can login now.",
                                        Toast.LENGTH_LONG).show();

                                requireActivity().getSupportFragmentManager().popBackStack(null,
                                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                ((AuthActivity) requireActivity()).openLogin(false);
                            }
                            else{
                                Toast.makeText(requireContext(),
                                        "Reset failed: "+response.code(),
                                        Toast.LENGTH_LONG).show();

                            }
                        }

                        @Override
                        public void onFailure(Call<MessageResponse> call, Throwable throwable) {
                            setLoading(false);
                            Toast.makeText(requireContext(),
                                    "Network error: " + throwable.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();                        }
                    });
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
    private void setLoading(boolean loading){
        btnSave.setEnabled(!loading);
        btnBack.setEnabled(!loading);
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
