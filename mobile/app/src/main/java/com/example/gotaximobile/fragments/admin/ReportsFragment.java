package com.example.gotaximobile.fragments.admin;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.data.TokenStorage;
import com.example.gotaximobile.models.dtos.BlockableUserDTO;
import com.example.gotaximobile.models.dtos.DailyItemDTO;
import com.example.gotaximobile.models.dtos.RideAnalyticsResponseDTO;
import com.example.gotaximobile.network.RetrofitClient;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportsFragment extends Fragment {

    private LineChart lineChart, rideChart, kilometersChart;
    private TextView tvTotalValue, tvAvgValue, tvTotalValueRides, tvAvgValueRides, tvTotalValueKilometers, tvAvgValueKilometers;
    private Button btnSelectRange;
    private TokenStorage storage;
    private long startTimestamp, endTimestamp;
    private String selectedFilter = "ME";
    private List<BlockableUserDTO> allUsers = new ArrayList<>();
    private String selectedUserId = null;
    private String selectedUserRole = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        lineChart = view.findViewById(R.id.reportsChart);
        rideChart = view.findViewById(R.id.ridesChart);
        kilometersChart = view.findViewById(R.id.kilometersChart);
        tvTotalValue = view.findViewById(R.id.tvTotalValue);
        tvAvgValue = view.findViewById(R.id.tvAvgValue);
        btnSelectRange = view.findViewById(R.id.btnSelectRange);
        storage = new TokenStorage(requireContext());

        tvTotalValue = view.findViewById(R.id.tvTotalValue);
        tvAvgValue = view.findViewById(R.id.tvAvgValue);

        tvTotalValueRides = view.findViewById(R.id.tvTotalValueRides);
        tvAvgValueRides = view.findViewById(R.id.tvAvgValueRides);

        tvTotalValueKilometers = view.findViewById(R.id.tvTotalValueKilometers);
        tvAvgValueKilometers = view.findViewById(R.id.tvAvgValueKilometers);

        setupAdminUserSelector(view);

        btnSelectRange.setOnClickListener(v -> openDateRangePicker());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialToolbar toolbar = view.findViewById(R.id.topAppBarAnalytics);
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });
    }

    private void setupAdminUserSelector(View view) {
        if (!"ADMIN".equals(storage.getRole())) return;

        TextInputLayout tilFilter = view.findViewById(R.id.tilUserFilter);
        AutoCompleteTextView actvFilter = view.findViewById(R.id.actvUserFilter);
        tilFilter.setVisibility(View.VISIBLE);

        String[] filterOptions = {"All Passengers", "All Drivers", "Specific User"};
        actvFilter.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, filterOptions));

        TextInputLayout tilEmailSelect = view.findViewById(R.id.tilUserSelect);
        AutoCompleteTextView actvEmailSelect = view.findViewById(R.id.actvUserSelect);

        actvFilter.setOnItemClickListener((parent, v, position, id) -> {
            selectedFilter = filterOptions[position];
            if ("Specific User".equals(selectedFilter)) {
                tilEmailSelect.setVisibility(View.VISIBLE);
            } else {
                tilEmailSelect.setVisibility(View.GONE);
                selectedUserId = null;
                selectedUserRole = null;
                if (startTimestamp != 0) fetchReportData();
            }
        });

        RetrofitClient.userService(getContext()).getBlockableUsers().enqueue(new Callback<List<BlockableUserDTO>>() {
            @Override
            public void onResponse(Call<List<BlockableUserDTO>> call, Response<List<BlockableUserDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allUsers = response.body();
                    List<String> userEmails = new ArrayList<>();
                    for (BlockableUserDTO user : allUsers) {
                        userEmails.add(user.email);
                    }
                    actvEmailSelect.setAdapter(new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_dropdown_item_1line, userEmails));
                }
            }
            @Override public void onFailure(Call<List<BlockableUserDTO>> call, Throwable t) { Log.e("API", "Fail fetch users"); }
        });

        actvEmailSelect.setOnItemClickListener((parent, v, position, id) -> {
            String selectedEmail = (String) parent.getItemAtPosition(position);
            for (BlockableUserDTO user : allUsers) {
                if (user.email.equals(selectedEmail)) {
                    selectedUserId = user.id.toString();
                    selectedUserRole = user.role;
                    break;
                }
            }
            if (startTimestamp != 0) fetchReportData();
        });
    }


    private void openDateRangePicker() {
        MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Dates")
                .setSelection(new Pair<>(MaterialDatePicker.todayInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()))
                .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            startTimestamp = selection.first;
            endTimestamp = selection.second;
            fetchReportData();
        });
        picker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void fetchReportData() {
        if (startTimestamp == 0 || endTimestamp == 0) return;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startStr = sdf.format(new Date(startTimestamp));
        String endStr = sdf.format(new Date(endTimestamp));

        String role = null;
        boolean aggregate = false;
        String userIdParam = null;

        if ("ADMIN".equals(storage.getRole())) {
            if ("All Drivers".equals(selectedFilter)) {
                role = "DRIVER";
                aggregate = true;
            } else if ("All Passengers".equals(selectedFilter)) {
                role = "PASSENGER";
                aggregate = true;
            } else if ("Specific User".equals(selectedFilter)) {
                userIdParam = selectedUserId;
                role = "ADMIN";
                aggregate = false;
                if (userIdParam == null) return;
            }
        } else {
            role = storage.getRole();
            aggregate = false;
        }

        RetrofitClient.rideAnalyticsService(getContext())
                .getRideAnalytics(startStr, endStr, role, userIdParam, aggregate)
                .enqueue(new Callback<RideAnalyticsResponseDTO>() {
                    @Override
                    public void onResponse(Call<RideAnalyticsResponseDTO> call, Response<RideAnalyticsResponseDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            updateUI(response.body());
                        }
                    }
                    @Override public void onFailure(Call<RideAnalyticsResponseDTO> call, Throwable t) { Log.e("NET", t.getMessage()); }
                });
    }

    private void updateUI(RideAnalyticsResponseDTO report) {
        if (report.daily == null || report.daily.isEmpty()) return;

        tvTotalValue.setText(String.format(Locale.getDefault(), "$%.2f", report.totals.money));
        tvAvgValue.setText(String.format(Locale.getDefault(), "$%.2f", report.totals.money / report.daily.size()));

        tvTotalValueRides.setText(String.format(Locale.getDefault(), "%d Rides", (int)report.totals.rides));
        tvAvgValueRides.setText(String.format(Locale.getDefault(), "%.1f Avg", (float) report.totals.rides / report.daily.size()));

        tvTotalValueKilometers.setText(String.format(Locale.getDefault(), "%.2f km", report.totals.kilometers));
        tvAvgValueKilometers.setText(String.format(Locale.getDefault(), "%.1f Avg", report.totals.kilometers / report.daily.size()));

        List<Entry> moneyEntries = new ArrayList<>();
        List<Entry> rideEntries = new ArrayList<>();
        List<Entry> distanceEntries = new ArrayList<>();
        final List<String> dates = new ArrayList<>();

        for (int i = 0; i < report.daily.size(); i++) {
            DailyItemDTO day = report.daily.get(i);
            moneyEntries.add(new Entry(i, (float) day.money));
            rideEntries.add(new Entry(i, (float) day.rides));
            distanceEntries.add(new Entry(i, (float) day.kilometers));
            dates.add(day.date.substring(5)); // MM-DD
        }

        // Render each chart separately
        setupSingleChart(lineChart, moneyEntries, "Money ($)", Color.BLUE, dates);
        setupSingleChart(rideChart, rideEntries, "Rides", Color.GREEN, dates);
        setupSingleChart(kilometersChart, distanceEntries, "Distance (km)", Color.RED, dates);
    }


    private void setupSingleChart(LineChart chart, List<Entry> entries, String label, int color, List<String> dates) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setLineWidth(2f);
        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(color);
        dataSet.setFillAlpha(30);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return (index >= 0 && index < dates.size()) ? dates.get(index) : "";
            }
        });

        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.animateX(800);
        chart.invalidate();
    }
}