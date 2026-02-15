package com.example.gotaximobile.fragments.admin;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.AdminRideDTO;

import java.util.List;

public class AdminRideAdapter extends RecyclerView.Adapter<AdminRideAdapter.ViewHolder> {

    public interface OnRideClick {
        void onClick(AdminRideDTO ride);
    }

    private List<AdminRideDTO> rides;
    private OnRideClick listener;

    public AdminRideAdapter(List<AdminRideDTO> rides, OnRideClick listener) {
        this.rides = rides;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_ride, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminRideDTO ride = rides.get(position);
        holder.driverName.setText(ride.driver.firstName + " " + ride.driver.lastName);
        holder.startAddress.setText(ride.ride.startAddress);
        holder.endAddress.setText(ride.ride.destinationAddress);
        holder.startedAt.setText(ride.ride.startedAt);
        holder.endedAt.setText(ride.ride.endedAt != null ? ride.ride.endedAt : "Ongoing");
        holder.itemView.setOnClickListener(v -> listener.onClick(ride));
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView driverName, startAddress, endAddress, startedAt, endedAt;

        ViewHolder(View itemView) {
            super(itemView);
            driverName = itemView.findViewById(R.id.driverName);
            startAddress = itemView.findViewById(R.id.startAddress);
            endAddress = itemView.findViewById(R.id.endAddress);
            startedAt = itemView.findViewById(R.id.startedAt);
            endedAt = itemView.findViewById(R.id.endedAt);
        }
    }
}
