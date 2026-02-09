package com.example.gotaximobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.DriverRequestListDTO;

import java.util.List;

public class DriverRequestAdapter extends RecyclerView.Adapter<DriverRequestAdapter.ViewHolder> {
    private List<DriverRequestListDTO> requests;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DriverRequestListDTO request);
    }

    public DriverRequestAdapter(List<DriverRequestListDTO> requests, OnItemClickListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_driver_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DriverRequestListDTO request = requests.get(position);
        holder.tvName.setText(request.firstName + " " + request.lastName);
        holder.tvEmail.setText(request.email);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(request));
    }

    @Override
    public int getItemCount() { return requests.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_driver_name);
            tvEmail = itemView.findViewById(R.id.tv_driver_email);
        }
    }
}