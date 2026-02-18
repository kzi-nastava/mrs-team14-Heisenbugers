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
import com.example.gotaximobile.fragments.driverProfileRequests.DriverRequestsFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminPanelFragment extends Fragment {

    private boolean isFabExpanded = false;
    private FloatingActionButton fabMain;
    private ExtendedFloatingActionButton fabRequests, fabRegister;

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

        fabMain.setOnClickListener(v -> toggleFab());

        fabRequests.setOnClickListener(v -> {
            DriverRequestsFragment requestsFragment = new DriverRequestsFragment();

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, requestsFragment)
                    .addToBackStack(null)
                    .commit();

            toggleFab();
        });

        fabRegister.setOnClickListener(v -> {
            // Navigate to Registration Fragment
            Toast.makeText(getContext(), "Opening Registration...", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void toggleFab() {
        if (!isFabExpanded) {
            fabRequests.show();
            fabRegister.show();
            fabMain.setImageResource(R.drawable.ic_close);
        } else {
            fabRequests.hide();
            fabRegister.hide();
            fabMain.setImageResource(R.drawable.ic_edit);
        }
        isFabExpanded = !isFabExpanded;
    }
}