package com.example.gotaximobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.Constants;
import com.example.gotaximobile.models.Ride;
import com.google.android.material.chip.Chip;

import java.io.Serializable;

public class DriverHistoryOneRideFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_history_one_ride, container, false);

        Bundle args = getArguments();
        if (args != null) {
            Serializable ride = args.getSerializable("ride");
            if (ride != null) {
                Ride r = (Ride) ride;
                TextView driver = view.findViewById(R.id.driver_name_value);
                driver.setText(r.getDriver().getFullName());

                TextView start = view.findViewById(R.id.start_value);
                start.setText(r.getStartLocation());

                TextView end = view.findViewById(R.id.end_value);
                end.setText(r.getEndLocation());

                TextView time = view.findViewById(R.id.time_value);
                time.setText(r.getFormatedTime());

                TextView price = view.findViewById(R.id.price_value);
                price.setText(r.getFormatedPrice());

                TextView rating = view.findViewById(R.id.rating_value);
                rating.setText(String.format(Constants.LOCALE, "%f", r.getRating()));

                TextView canceled = view.findViewById(R.id.cancelled_value);
                canceled.setText(r.isCancelled() ? "Yes" : "No");

                Chip panicChip = view.findViewById(R.id.was_panic_chip);
                if (!r.isWasPanic()) {
                    panicChip.setVisibility(View.GONE);
                }
            }

        }

        return view;

    }
}