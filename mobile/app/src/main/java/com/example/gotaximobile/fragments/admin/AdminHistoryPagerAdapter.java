package com.example.gotaximobile.fragments.admin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdminHistoryPagerAdapter extends FragmentStateAdapter {

    public AdminHistoryPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        boolean drivers = position == 0;
        return AdminUserPickFragment.newInstance(drivers);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
