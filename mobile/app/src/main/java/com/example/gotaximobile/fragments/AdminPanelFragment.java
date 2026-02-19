package com.example.gotaximobile.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gotaximobile.R;
import com.example.gotaximobile.fragments.admin.AdminAllRidesFragment;
import com.example.gotaximobile.fragments.admin.AdminRideHistoryHubFragment;
import com.example.gotaximobile.fragments.admin.ManageUsersFragment;
import com.example.gotaximobile.fragments.auth.DriverRegistrationFragment;
import com.example.gotaximobile.fragments.driverProfileRequests.DriverRequestsFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminPanelFragment extends Fragment {

    private boolean isFabExpanded = false;
    private FloatingActionButton fabMain;
    private ExtendedFloatingActionButton fabRequests, fabRegister, fabRideHistory, fabManageUsers, fabPanicDashboard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_panel, container, false);

        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.admin_fragment_container, new AdminAllRidesFragment())
                    .commit();
        }

        fabMain = view.findViewById(R.id.fab_main);
        fabRequests = view.findViewById(R.id.fab_requests);
        fabRegister = view.findViewById(R.id.fab_register);
        fabRideHistory = view.findViewById(R.id.fab_ride_history);
        fabManageUsers = view.findViewById(R.id.fab_manageUsers);

        fabPanicDashboard = view.findViewById(R.id.fab_panic_dashboard);

        fabRequests.hide();
        fabRegister.hide();
        fabRideHistory.hide();
        fabManageUsers.hide();
        fabMain.setImageResource(R.drawable.ic_edit);
        isFabExpanded = false;

        fabMain.setOnClickListener(v -> toggleFab());

        fabRideHistory.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminRideHistoryHubFragment())
                    .addToBackStack(null)
                    .commit();
            toggleFab();
        });

        fabRequests.setOnClickListener(v -> {
            DriverRequestsFragment requestsFragment = new DriverRequestsFragment();

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, requestsFragment)
                    .addToBackStack(null)
                    .commit();

            toggleFab();
        });

        fabRegister.setOnClickListener(v -> {
            DriverRegistrationFragment driverRegistrationFragment = new DriverRegistrationFragment();

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, driverRegistrationFragment)
                    .addToBackStack(null)
                    .commit();

            toggleFab();
        });

        fabManageUsers.setOnClickListener(v -> {
            ManageUsersFragment manageUsersFragment = new ManageUsersFragment();

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, manageUsersFragment)
                    .addToBackStack(null)
                    .commit();
            toggleFab();
        });

        fabPanicDashboard.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new com.example.gotaximobile.fragments.admin.AdminPanicDashboardFragment())
                    .addToBackStack(null)
                    .commit();
            toggleFab();
        });

        return view;
    }

    private void toggleFab() {
        if (!isFabExpanded) {
            fabRequests.show();
            fabRegister.show();
            fabRideHistory.show();
            fabManageUsers.show();
            fabPanicDashboard.show();
            fabMain.setImageResource(R.drawable.ic_close);
        } else {
            fabRequests.hide();
            fabRegister.hide();
            fabRideHistory.hide();
            fabManageUsers.hide();
            fabPanicDashboard.hide();
            fabMain.setImageResource(R.drawable.ic_edit);
        }
        isFabExpanded = !isFabExpanded;
    }
}