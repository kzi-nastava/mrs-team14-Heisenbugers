package com.example.gotaximobile.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gotaximobile.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputLayout tilPassword, tilConfirm;
    private TextInputEditText etPassword, etConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_reset_password);

        MaterialToolbar toolbar = findViewById(R.id.top_app_bar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
            toolbar.getMenu().clear();
        }

        tilPassword = findViewById(R.id.tilPassword);
        tilConfirm = findViewById(R.id.tilConfirm);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirm);

        MaterialButton btnSave = findViewById(R.id.btnSave);
        MaterialButton btnBackToLogin = findViewById(R.id.btnBackToLogin);

        btnSave.setOnClickListener(v -> {
            clearErrors();
            if (!validate()) return;


            Toast.makeText(this, "Password changed. You can login now.", Toast.LENGTH_LONG).show();

            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });


        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
    }

    private void clearErrors() {
        tilPassword.setError(null);
        tilConfirm.setError(null);
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