package com.example.gotaximobile.fragments.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gotaximobile.R;
import com.example.gotaximobile.adapters.ProfileTabAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class ProfileFragment extends Fragment {

    private boolean isDriver = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TabLayout tabLayout = view.findViewById(R.id.profileTabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.profileViewPager);

        if (isDriver) {
            tabLayout.setVisibility(View.VISIBLE);

            ProfileTabAdapter adapter = new ProfileTabAdapter(this);
            viewPager.setAdapter(adapter);

            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                if (position == 0) tab.setText("Personal");
                else tab.setText("Driver");
            }).attach();
        }

        return view;
    }
}