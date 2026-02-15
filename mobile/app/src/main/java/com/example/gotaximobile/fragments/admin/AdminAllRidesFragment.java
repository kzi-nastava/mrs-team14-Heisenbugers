package com.example.gotaximobile.fragments.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.AdminJustRideDTO;
import com.example.gotaximobile.models.dtos.AdminRideDTO;
import com.example.gotaximobile.models.dtos.DriverDto;

import java.util.ArrayList;
import java.util.List;

public class AdminAllRidesFragment extends Fragment {

    private EditText searchEditText;
    private RecyclerView ridesRecyclerView;
    private TextView noRidesTextView;
    private AdminRideAdapter adapter;
    private List<AdminRideDTO> rideList = new ArrayList<>();
    private List<AdminRideDTO> filteredList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_all_rides, container, false);
        searchEditText = view.findViewById(R.id.searchEditText);
        ridesRecyclerView = view.findViewById(R.id.ridesRecyclerView);
        noRidesTextView = view.findViewById(R.id.noRidesTextView);

        adapter = new AdminRideAdapter(filteredList, ride -> openRideDialog(ride));
        ridesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ridesRecyclerView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                applyFilter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        loadRides();
        return view;
    }

    private void applyFilter(String term) {
        filteredList.clear();
        for (AdminRideDTO r : rideList) {
            if ((r.driver.firstName + " " + r.driver.lastName + " " +
                    r.ride.startAddress + " " + r.ride.destinationAddress + " " +
                    r.ride.startedAt + " " + (r.ride.endedAt != null ? r.ride.endedAt : ""))
                    .toLowerCase().contains(term.toLowerCase())) {
                filteredList.add(r);
            }
        }
        noRidesTextView.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
    }

    private void openRideDialog(AdminRideDTO ride) {
        RideDetailDialog dialog = new RideDetailDialog(ride);
        dialog.show(getParentFragmentManager(), "rideDetail");
    }

    private void loadRides() {
        rideList.clear();

        rideList.add(new AdminRideDTO() {{
            ride = new AdminJustRideDTO() {{
                rideId = "ride-123";
                status = "COMPLETED";
                startedAt = "2025-12-19T08:12:00";
                endedAt = "2025-12-19T10:12:00";
                startAddress = "ул.Атамана Головатого 2а";
                destinationAddress = "ул.Красная 113";
                canceled = false;
                canceledBy = null;
                price = 350;
                panicTriggered = false;
            }};
            driver = new DriverDto() {{
                firstName = "Vozac";
                lastName = "Vozacovic";
            }};
            vehicleLatitude = 44.7886;
            vehicleLongitude = 20.4689;
        }});

        rideList.add(new AdminRideDTO() {{
            ride = new AdminJustRideDTO() {{
                rideId = "ride-456";
                status = "CANCELED";
                startedAt = "2025-12-20T14:00:00";
                endedAt = null;
                startAddress = "ул.Ленина 50";
                destinationAddress = "ул.Пушкина 20";
                canceled = true;
                canceledBy = "PASSENGER";
                price = 0;
                panicTriggered = false;
            }};
            driver = new DriverDto() {{
                firstName = "Marko";
                lastName = "Markovic";
            }};
            vehicleLatitude = 44.7866;
            vehicleLongitude = 20.4489;
        }});

        applyFilter(searchEditText.getText().toString());
    }
}
