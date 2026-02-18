package com.example.gotaximobile.fragments.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.gotaximobile.BuildConfig;
import com.example.gotaximobile.R;
import com.example.gotaximobile.activities.AuthActivity;
import com.example.gotaximobile.activities.MainActivity;
import com.example.gotaximobile.adapters.ProfileTabAdapter;
import com.example.gotaximobile.data.TokenStorage;
import com.example.gotaximobile.fragments.RideFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;


public class ProfileFragment extends Fragment {

    private Bundle latestProfileData;
    private Bundle latestVehicleData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        TokenStorage storage = new TokenStorage(requireContext());
        boolean isDriver = Objects.equals(storage.getRole(), "DRIVER");

        boolean isUserLogged = storage.isLoggedIn();

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        MaterialToolbar topAppBar = view.findViewById(R.id.topAppBar);

        TabLayout tabLayout = view.findViewById(R.id.profileTabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.profileViewPager);

        View loggedOutLayout = view.findViewById(R.id.loggedOutLayout);

        if(isUserLogged){
            loggedOutLayout.setVisibility(View.GONE);
            getChildFragmentManager().setFragmentResultListener("profileKey", getViewLifecycleOwner(), (requestKey, bundle) -> {
                this.latestProfileData = bundle;
            });

            getChildFragmentManager().setFragmentResultListener("vehicleInfo", getViewLifecycleOwner(), (requestKey, bundle) -> {
                this.latestVehicleData = bundle;
            });

            topAppBar.setNavigationOnClickListener(v -> {
                EditPersonalProfile editProfileFragment = new EditPersonalProfile();
                EditVehicle editVehicleFragment = new EditVehicle();

                if (latestVehicleData != null) {
                    editVehicleFragment.setArguments(latestVehicleData);
                }

                if (latestProfileData != null) {
                    editProfileFragment.setArguments(latestProfileData);
                }

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, viewPager.getCurrentItem() == 1 ? editVehicleFragment : editProfileFragment)
                        .addToBackStack(null)
                        .commit();
            });

            ImageView icon = view.findViewById(R.id.history_button);
            icon.setOnClickListener(v -> {

                TokenStorage storage2 = new TokenStorage(requireContext());
                String role = storage2.getRole();
                Fragment next;

                if ("PASSENGER".equals(role)) {
                    next = new com.example.gotaximobile.fragments.PassengerRideHistoryFragment();
                } else if ("DRIVER".equals(role)) {
                    next = new com.example.gotaximobile.fragments.RideFragment();
                } else {
                    // ADMIN
                    next = new com.example.gotaximobile.fragments.PassengerRideHistoryFragment();

                   // Toast.makeText(requireContext(), "History is not available for this role", Toast.LENGTH_SHORT).show();
                    return;
                }

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, next)
                        .addToBackStack(null)
                        .commit();

                /*
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new RideFragment())
                        .addToBackStack(null)
                        .commit();*/
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
        }else{
            loggedOutLayout.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            topAppBar.getMenu().clear();

            view.findViewById(R.id.btnLoginRedirect).setOnClickListener(v -> {
                startActivity(new Intent(requireContext(), AuthActivity.class));
            });
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ShapeableImageView profileImage = view.findViewById(R.id.profile_image);
        TokenStorage storage = new TokenStorage(requireContext());

        if (storage.isLoggedIn()) {
            getChildFragmentManager().setFragmentResultListener("profileKey", getViewLifecycleOwner(), (requestKey, bundle) -> {
                this.latestProfileData = bundle;

                String photoUrl = bundle.getString("profilePhoto");

                if (photoUrl != null) {
                    String fixedUrl = photoUrl.replace("http://localhost:8081/", BuildConfig.BASE_URL);

                    Glide.with(this)
                            .load(fixedUrl)
                            .placeholder(R.drawable.ic_profile)
                            .error(R.drawable.ic_profile)
                            .circleCrop()
                            .into(profileImage);
                }
            });
        } else {
            profileImage.setImageResource(R.drawable.ic_profile);
        }
    }

}