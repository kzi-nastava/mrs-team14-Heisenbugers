package com.example.gotaximobile.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.gotaximobile.models.dtos.LoginResponseDTO;

public class TokenStorage {
    private static final String PREFS = "auth_prefs";

    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_ROLE = "role";
   // private static final String KEY_USER_ID = "user_id";

    private final SharedPreferences sp;

    public TokenStorage(Context ctx) {

        sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void save(LoginResponseDTO dto) {
        if (dto == null) return;
        sp.edit()
                .putString(KEY_ACCESS_TOKEN, dto.getAccessToken())
                .putString(KEY_ROLE, dto.getRole()).apply();
                //.putString(KEY_USER_ID, dto.userId)

    }

    public String getAccessToken() {
        return sp.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRole() {
        return sp.getString(KEY_ROLE, null);
    }

    public boolean isLoggedIn() {
        String token = getAccessToken();
        return token != null && !token.isEmpty();
    }

    public String getAuthHeaderValue() {
        String token = getAccessToken();
        if (token == null || token.isEmpty()) return null;
        return "Bearer " + token;
    }
    public void clear() { sp.edit().clear().apply(); }
}
