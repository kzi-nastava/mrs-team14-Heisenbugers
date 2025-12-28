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


public class PersonalProfile extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_profile, container, false);

        ProfileCardView nameCard = view.findViewById(R.id.card_name);
        nameCard.setData("Name", "John Doe", R.drawable.ic_person);

        ProfileCardView emailCard = view.findViewById(R.id.card_email);
        emailCard.setData("Email", "johndoe@gmail.com", R.drawable.ic_email);

        ProfileCardView addressCard = view.findViewById(R.id.card_address);
        addressCard.setData("Address", "Bulevar Jovana Ducica 15, Novi Sad", R.drawable.ic_address);

        ProfileCardView phoneCard = view.findViewById(R.id.card_phone);
        phoneCard.setData("Phone", "+381645412147", R.drawable.ic_phone);


        return view;
    }
}