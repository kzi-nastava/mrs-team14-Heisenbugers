package com.example.gotaximobile.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.gotaximobile.fragments.profile.DriverProfile;
import com.example.gotaximobile.fragments.profile.ManagePassword;
import com.example.gotaximobile.fragments.profile.PersonalProfile;

public class ProfileTabAdapter extends FragmentStateAdapter {
    private final boolean driver;

    public ProfileTabAdapter(@NonNull Fragment fragment, boolean driver) {
        super(fragment);
        this.driver = driver;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(driver){
            if (position == 1) {
                return new DriverProfile();
            }else if (position == 2){
                return new ManagePassword();
            }
        }else{
            if (position == 1) {
                return new ManagePassword();
            }
        }
        return new PersonalProfile();
    }

    @Override
    public int getItemCount() {
        if(driver){
            return 3;
        }else{
            return 2;
        }

    }
}