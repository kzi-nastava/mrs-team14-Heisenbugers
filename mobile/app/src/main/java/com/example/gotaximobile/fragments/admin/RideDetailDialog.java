package com.example.gotaximobile.fragments.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.AdminRideDTO;

public class RideDetailDialog extends DialogFragment {

    private final AdminRideDTO ride;

    public RideDetailDialog(AdminRideDTO ride) {
        this.ride = ride;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_ride_detail, container, false);

        TextView driverName = view.findViewById(R.id.driverName);
        TextView startAddress = view.findViewById(R.id.startAddress);
        TextView endAddress = view.findViewById(R.id.endAddress);
        TextView startedAt = view.findViewById(R.id.startedAt);
        TextView endedAt = view.findViewById(R.id.endedAt);

        driverName.setText(ride.driver.firstName + " " + ride.driver.lastName);
        startAddress.setText(ride.ride.startAddress);
        endAddress.setText(ride.ride.destinationAddress);
        startedAt.setText(ride.ride.startedAt);
        endedAt.setText(ride.ride.endedAt != null ? ride.ride.endedAt : "Ongoing");

        return view;
    }
}


