package com.example.gotaximobile.activities;


import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.data.TokenStorage;
import com.example.gotaximobile.fragments.AdminPanelFragment;
import com.example.gotaximobile.fragments.FavoriteRoutesFragment;
import com.example.gotaximobile.fragments.HomeFragment;

import com.example.gotaximobile.fragments.admin.AdminPriceFragment;

import com.example.gotaximobile.fragments.admin.AdminAllRidesFragment;

import com.example.gotaximobile.fragments.profile.ProfileFragment;
import com.example.gotaximobile.fragments.ride.DuringRideFragment;
import com.example.gotaximobile.models.dtos.UserStateDTO;
import com.example.gotaximobile.models.enums.UserState;
import com.example.gotaximobile.network.RetrofitClient;
import com.example.gotaximobile.network.UserService;
import com.example.gotaximobile.services.NotificationService;
import com.example.gotaximobile.utils.NotificationUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar topAppBar;
    private TokenStorage tokenStorage;
    private UserService userService;
    private UserStateDTO userState;

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        tokenStorage = new TokenStorage(getApplicationContext());

        NotificationUtils.createChannel(this);

        String headerValue = tokenStorage.getAuthHeaderValue();

        NotificationService service = new NotificationService(this, headerValue);
        service.connect();

        userService = RetrofitClient.userService(this);

        setContentView(R.layout.activity_main);

        topAppBar = findViewById(R.id.top_app_bar);

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
            decideOnHomeFragment(fragment -> {
                loadFragment(fragment);
                updateTopBarVisibility(R.id.nav_home);
            });
        }

        bottomNav = findViewById(R.id.bottom_navigation);

        checkIsAdmin();

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                decideOnHomeFragment(fragment -> {
                    loadFragment(fragment);
                    updateTopBarVisibility(id);
                });
                return true;
            } else {
                if (id == R.id.nav_favorite) {
                    selectedFragment = new FavoriteRoutesFragment();
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
            }
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


    private void decideOnHomeFragment(FragmentCallback callback) {
        userService.getUserState().enqueue(new Callback<UserStateDTO>() {
            @Override
            public void onResponse(@NonNull Call<UserStateDTO> call,
                                   @NonNull Response<UserStateDTO> response) {
                UserStateDTO userState = response.body();
                Fragment fragment;
                if (userState != null) {
                    if (userState.state == UserState.RIDING) {
                        Bundle args = new Bundle();
                        args.putString("rideId", userState.rideId.toString());
                        fragment = new DuringRideFragment();
                        fragment.setArguments(args);// ride ongoing
                    } else {
                        fragment = new HomeFragment();
                    }

                } else {
                    fragment = new HomeFragment();
                }
                callback.onFragmentReady(fragment);

            }

            @Override
            public void onFailure(@NonNull Call<UserStateDTO> call, @NonNull Throwable t) {
                Log.e("NETWORK_ERROR", Objects.requireNonNull(t.getMessage()));
                callback.onFragmentReady(new HomeFragment()); // fallback
            }
        });
    }

    public void navigateToHome() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }

        decideOnHomeFragment(fragment -> {
            loadFragment(fragment);
            updateTopBarVisibility(R.id.nav_home);
        });
    }

    interface FragmentCallback {
        void onFragmentReady(Fragment fragment);
    }
}
