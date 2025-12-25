package com.example.gotaximobile.fragments.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gotaximobile.R;

public class DriverProfile extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_profile, container, false);

        ProfileCardView activeTodayCard = view.findViewById(R.id.card_active_hours);
        activeTodayCard.setData("Active In Last 24H", "6H", R.drawable.ic_person);

        ProfileCardView modelCard = view.findViewById(R.id.card_model);
        modelCard.setData("Model", "Ford Fiesta", R.drawable.ic_email);

        ProfileCardView typeCard = view.findViewById(R.id.card_type);
        typeCard.setData("Type", "Standard", R.drawable.ic_address);

        ProfileCardView plateNoCard = view.findViewById(R.id.card_plate_no);
        plateNoCard.setData("Plate No.", "NS-254-KL", R.drawable.ic_address);

        ProfileCardView seatsCard = view.findViewById(R.id.card_seats);
        seatsCard.setData("Seats", "5", R.drawable.ic_phone);

        ProfileCardView babiesAllowedCard = view.findViewById(R.id.card_babies_allowed);
        babiesAllowedCard.setData("Baby Allowed", "Yes", R.drawable.ic_phone);

        ProfileCardView petsAllowedCard = view.findViewById(R.id.card_pets_allowed);
        petsAllowedCard.setData("Pets Allowed", "No", R.drawable.ic_phone);

        return view;
    }
}