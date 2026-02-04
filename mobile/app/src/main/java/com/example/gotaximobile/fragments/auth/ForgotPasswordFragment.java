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
import com.example.gotaximobile.models.dtos.ForgotPasswordRequestDTO;
import com.example.gotaximobile.models.dtos.ForgotPasswordResponseDTO;
import com.example.gotaximobile.models.dtos.MessageResponse;
import com.example.gotaximobile.network.AuthApi;
import com.example.gotaximobile.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordFragment extends Fragment {

    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;

    private MaterialButton btnSend, btnBack;
    private AuthApi authApi;

    public ForgotPasswordFragment() {
        super(R.layout.fragment_forgot_password);
    }

    @Override
    public void onCreate(@NonNull Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        authApi = RetrofitClient.authApi(requireContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tilEmail = view.findViewById(R.id.tilEmail);
        etEmail = view.findViewById(R.id.etEmail);

        btnSend = view.findViewById(R.id.btnSendLink);
        btnBack = view.findViewById(R.id.btnBackToLogin);

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

            setLoading(true);

            authApi.forgotPassword(new ForgotPasswordRequestDTO(email)).
                    enqueue(new Callback<MessageResponse>(){
                                @Override
                                public void onResponse(Call<MessageResponse> call,
                                                       Response<MessageResponse> response) {
                                    setLoading(false);

                                    if (!response.isSuccessful()) {
                                        Toast.makeText(requireContext(),
                                                "Request failed: " + response.code(),
                                                Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    MessageResponse body = response.body();
                                    String message = (body != null && body.getMessage() != null)
                                            ? body.getMessage() :
                                            "If the email exists, a reset link has been sent.";
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                                    //((AuthActivity) requireActivity()).openLogin(true);
                                    ((AuthActivity) requireActivity()).openCheckEmail();
                                }


                                @Override
                                public void onFailure(Call<MessageResponse> call, Throwable throwable){
                                    setLoading(false);
                                    Toast.makeText(requireContext(),
                                            "Network error: " + throwable.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                                });

        });

        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void setLoading(boolean loading){
        btnSend.setEnabled(!loading);
        btnBack.setEnabled(!loading);
    }

    private String getText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
