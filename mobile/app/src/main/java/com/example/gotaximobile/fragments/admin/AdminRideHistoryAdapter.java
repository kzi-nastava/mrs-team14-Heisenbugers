package com.example.gotaximobile.fragments.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.AdminRideListItemDTO;

import java.util.ArrayList;
import java.util.List;

public class AdminRideHistoryAdapter extends RecyclerView.Adapter<AdminRideHistoryAdapter.VH> {

    public interface OnRideClick {
        void onClick(AdminRideListItemDTO item);
    }

    private final List<AdminRideListItemDTO> data = new ArrayList<>();
    private final OnRideClick listener;

    public AdminRideHistoryAdapter(OnRideClick listener) {
        this.listener = listener;
    }

    private static final java.time.format.DateTimeFormatter DATE_TIME =
            java.time.format.DateTimeFormatter.ofPattern("dd.MM HH:mm");

    private String fmt(java.time.LocalDateTime dt) {
        return dt == null ? "" : dt.format(DATE_TIME);
    }

    private String money(java.math.BigDecimal price) {
        if (price == null) return "";
        return price.stripTrailingZeros().toPlainString() + " RSD";
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_ride_history, parent, false);
        return new VH(v);
    }



    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        AdminRideListItemDTO item = data.get(position);

        h.route.setText(safe(item.getStartAddress()) + " → " + safe(item.getDestinationAddress()));

        // status chip
        String status = item.getStatus() != null ? item.getStatus().toString() : "";
        h.chipStatus.setText(status);

        // price
        h.price.setText(money(item.getPrice()));

        // time: start — end / Ongoing
        String start = fmt(item.getStartedAt());
        String end = item.getEndedAt() != null ? fmt(item.getEndedAt()) : "Ongoing";
        h.time.setText(start + " — " + end);

        // flags
        StringBuilder f = new StringBuilder();
        if (item.isCanceled()) f.append("CANCELED");
        if (item.isPanicTriggered()) {
            if (f.length() > 0) f.append(" • ");
            f.append("PANIC");
        }
        h.flags.setText(f.length() == 0 ? "" : f.toString());

        h.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void submit(List<AdminRideListItemDTO> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView route, flags, price, time;
        com.google.android.material.chip.Chip chipStatus;

        VH(@NonNull View itemView) {
            super(itemView);
            route = itemView.findViewById(R.id.tvRoute);
            flags = itemView.findViewById(R.id.tvFlags);
            price = itemView.findViewById(R.id.tvPrice);
            time = itemView.findViewById(R.id.tvTime);
            chipStatus = itemView.findViewById(R.id.chipStatus);
        }
    }


    private String safe(String s) { return s == null ? "" : s; }
}
