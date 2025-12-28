package com.example.gotaximobile.fragments;

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
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.Constants;
import com.example.gotaximobile.models.Ride;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

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

                // COMMON
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
                rating.setText(String.format(Constants.LOCALE, "%.1f", r.getRating()));

                TextView canceled = view.findViewById(R.id.cancelled_value);
                canceled.setText(r.isCancelled() ? "Yes" : "No");

                // PASSENGERS (add TextViews dynamically)
                TableLayout passengersTable = view.findViewById(R.id.passengers_table);
                passengersTable.removeAllViews();
                passengersTable.setStretchAllColumns(true);

                for (int i = 0; i < r.getPassengers().size(); i += 2) {
                    TableRow row = makeTableRow(r, i);

                    passengersTable.addView(row);
                }

                // VIOLATIONS
                View violationsSection = view.findViewById(R.id.violations_section);

                if (r.getTrafficViolations().isEmpty()) {
                    violationsSection.setVisibility(View.GONE);
                } else {
                    MakeViolationChips(view, r);
                }

                // PANIC
                Chip panicChip = view.findViewById(R.id.was_panic_chip);
                if (!r.isWasPanic()) {
                    panicChip.setVisibility(View.GONE);
                }
            }

        }

        return view;

    }

    private void MakeViolationChips(View view, Ride r) {
        ChipGroup chipGroup = view.findViewById(R.id.violations_chip_group);
        chipGroup.removeAllViews();

        for (String violation : r.getTrafficViolations()) {
            Chip chip = new Chip(getContext());
            chip.setText(violation);
            chip.setClickable(false);
            chip.setCheckable(false);
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