package com.example.gotaximobile.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gotaximobile.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_forgot_password);

        MaterialToolbar toolbar = findViewById(R.id.top_app_bar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
            toolbar.getMenu().clear();
        }

        tilEmail = findViewById(R.id.tilEmail);
        etEmail = findViewById(R.id.etEmail);

        MaterialButton btnSendLink = findViewById(R.id.btnSendLink);
        MaterialButton btnBackToLogin = findViewById(R.id.btnBackToLogin);

        btnSendLink.setOnClickListener(v -> {
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

                    Toast.makeText(this, "Reset link sent (UI). Check your email.", Toast.LENGTH_LONG).show();
                });

        btnBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

    }

    private String getText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}