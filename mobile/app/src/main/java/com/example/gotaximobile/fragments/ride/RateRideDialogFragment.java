package com.example.gotaximobile.fragments.ride;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.gotaximobile.R;
import com.example.gotaximobile.network.RetrofitClient;
import com.example.gotaximobile.network.RideService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RateRideDialogFragment extends DialogFragment {

    private int driverRate = 0;
    private int vehicleRate = 0;

    private LinearLayout driverStarsLayout;
    private LinearLayout vehicleStarsLayout;

    private Button submitButton;

    private TextView startAddressView;
    private TextView endAddressView;
    private TextView timeValueView;
    private TextView priceValueView;
    private EditText commentInput;

    public static RateRideDialogFragment newInstance(String startAddress, String endAddress,
                                                     String time, String price, String rideId) {
        RateRideDialogFragment fragment = new RateRideDialogFragment();
        Bundle args = new Bundle();
        args.putString("start", startAddress);
        args.putString("end", endAddress);
        args.putString("time", time);
        args.putString("price", price);
        args.putString("rideId", rideId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the modal layout
        return inflater.inflate(R.layout.dialog_rate_ride, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        driverStarsLayout = view.findViewById(R.id.driverStars);
        vehicleStarsLayout = view.findViewById(R.id.vehicleStars);
        submitButton = view.findViewById(R.id.submitButton);

        startAddressView = view.findViewById(R.id.startAddress);
        endAddressView = view.findViewById(R.id.endAddress);
        timeValueView = view.findViewById(R.id.timeValue);
        priceValueView = view.findViewById(R.id.priceValue);
        commentInput = view.findViewById(R.id.commentInput);

        ImageView closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dismiss());

        // Load data from arguments
        if (getArguments() != null) {
            startAddressView.setText(getArguments().getString("start", ""));
            endAddressView.setText(getArguments().getString("end", ""));
            timeValueView.setText(getArguments().getString("time", ""));
            priceValueView.setText(getArguments().getString("price", ""));
        }

        // Initialize stars
        setupStars(driverStarsLayout, true);
        setupStars(vehicleStarsLayout, false);

        // Submit button
        submitButton.setOnClickListener(v -> submitRate());
        updateSubmitButtonState();
    }

    private void setupStars(LinearLayout layout, boolean isDriver) {
        layout.removeAllViews();
        for (int i = 1; i <= 5; i++) {
            ImageView star = new ImageView(requireContext());
            star.setImageResource(R.drawable.ic_star_outline); // empty star drawable
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(72, 72);
            params.setMarginStart(8);
            star.setLayoutParams(params);
            final int index = i;
            star.setOnClickListener(v -> {
                if (isDriver) {
                    driverRate = index;
                    updateStars(driverStarsLayout, driverRate);
                } else {
                    vehicleRate = index;
                    updateStars(vehicleStarsLayout, vehicleRate);
                }
                updateSubmitButtonState();
            });
            layout.addView(star);
        }
    }

    private void updateStars(LinearLayout layout, int filled) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            ImageView star = (ImageView) layout.getChildAt(i);
            if (i < filled) {
                star.setImageResource(R.drawable.ic_star_filled); // filled star
            } else {
                star.setImageResource(R.drawable.ic_star_outline); // empty star
            }
        }
    }

    private void updateSubmitButtonState() {
        submitButton.setEnabled(driverRate > 0 && vehicleRate > 0);
    }

    private void submitRate() {
        Map<String, Object> body = new HashMap<>();
        body.put("driverScore", driverRate);
        body.put("vehicleScore", vehicleRate);
        body.put("comment", commentInput.getText().toString());
        assert getArguments() != null;
        UUID rideId = UUID.fromString(getArguments().getString("rideId", ""));
        RideService service = RetrofitClient.rideService(requireContext());
        service.rateRide(rideId, body).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, String>> call,
                                   @NonNull Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    /*
                    Toast.makeText(requireContext(),
                            "Ride successfully rated", Toast.LENGTH_SHORT).show();

                     */
                } else {
                    /*
                    Toast.makeText(requireContext(),
                            "Failed to rate ride", Toast.LENGTH_SHORT).show();

                     */
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                /*
                Toast.makeText(requireContext(),
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        
                 */
            }
        });


        dismiss(); // close modal
    }
}
