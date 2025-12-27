package com.example.gotaximobile.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gotaximobile.R;
import com.example.gotaximobile.fragments.auth.ForgotPasswordFragment;
import com.example.gotaximobile.fragments.auth.LoginFragment;
import com.example.gotaximobile.fragments.auth.RegisterFragment;
import com.example.gotaximobile.fragments.auth.ResetPasswordFragment;
import com.google.android.material.appbar.MaterialToolbar;

public class AuthActivity extends AppCompatActivity {

    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        topAppBar = findViewById(R.id.top_app_bar);
        topAppBar.setNavigationOnClickListener(v -> onBackPressed());

        if (savedInstanceState == null) {
            openLogin(false);
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this::updateBackArrow);
        updateBackArrow();
    }

    public void openLogin(boolean addToBackStack) {
        replaceFragment(new LoginFragment(), addToBackStack, "login");
    }

    public void openRegister() {
        replaceFragment(new RegisterFragment(), true, "register");
    }

    public void openForgotPassword() {
        replaceFragment(new ForgotPasswordFragment(), true, "forgot");
    }

    public void openResetPassword() {
        replaceFragment(new ResetPasswordFragment(), true, "reset");
    }

    private void replaceFragment(androidx.fragment.app.Fragment fragment,
                                 boolean addToBackStack,
                                 String tag) {

        androidx.fragment.app.FragmentTransaction tx =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.auth_fragment_container, fragment, tag);

        if (addToBackStack) tx.addToBackStack(tag);
        tx.commit();
    }

    private void updateBackArrow() {
        androidx.fragment.app.Fragment current =
                getSupportFragmentManager().findFragmentById(R.id.auth_fragment_container);

        boolean isLogin = current instanceof com.example.gotaximobile.fragments.auth.LoginFragment;

        topAppBar.setNavigationIcon(R.drawable.ic_arrow_back);
        topAppBar.setNavigationContentDescription("Back");

        if (isLogin) {
            topAppBar.setNavigationOnClickListener(v -> finish());
        } else {
            topAppBar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }


    public void showExitToMainArrow() {
        topAppBar.setNavigationIcon(R.drawable.ic_arrow_back);
        topAppBar.setNavigationContentDescription("Back");
        topAppBar.setNavigationOnClickListener(v -> finish()); // вернёт на MainActivity
    }

    public void restoreDefaultArrowBehavior() {
        topAppBar.setNavigationOnClickListener(v -> onBackPressed());
        updateBackArrow();
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
