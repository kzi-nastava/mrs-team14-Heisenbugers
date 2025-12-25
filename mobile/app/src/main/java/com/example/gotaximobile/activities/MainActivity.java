package com.example.gotaximobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.activities.auth.LoginActivity;
import com.example.gotaximobile.fragments.FavoriteRoutesFragment;
import com.example.gotaximobile.fragments.HomeFragment;
import com.example.gotaximobile.fragments.ProfileFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MaterialToolbar topAppBar = findViewById(R.id.top_app_bar);
        if (topAppBar != null) {
            MenuItem loginItem = topAppBar.getMenu().findItem(R.id.action_login);
            if (loginItem != null) {
                SpannableString s = new SpannableString(loginItem.getTitle());
                s.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(this, R.color.lime)),
                        0, s.length(),
                        0
                );
                loginItem.setTitle(s);
            }

            topAppBar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_login) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    return true;
                }
                return false;
            });
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_favorite) {
                selectedFragment = new FavoriteRoutesFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }


            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
