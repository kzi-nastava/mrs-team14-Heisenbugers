package com.example.gotaximobile.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.fragments.auth.CheckEmailFragment;
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

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBack();
            }
        });

        topAppBar.setNavigationOnClickListener(v -> handleBack());

        /*if (savedInstanceState == null) {
            openLogin(false);
        }*/
        handleDeepLink(getIntent(),savedInstanceState);

        getSupportFragmentManager().addOnBackStackChangedListener(this::updateBackArrow);
        updateBackArrow();


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleDeepLink(intent,null);
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
    public void openCheckEmail() {
        replaceFragment(new CheckEmailFragment(), true, "check_email");
    }

    /*public void openResetPassword(String token) {
        replaceFragment(new ResetPasswordFragment.newInstance(token), true, "reset");
    }*/
    public void openResetPassword(String token) {
        ResetPasswordFragment f = new ResetPasswordFragment();
        Bundle b = new Bundle();
        b.putString(ResetPasswordFragment.ARG_TOKEN, token);
        f.setArguments(b);
        replaceFragment(f, true, "reset");
    }

    private void replaceFragment(Fragment fragment, boolean addToBackStack, String tag) {
        var tx = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.auth_fragment_container, fragment, tag);

        if (addToBackStack) tx.addToBackStack(tag);
        tx.commit();
    }

    private void handleDeepLink(Intent intent, Bundle savedInstanceState) {
        Uri data = intent.getData();
        android.util.Log.d("AuthActivity", "handleDeepLink data = " + data);

        if (data != null) {
            String token = data.getQueryParameter("token");
            android.util.Log.d("AuthActivity", "token from URI = " + token);

            if (token != null && !token.isEmpty()) {
                openResetPassword(token);
                return;
            }
        }

        if (savedInstanceState == null) {
            openLogin(false);
        }
    }

    private void handleBack() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.auth_fragment_container);
        boolean isLogin = current instanceof LoginFragment;

        if (isLogin) {
            finish();
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    private void updateBackArrow() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.auth_fragment_container);
        boolean isLogin = current instanceof LoginFragment;

        topAppBar.setNavigationIcon(R.drawable.ic_arrow_back);
        topAppBar.setNavigationContentDescription("Back");

         topAppBar.setNavigationOnClickListener(v -> handleBack());
    }

    // will delete?
    public void showExitToMainArrow() {
        topAppBar.setNavigationIcon(R.drawable.ic_arrow_back);
        topAppBar.setNavigationContentDescription("Back");
        topAppBar.setNavigationOnClickListener(v -> finish());
    }

    public void restoreDefaultArrowBehavior() {
        topAppBar.setNavigationOnClickListener(v -> handleBack());
        updateBackArrow();
    }
}
