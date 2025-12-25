package com.example.gotaximobile.activities.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gotaximobile.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilSurname, tilAddress, tilPhone, tilEmail, tilPassword, tilConfirm;
    private TextInputEditText etName, etSurname, etAddress, etPhone, etEmail, etPassword, etConfirm;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_register);

        bindViews();

        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/


        MaterialButton btnCreate = findViewById(R.id.btnCreateAccount);
        TextView btnGoToLogin = findViewById(R.id.btnGoToLogin);
        MaterialButton btnAddPhoto = findViewById(R.id.btnAddPhoto);

        MaterialToolbar toolbar = findViewById(R.id.top_app_bar);
        toolbar.setNavigationOnClickListener(v -> finish());

        //Photo upload
        btnAddPhoto.setOnClickListener(v ->
                Toast.makeText(this, "Photo upload is optional.", Toast.LENGTH_SHORT).show()
        );

        //Register button
        btnCreate.setOnClickListener(v -> {
            clearErrors();
            if (!validate()) return;
            //simulate successful registration
            Toast.makeText(
                    this,
                    "Registration completed successfully. Please check your email to activate the account.",
                    Toast.LENGTH_LONG
            ).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }


    private void bindViews() {
        tilName = findViewById(R.id.tilName);
        tilSurname = findViewById(R.id.tilSurname);
        tilAddress = findViewById(R.id.tilAddress);
        tilPhone = findViewById(R.id.tilPhone);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirm = findViewById(R.id.tilConfirm);

        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirm);
    }

    private void clearErrors() {
        tilName.setError(null);
        tilSurname.setError(null);
        tilAddress.setError(null);
        tilPhone.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirm.setError(null);
    }


    private boolean validate() {
        boolean ok = true;

        String name = getText(etName);
        String surname = getText(etSurname);
        String address = getText(etAddress);
        String phone = getText(etPhone);
        String email = getText(etEmail);
        String pass = getText(etPassword);
        String confirm = getText(etConfirm);

        if (TextUtils.isEmpty(name)) { tilName.setError("Required"); ok = false; }
        if (TextUtils.isEmpty(surname)) { tilSurname.setError("Required"); ok = false; }
        if (TextUtils.isEmpty(address)) { tilAddress.setError("Required"); ok = false; }
        if (TextUtils.isEmpty(phone)) { tilPhone.setError("Required"); ok = false; }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Required");
            ok = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Invalid email");
            ok = false;
        }

        if (TextUtils.isEmpty(pass)) {
            tilPassword.setError("Required");
            ok = false;
        } else if (pass.length() < 6) {
            tilPassword.setError("Min 6 characters");
            ok = false;
        }

        if (TextUtils.isEmpty(confirm)) {
            tilConfirm.setError("Required");
            ok = false;
        } else if (!confirm.equals(pass)) {
            tilConfirm.setError("Passwords do not match");
            ok = false;
        }return ok;
    }

    private String getText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}