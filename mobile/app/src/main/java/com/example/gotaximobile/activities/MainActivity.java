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
import com.example.gotaximobile.data.TokenStorage;
import com.example.gotaximobile.fragments.AdminPanelFragment;
import com.example.gotaximobile.fragments.HomeFragment;
import com.example.gotaximobile.fragments.profile.ProfileFragment;
import com.example.gotaximobile.fragments.ride.DuringRideFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar topAppBar;
    private TokenStorage tokenStorage;

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        topAppBar = findViewById(R.id.top_app_bar);
        tokenStorage = new TokenStorage(getApplicationContext());

        if (topAppBar != null) {
            topAppBar.getMenu().clear();
            topAppBar.inflateMenu(R.menu.top_app_bar_menu);

            updateAuthMenuItems();


            topAppBar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_login) {
                    startActivity(new Intent(MainActivity.this, AuthActivity.class));
                    return true;
                }
                if (item.getItemId() == R.id.action_logout) {
                    handleLogout();
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

        bottomNav = findViewById(R.id.bottom_navigation);

        checkIsAdmin();

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_favorite) {
                Bundle args = new Bundle();
                args.putString("rideId", "5adcfe6c-b3bd-4f7c-a098-0d8bb0473da3");
                selectedFragment = new DuringRideFragment();
                selectedFragment.setArguments(args);
            } else if (id == R.id.nav_admin_panel) {
                selectedFragment = new AdminPanelFragment();
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

    private void updateAuthMenuItems() {
        if (topAppBar == null) return;

        boolean loggedIn = tokenStorage.isLoggedIn();

        MenuItem loginItem = topAppBar.getMenu().findItem(R.id.action_login);
        MenuItem logoutItem = topAppBar.getMenu().findItem(R.id.action_logout);

        if (loginItem != null) {
            loginItem.setVisible(!loggedIn);
            if (!loggedIn) {
                tintMenuItemText(topAppBar, R.id.action_login, R.color.app_primary);
            }
        }

        if (logoutItem != null) {
            logoutItem.setVisible(loggedIn);
            if (loggedIn) {
                tintMenuItemText(topAppBar, R.id.action_logout, R.color.app_primary);
            }
        }
    }

    private void handleLogout() {
        tokenStorage.clear();
        checkIsAdmin();
        updateAuthMenuItems();
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        } else {
            loadFragment(new HomeFragment());
            updateTopBarVisibility(R.id.nav_home);
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        updateAuthMenuItems();
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void checkIsAdmin() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        boolean isAdmin = Objects.equals(tokenStorage.getRole(), "ADMIN");

        MenuItem adminItem = bottomNav.getMenu().findItem(R.id.nav_admin_panel);
        if (adminItem != null) {
            adminItem.setVisible(isAdmin);
        }
    }
}
