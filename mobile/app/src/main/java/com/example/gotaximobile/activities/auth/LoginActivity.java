package com.example.gotaximobile.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gotaximobile.R;
import com.example.gotaximobile.activities.MainActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {


    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_login);



        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        MaterialButton btnForgotPassword = findViewById(R.id.btnForgotPassword);
        MaterialButton btnGoToRegister = findViewById(R.id.btnGoToRegister);

        MaterialButton btnSignIn = findViewById(R.id.btnSignIn);

        MaterialToolbar topAppBar = findViewById(R.id.top_app_bar);
        if (topAppBar != null) {
            topAppBar.setNavigationOnClickListener(v -> finish());
            topAppBar.getMenu().clear();
        }


        btnSignIn.setOnClickListener(v -> {
            clearErrors();
            if (!validate()) return;


            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });

        btnForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class))
        );

        btnGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
    }


    private void clearErrors() {
        tilEmail.setError(null);
        tilPassword.setError(null);
    }

    private boolean validate() {
        String email = getText(etEmail);
        String pass = getText(etPassword);

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Invalid email");
            etEmail.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(pass)) {
            tilPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private String getText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }




}