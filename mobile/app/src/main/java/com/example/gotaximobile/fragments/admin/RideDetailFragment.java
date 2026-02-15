package com.example.gotaximobile.fragments.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.fragments.MapFragment;
import com.example.gotaximobile.models.MapPin;
import com.example.gotaximobile.models.dtos.AdminRideDTO;

public class RideDetailFragment extends Fragment {

    private AdminRideDTO ride;

    public RideDetailFragment() {
    }

    public static RideDetailFragment newInstance(AdminRideDTO ride) {
        RideDetailFragment fragment = new RideDetailFragment();
        fragment.ride = ride;
        return fragment;
    }


    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_detail, container, false);

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MapFragment mapFragment = (MapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);

        MapPin carPin = new MapPin(
                ride.vehicleLatitude,
                ride.vehicleLongitude,
                R.drawable.ic_car_map,
                "They are here"
        );
        carPin.snapToRoad = true;
        assert mapFragment != null;
        mapFragment.addPin(carPin);
    }
}


