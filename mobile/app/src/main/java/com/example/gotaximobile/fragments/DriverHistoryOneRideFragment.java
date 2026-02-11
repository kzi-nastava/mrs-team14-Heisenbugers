package com.example.gotaximobile.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.fragments.ride.RateRideDialogFragment;
import com.example.gotaximobile.models.Constants;
import com.example.gotaximobile.models.Ride;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.Serializable;

public class DriverHistoryOneRideFragment extends Fragment {

    private Ride ride;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_history_one_ride, container, false);

        Bundle args = getArguments();
        if (args != null) {
            Serializable rideSer = args.getSerializable("ride");
            if (rideSer != null) {
                ride = (Ride) rideSer;

                // COMMON
                //TextView driver = view.findViewById(R.id.driver_name_value);
                //driver.setText(r.getDriver().getFullName());

                TextView start = view.findViewById(R.id.start_value);
                start.setText(ride.getStartLocation());

                TextView end = view.findViewById(R.id.end_value);
                end.setText(ride.getEndLocation());

                TextView time = view.findViewById(R.id.time_value);
                time.setText(ride.getFormatedTime());

                TextView price = view.findViewById(R.id.price_value);
                price.setText(ride.getFormatedPrice());

                TextView rating = view.findViewById(R.id.rating_value);
                rating.setText(String.format(Constants.LOCALE, "%.1f", ride.getRating()));

                TextView canceled = view.findViewById(R.id.cancelled_value);
                canceled.setText(ride.isCancelled() ? "Yes" : "No");

                // PASSENGERS (add TextViews dynamically)
                TableLayout passengersTable = view.findViewById(R.id.passengers_table);
                passengersTable.removeAllViews();
                passengersTable.setStretchAllColumns(true);

                for (int i = 0; i < ride.getPassengers().size(); i += 2) {
                    TableRow row = makeTableRow(ride, i);

                    passengersTable.addView(row);
                }

                // VIOLATIONS
                View violationsSection = view.findViewById(R.id.violations_section);

                if (ride.getTrafficViolations().isEmpty()) {
                    violationsSection.setVisibility(View.GONE);
                } else {
                    MakeViolationChips(view, ride);
                }

                // PANIC
                Chip panicChip = view.findViewById(R.id.was_panic_chip);
                if (!ride.isWasPanic()) {
                    panicChip.setVisibility(View.GONE);
                }
            }

        }

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MaterialButton rateButton = view.findViewById(R.id.rate_ride_button);
        rateButton.setOnClickListener(v -> {
            RateRideDialogFragment dialog = RateRideDialogFragment.newInstance(
                    ride.getStartLocation(),
                    ride.getEndLocation(),
                    ride.getFormatedTime(),
                    ride.getFormatedPrice(),
                    ride.getId().toString()
            );
            dialog.show(getParentFragmentManager(), "rateRideDialog");
        });
    }

    private void MakeViolationChips(View view, Ride r) {
        int strokeColor = ContextCompat.getColor(requireContext(), R.color.outline);

        ChipGroup chipGroup = view.findViewById(R.id.violations_chip_group);
        chipGroup.removeAllViews();

        for (String violation : r.getTrafficViolations()) {
            Chip chip = new Chip(getContext());
            chip.setText(violation);
            chip.setClickable(false);
            chip.setCheckable(false);
            chip.setBackgroundColor(Color.TRANSPARENT);
            chip.setTextColor(strokeColor);
            chip.setChipStrokeColor(ColorStateList.valueOf(strokeColor));
            chipGroup.addView(chip);
        }
    }

    @NonNull
    private TableRow makeTableRow(Ride r, int i) {
        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView tv1 = new TextView(getContext());
        tv1.setText(r.getPassengers().get(i).getFullName());
        tv1.setGravity(Gravity.CENTER);
        tv1.setPadding(16, 16, 16, 16);
        row.addView(tv1);


        if (i + 1 < r.getPassengers().size()) {
            TextView tv2 = new TextView(getContext());
            tv2.setText(r.getPassengers().get(i + 1).getFullName());
            tv2.setGravity(Gravity.CENTER);
            tv2.setPadding(16, 16, 16, 16);
            row.addView(tv2);

        }
        return row;
    }
}