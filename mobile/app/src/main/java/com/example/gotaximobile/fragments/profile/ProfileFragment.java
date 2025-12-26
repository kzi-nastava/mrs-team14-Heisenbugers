package com.example.gotaximobile.fragments.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gotaximobile.R;
import com.example.gotaximobile.adapters.ProfileTabAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class ProfileFragment extends Fragment {

    private boolean isDriver = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        MaterialToolbar topAppBar = view.findViewById(R.id.topAppBar);

        TabLayout tabLayout = view.findViewById(R.id.profileTabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.profileViewPager);

        topAppBar.setNavigationOnClickListener(v -> {
            EditPersonalProfile editProfileFragment = new EditPersonalProfile();
            EditVehicle editVehicleFragment = new EditVehicle();

            System.out.println(viewPager.getCurrentItem());

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, viewPager.getCurrentItem() == 1 ? editVehicleFragment: editProfileFragment)
                    .addToBackStack(null)
                    .commit();
        });

        if (isDriver) {
            tabLayout.setVisibility(View.VISIBLE);

            ProfileTabAdapter adapter = new ProfileTabAdapter(this);
            viewPager.setAdapter(adapter);

            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                if (position == 0) tab.setText("Personal");
                else if (position == 1) {
                    tab.setText("Driver");
                } else tab.setText("Manage Password");
            }).attach();
        }

        return view;
    }
}