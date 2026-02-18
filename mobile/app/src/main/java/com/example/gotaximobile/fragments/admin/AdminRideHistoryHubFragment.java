package com.example.gotaximobile.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gotaximobile.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdminRideHistoryHubFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_history_hub, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ViewPager2 pager = view.findViewById(R.id.pager);
        TabLayout tabs = view.findViewById(R.id.tabs);

        pager.setAdapter(new AdminHistoryPagerAdapter(this));

        new TabLayoutMediator(tabs, pager, (tab, position) -> {
            tab.setText(position == 0 ? "Drivers" : "Passengers");
        }).attach();
    }
}
