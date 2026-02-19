package com.example.gotaximobile.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.RideHistoryDTO;

import java.util.ArrayList;
import java.util.List;
public class PassengerRideHistoryAdapter extends RecyclerView.Adapter<PassengerRideHistoryAdapter.VH> {

    public interface OnRideClick {
        void onClick(RideHistoryDTO item);
        void onFavoriteClick(RideHistoryDTO item, int position);
    }

    private final List<RideHistoryDTO> data = new ArrayList<>();
    private final OnRideClick listener;

    public PassengerRideHistoryAdapter(OnRideClick listener) {
        this.listener = listener;
    }

    public void submit(List<RideHistoryDTO> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_passenger_history, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        RideHistoryDTO r = data.get(pos);

        h.route.setText((r.getStartAddress() == null ? "" : r.getStartAddress()) + " → " +
                (r.getEndAddress() == null ? "" : r.getEndAddress()));

        String started = r.getStartedAt() == null ? "—" : r.getStartedAt().toString().replace('T',' ').substring(0,16);
        String ended = r.getEndedAt() == null ? "Ongoing" : r.getEndedAt().toString().replace('T',' ').substring(0,16);
        h.time.setText(started + "  -  " + ended);

        h.price.setText(String.format("%.2f RSD", r.getPrice()));

        String flags = r.isCanceled() ? "CANCELED" : "OK";
        if (r.isPanicTriggered()) flags += " | PANIC";
        h.flags.setText(flags);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(r);
        });

        int heartIcon = r.isFavorite() ? R.drawable.ic_heart_fill : R.drawable.ic_hearth;
        h.btnFavorite.setImageResource(heartIcon);

        h.btnFavorite.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFavoriteClick(r, pos);
            }
        });

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(r);
        });
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView route, time, price, flags;
        ImageButton btnFavorite;
        VH(@NonNull View item) {
            super(item);
            route = item.findViewById(R.id.tvRoute);
            time = item.findViewById(R.id.tvTime);
            price = item.findViewById(R.id.tvPrice);
            flags = item.findViewById(R.id.tvFlags);
            btnFavorite = item.findViewById(R.id.btnFavorite);
        }
    }
}
