package com.example.gotaximobile.fragments.auth;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.gotaximobile.R;
import com.example.gotaximobile.activities.AuthActivity;
import com.google.android.material.button.MaterialButton;

public class CheckEmailFragment extends Fragment {

    public CheckEmailFragment() {
        super(R.layout.fragment_check_email);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MaterialButton btnBack = view.findViewById(R.id.btnBackToLogin);

        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack(null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ((AuthActivity) requireActivity()).openLogin(false);
        });
    }
}
