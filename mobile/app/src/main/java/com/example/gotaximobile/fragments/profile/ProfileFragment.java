package com.example.gotaximobile.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gotaximobile.R;
import com.example.gotaximobile.adapters.ProfileTabAdapter;
import com.example.gotaximobile.data.TokenStorage;
import com.example.gotaximobile.fragments.RideFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;


public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        TokenStorage storage = new TokenStorage(requireContext());
        boolean isDriver = Objects.equals(storage.getRole(), "DRIVER");

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        MaterialToolbar topAppBar = view.findViewById(R.id.topAppBar);

        TabLayout tabLayout = view.findViewById(R.id.profileTabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.profileViewPager);

        topAppBar.setNavigationOnClickListener(v -> {
            EditPersonalProfile editProfileFragment = new EditPersonalProfile();
            EditVehicle editVehicleFragment = new EditVehicle();

            Bundle bundle = new Bundle();
            bundle.putString("model", "Ford Fiesta");
            bundle.putString("type", "Standard");
            bundle.putString("plate", "NS-254-KL");
            bundle.putString("seats", "5");
            bundle.putBoolean("babies", true);
            bundle.putBoolean("pets", false);

            editVehicleFragment.setArguments(bundle);

            Bundle bundlePP = new Bundle();
            bundlePP.putString("name", "John Doe");
            bundlePP.putString("email", "johndoe@gmail.com");
            bundlePP.putString("address", "Bulevar Jovana Ducica 15, Novi Sad");
            bundlePP.putString("phone", "381645412147");

            editProfileFragment.setArguments(bundlePP);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, viewPager.getCurrentItem() == 1 ? editVehicleFragment : editProfileFragment)
                    .addToBackStack(null)
                    .commit();
        });

        ImageView icon = view.findViewById(R.id.history_button);
        icon.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RideFragment())
                    .addToBackStack(null)
                    .commit();
        });

        tabLayout.setVisibility(View.VISIBLE);
        if (isDriver) {
            ProfileTabAdapter adapter = new ProfileTabAdapter(this, isDriver);
            viewPager.setAdapter(adapter);

            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                if (position == 0) tab.setText("Personal");
                else if (position == 1) {
                    tab.setText("Driver");
                } else tab.setText("Manage Password");
            }).attach();
        }else{
            ProfileTabAdapter adapter = new ProfileTabAdapter(this, isDriver);
            viewPager.setAdapter(adapter);

            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                if (position == 0) tab.setText("Personal");
                else if (position == 1) {
                    tab.setText("Manage Password");
                }
            }).attach();
        }

        return view;
    }
}