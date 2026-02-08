package com.example.gotaximobile.fragments.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gotaximobile.R;
import com.example.gotaximobile.adapters.ProfileTabAdapter;
import com.example.gotaximobile.models.dtos.GetProfileDTO;
import com.example.gotaximobile.network.RetrofitClient;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PersonalProfile extends Fragment {

    private ProfileCardView nameCard, emailCard, addressCard, phoneCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_profile, container, false);

        nameCard = view.findViewById(R.id.card_name);
        emailCard = view.findViewById(R.id.card_email);
        addressCard = view.findViewById(R.id.card_address);
        phoneCard = view.findViewById(R.id.card_phone);

        fetchProfileData();

        return view;
    }

    private void fetchProfileData() {
        RetrofitClient.profileService(getContext()).getProfileInfo().enqueue(new Callback<GetProfileDTO>() {
            @Override
            public void onResponse(@NonNull Call<GetProfileDTO> call, @NonNull Response<GetProfileDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetProfileDTO profile = response.body();
                    updateView(profile);
                } else {
                    Log.e("API_ERROR", "Response failed: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetProfileDTO> call, @NonNull Throwable t) {
                Log.e("NETWORK_ERROR", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void updateView(GetProfileDTO profile) {
        String fullName = profile.firstName + " " + profile.lastName;

        nameCard.setData("Name", fullName, R.drawable.ic_person);
        emailCard.setData("Email", profile.email, R.drawable.ic_email);
        addressCard.setData("Address", profile.address, R.drawable.ic_address);
        phoneCard.setData("Phone", profile.phoneNumber, R.drawable.ic_phone);
    }
}