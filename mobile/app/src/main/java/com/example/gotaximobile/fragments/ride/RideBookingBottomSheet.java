package com.example.gotaximobile.fragments.ride;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.GeocodeSuggestionDTO;
import com.example.gotaximobile.models.dtos.LocationDTO;
import com.example.gotaximobile.models.dtos.RideRequestDTO;
import com.example.gotaximobile.models.dtos.RouteDTO;
import com.example.gotaximobile.network.RetrofitClient;
import com.example.gotaximobile.viewmodels.RideBookingViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.materialswitch.MaterialSwitch;

import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideBookingBottomSheet extends BottomSheetDialogFragment {

    private LinearLayout containerStops, containerPassengers;
    private AutoCompleteTextView acStart, acDestination;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private RideBookingViewModel viewModel;
    private String selectedScheduledTime = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_ride_booking, container, false);

        viewModel = new androidx.lifecycle.ViewModelProvider(requireParentFragment()).get(RideBookingViewModel.class);

        // Initialize Views
        containerStops = v.findViewById(R.id.containerStops);
        containerPassengers = v.findViewById(R.id.containerPassengers);
        acStart = v.findViewById(R.id.acStart);
        acDestination = v.findViewById(R.id.acDestination);

        // Setup Debounce for static fields
        setupDebounce(acStart);
        setupSuggestionClick(acStart, "start");
        setupDebounce(acDestination);
        setupSuggestionClick(acDestination, "end");

        // Add Stop Logic
        v.findViewById(R.id.btnAddStop).setOnClickListener(view -> addDynamicField(containerStops, "Intermediate Stop", true));

        // Add Passenger Logic
        v.findViewById(R.id.btnAddPassenger).setOnClickListener(view -> addDynamicField(containerPassengers, "Passenger Email", false));

        // Reset Logic
        v.findViewById(R.id.btnReset).setOnClickListener(view -> {
            containerStops.removeAllViews();
            containerPassengers.removeAllViews();
            acStart.setText("");
            acDestination.setText("");

            //if (viewModel != null) viewModel.reset();

            // 3. Tell HomeFragment to clear the Map
            getParentFragmentManager().setFragmentResult("clear_map", new Bundle());
        });

        // Vehicle Selection (MaterialButtonToggleGroup handles borders automatically)
        MaterialButtonToggleGroup vehicleGroup = v.findViewById(R.id.toggleGroupVehicle);
        vehicleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                MaterialButton checkedButton = group.findViewById(checkedId);

                String vehicleType = checkedButton.getText().toString();

                Bundle res = new Bundle();
                res.putString("type", vehicleType);
                getParentFragmentManager().setFragmentResult("calculate_price", res);
            }
        });

        // Final Buttons
        v.findViewById(R.id.btnOrder).setOnClickListener(view -> {
            selectedScheduledTime = null;
            processOrder(false);
        });
        v.findViewById(R.id.btnSchedule).setOnClickListener(view -> showTimePicker());

        return v;
    }

    private void showTimePicker() {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, selectedMinute) -> {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedCal.set(Calendar.MINUTE, selectedMinute);
            selectedCal.set(Calendar.SECOND, 0);

            // 5 Hours Limit Logic
            Calendar maxLimit = Calendar.getInstance();
            maxLimit.add(Calendar.HOUR_OF_DAY, 5);

            if (selectedCal.before(now)) {
                Toast.makeText(getContext(), "Cannot select time in the past", Toast.LENGTH_SHORT).show();
            } else if (selectedCal.after(maxLimit)) {
                Toast.makeText(getContext(), "Maximum schedule time is 5 hours from now", Toast.LENGTH_SHORT).show();
            } else {
                // Format for Backend (ISO 8601)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                selectedScheduledTime = sdf.format(selectedCal.getTime());

                // UI Update: Update the button text to show selected time
                MaterialButton btnSchedule = getView().findViewById(R.id.btnSchedule);
                btnSchedule.setText("At " + String.format("%02d:%02d", hourOfDay, selectedMinute));

                // Trigger the actual order
                processOrder(true);
            }
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void addDynamicField(LinearLayout container, String hint, boolean isLocation) {
        View itemView = getLayoutInflater().inflate(R.layout.item_dynamic_input, container, false);
        AutoCompleteTextView input = itemView.findViewById(R.id.acInput);
        input.setHint(hint);
        if (isLocation) {
            // Only setup debounce and click listeners if it's a location/stop
            setupDebounce(input);
            setupSuggestionClick(input, "stop");

            itemView.findViewById(R.id.btnRemove).setOnClickListener(v -> {
                int index = containerStops.indexOfChild(itemView);
                if (index != -1) {
                    viewModel.stopPoints.remove(index);
                    viewModel.stopAddresses.remove(index);

                    containerStops.removeView(itemView);

                    Bundle res = new Bundle();
                    res.putString("name", input.getText().toString());
                    res.putString("tag", "stop");
                    getParentFragmentManager().setFragmentResult("recalculate_route", res);
                }
            });
        } else {
            // For passengers, treat it as a normal EditText
            input.setAdapter(null); // Ensure no adapter is attached
            input.setThreshold(999); // Effectively prevents dropdown from showing

            itemView.findViewById(R.id.btnRemove).setOnClickListener(v -> {
                container.removeView(itemView);
            });
        }


        container.addView(itemView);
    }

    private void setupSuggestionClick(AutoCompleteTextView textView, String tag) {
        textView.setOnItemClickListener((parent, view, position, id) -> {
            GeocodeSuggestionDTO selected = (GeocodeSuggestionDTO) parent.getItemAtPosition(position);

            // Prepare data for HomeFragment
            Bundle result = new Bundle();
            result.putDouble("lat", Double.parseDouble(selected.lat));
            result.putDouble("lon", Double.parseDouble(selected.lon));
            result.putString("name", selected.display_name);
            result.putString("tag", tag); // "start", "end", or "stop"

            getParentFragmentManager().setFragmentResult("pin_selected", result);
        });
    }

    private void setupDebounce(AutoCompleteTextView textView) {
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> fetchSuggestions(s.toString(), textView);
                searchHandler.postDelayed(searchRunnable, 1000); // 1 second delay
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchSuggestions(String query, AutoCompleteTextView textView) {
        if (query.length() < 3) return;

        RetrofitClient.mapService(getContext()).searchStreet(query).enqueue(new Callback<List<GeocodeSuggestionDTO>>() {
            @Override
            public void onResponse(Call<List<GeocodeSuggestionDTO>> call, Response<List<GeocodeSuggestionDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GeocodeSuggestionDTO> fullList = response.body();

                    // Limit to top 5 suggestions
                    List<GeocodeSuggestionDTO> limitedList = fullList.subList(0, Math.min(fullList.size(), 5));

                    ArrayAdapter<GeocodeSuggestionDTO> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            limitedList
                    );

                    textView.setAdapter(adapter);

                    // Show dropdown only if the user is still typing in THIS field
                    if (textView.hasFocus()) {
                        textView.showDropDown();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<GeocodeSuggestionDTO>> call, Throwable t) {
                Log.e("API_ERROR", "Geocode failed: " + t.getMessage());
            }
        });
    }

    private void processOrder(boolean isScheduled) {
        // 1. Basic Validation
        String startAddr = acStart.getText().toString();
        String endAddr = acDestination.getText().toString();

        MaterialButtonToggleGroup vehicleGroup = getView().findViewById(R.id.toggleGroupVehicle);
        int checkedId = vehicleGroup.getCheckedButtonId();
        if (checkedId == View.NO_ID) {
            Toast.makeText(getContext(), "Please select a vehicle type", Toast.LENGTH_SHORT).show();
            return;
        }
        String selectedVehicle = ((Button)getView().findViewById(checkedId)).getText().toString().toUpperCase();

        // 2. Build the Payload
        RideRequestDTO payload = new RideRequestDTO();
        payload.vehicleType = selectedVehicle;
        payload.babyTransport = ((MaterialSwitch)getView().findViewById(R.id.switchBabies)).isChecked();
        payload.petTransport = ((MaterialSwitch)getView().findViewById(R.id.switchPets)).isChecked();
        payload.scheduledAt = isScheduled ? selectedScheduledTime : null;

        RouteDTO route = new RouteDTO();
        route.distanceKm = viewModel.distanceKm;
        route.estimatedTimeMin = viewModel.durationMinutes;


        route.start = new LocationDTO(viewModel.startAddress,
                viewModel.startPoint.getLatitude(), viewModel.startPoint.getLongitude());


        route.destination = new LocationDTO(viewModel.endAddress,
                viewModel.endPoint.getLatitude(), viewModel.endPoint.getLongitude());

        route.stops = new ArrayList<>();
        for (int i = 0; i < viewModel.stopPoints.size(); i++) {
            GeoPoint p = viewModel.stopPoints.get(i);
            String addr = viewModel.stopAddresses.get(i);
            route.stops.add(new LocationDTO(addr, p.getLatitude(), p.getLongitude()));
        }

        payload.route = route;

        // 4. Extract Passengers
        payload.passengersEmails = new ArrayList<>();
        for (int i = 0; i < containerPassengers.getChildCount(); i++) {
            View v = containerPassengers.getChildAt(i);
            AutoCompleteTextView emailInput = v.findViewById(R.id.acInput);
            String email = emailInput.getText().toString().trim();
            if (!email.isEmpty()) payload.passengersEmails.add(email);
        }

        if (isScheduled){
            payload.scheduledAt = selectedScheduledTime;
        }

        // 5. API Call
        RetrofitClient.rideService(getContext()).createRide(payload).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Ride created!", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }
}