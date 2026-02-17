package com.example.gotaximobile.fragments.ride;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.LocationDTO;
import com.example.gotaximobile.models.dtos.RideEstimateRequestDTO;
import com.example.gotaximobile.models.dtos.RideEstimateResponseDTO;
import com.example.gotaximobile.models.enums.VehicleType;
import com.example.gotaximobile.network.PublicService;
import com.example.gotaximobile.network.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EstimateRideBottomSheet extends BottomSheetDialogFragment {

    private PublicService publicApi;

    private TextInputEditText etStartAddress, etDestAddress;
    private MaterialButton btnEstimate;

    private final OkHttpClient http = new OkHttpClient();

    private Context appContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_estimate_ride, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appContext = requireContext().getApplicationContext();

        publicApi = RetrofitClient.publicService(requireContext());

        etStartAddress = view.findViewById(R.id.etStartAddress);
        etDestAddress = view.findViewById(R.id.etDestAddress);

        btnEstimate = view.findViewById(R.id.btnEstimate);
        btnEstimate.setOnClickListener(v -> doEstimate());
    }

    private void doEstimate() {
        String startAddr = text(etStartAddress);
        String destAddr = text(etDestAddress);

        if (TextUtils.isEmpty(startAddr) || TextUtils.isEmpty(destAddr)) {
            toastUi("Enter start and destination address");
            return;
        }

        btnEstimate.setEnabled(false);

        geocodeAddress(startAddr, new GeoCallback() {
            @Override
            public void onSuccess(LocationDTO startLoc) {
                geocodeAddress(destAddr, new GeoCallback() {
                    @Override
                    public void onSuccess(LocationDTO destLoc) {
                        callEstimateBackend(startLoc, destLoc);
                    }

                    @Override
                    public void onError(String message) {
                        enableButtonWithToast(message);
                    }
                });
            }

            @Override
            public void onError(String message) {
                enableButtonWithToast(message);
            }
        });
    }

    private void callEstimateBackend(LocationDTO startLoc, LocationDTO destLoc) {
        RideEstimateRequestDTO req = new RideEstimateRequestDTO();
        req.setStart(startLoc);
        req.setDestination(destLoc);


        req.setVehicleType(VehicleType.STANDARD);
        req.setStops(Collections.emptyList());
        req.setBabyTransport(false);
        req.setPetTransport(false);

        publicApi.estimateRide(req).enqueue(new retrofit2.Callback<RideEstimateResponseDTO>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<RideEstimateResponseDTO> call,
                                   @NonNull retrofit2.Response<RideEstimateResponseDTO> response) {

                if (!isAdded() || getActivity() == null) return;

                btnEstimate.setEnabled(true);

                if (!response.isSuccessful() || response.body() == null) {
                    toastUi("Estimate failed: " + response.code());
                    return;
                }

                RideEstimateResponseDTO data = response.body();

                ArrayList<double[]> points = new ArrayList<>();
                List<LocationDTO> route = data.getRoutePoints();
                if (route != null) {
                    for (LocationDTO p : route) {
                        points.add(new double[]{p.latitude, p.longitude});
                    }
                }

                Bundle result = new Bundle();
                result.putString("price", data.getEstimatedPrice() != null ? data.getEstimatedPrice().toPlainString() : "");
                result.putInt("timeMin", data.getEstimatedTimeMin());
                result.putDouble("distKm", data.getDistanceKm());
                result.putSerializable("routePoints", points);

                getParentFragmentManager().setFragmentResult("estimate_result", result);

                dismissAllowingStateLoss();
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<RideEstimateResponseDTO> call, @NonNull Throwable t) {
                if (!isAdded() || getActivity() == null) return;

                btnEstimate.setEnabled(true);
                toastUi("Network error: " + t.getMessage());
            }
        });
    }

    private void geocodeAddress(String query, GeoCallback callback) {
        String url = "https://nominatim.openstreetmap.org/search?format=json&limit=1&q="
                + Uri.encode(query);


        String userAgent = (appContext != null ? appContext.getPackageName() : "gotaxi-mobile") + " (student-project)";

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .build();

        http.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError("Geocoding failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("Geocoding failed: " + response.code());
                    return;
                }

                String body = response.body().string();
                try {
                    JSONArray arr = new JSONArray(body);
                    if (arr.length() == 0) {
                        callback.onError("Address not found: " + query);
                        return;
                    }
                    JSONObject obj = arr.getJSONObject(0);
                    double lat = Double.parseDouble(obj.getString("lat"));
                    double lon = Double.parseDouble(obj.getString("lon"));

                    LocationDTO loc = new LocationDTO();
                    loc.latitude = lat;
                    loc.longitude = lon;
                    loc.address = query;

                    callback.onSuccess(loc);
                } catch (Exception e) {
                    callback.onError("Geocoding parse error");
                }
            }
        });
    }

    private void enableButtonWithToast(String msg) {
        if (!isAdded() || getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            if (btnEstimate != null) btnEstimate.setEnabled(true);
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void toastUi(String msg) {
        if (!isAdded() || getActivity() == null) return;
        getActivity().runOnUiThread(() ->
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show()
        );
    }

    private String text(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private interface GeoCallback {
        void onSuccess(LocationDTO location);
        void onError(String message);
    }
}
