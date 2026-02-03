package com.example.gotaximobile.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.gotaximobile.models.dtos.LoginResponseDTO;

public class TokenStorage {
    private static final String PREFS = "auth_prefs";
    private static final String KEY_TOKEN = "access_token";
    private static final String KEY_ROLE = "role";
    private static final String KEY_USER_ID = "user_id";

    private final SharedPreferences sp;

    public TokenStorage(Context ctx) {
        sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void save(LoginResponseDTO dto) {
        sp.edit()
                .putString(KEY_TOKEN, dto.accessToken)
                .putString(KEY_ROLE, dto.role)
                .putString(KEY_USER_ID, dto.userId)
                .apply();
    }

    public String getToken() { return sp.getString(KEY_TOKEN, null); }
    public String getRole() { return sp.getString(KEY_ROLE, null); }
    public String getUserId() { return sp.getString(KEY_USER_ID, null); }

    public void clear() { sp.edit().clear().apply(); }
}
