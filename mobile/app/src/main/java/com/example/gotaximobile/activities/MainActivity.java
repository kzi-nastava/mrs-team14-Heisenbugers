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
import com.example.gotaximobile.fragments.FavoriteRoutesFragment;
import com.example.gotaximobile.fragments.HomeFragment;
import com.example.gotaximobile.fragments.profile.ProfileFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        topAppBar = findViewById(R.id.top_app_bar);

        if (topAppBar != null) {
            topAppBar.getMenu().clear();
            topAppBar.inflateMenu(R.menu.top_app_bar_menu);
            tintMenuItemText(topAppBar, R.id.action_login, R.color.app_primary);

            topAppBar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_login) {
                    startActivity(new Intent(MainActivity.this, AuthActivity.class));
                    return true;
                }
                return false;
            });
        }




        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());

            updateTopBarVisibility(R.id.nav_home);
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
                updateTopBarVisibility(id);
                return true;
            }
            return false;
        });
    }

    private void updateTopBarVisibility(int selectedNavId) {
        if (topAppBar == null) return;

        if (selectedNavId == R.id.nav_home) {
            topAppBar.setVisibility(android.view.View.VISIBLE);
        } else {
            topAppBar.setVisibility(android.view.View.GONE);
        }
    }
    private void tintMenuItemText(MaterialToolbar toolbar, int menuItemId, int colorRes) {
        MenuItem item = toolbar.getMenu().findItem(menuItemId);
        if (item == null) return;

        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(this, colorRes)),
                0, s.length(),
                SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        );
        item.setTitle(s);
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
