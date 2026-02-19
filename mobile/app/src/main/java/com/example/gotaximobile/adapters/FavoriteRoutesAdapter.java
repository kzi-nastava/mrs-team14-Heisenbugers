package com.example.gotaximobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.FavoriteRouteDTO;

import java.util.List;
import java.util.Locale;

public class FavoriteRoutesAdapter extends RecyclerView.Adapter<FavoriteRoutesAdapter.ViewHolder> {

    private final List<FavoriteRouteDTO> routes;
    private final OnRouteClickListener listener;

    public interface OnRouteClickListener {
        void onUse(FavoriteRouteDTO route);
        void onDelete(FavoriteRouteDTO route);
    }

    public FavoriteRoutesAdapter(List<FavoriteRouteDTO> routes, OnRouteClickListener listener) {
        this.routes = routes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_route, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteRouteDTO route = routes.get(position);

        holder.tvRouteTitle.setText(route.startAddress + " ➔ " + route.endAddress);

        int stopCount = route.stops != null ? route.stops.size() : 0;
        String metrics = String.format(Locale.getDefault(),
                "%.1f km • %d min • %d stops",
                route.distanceKm,
                route.timeMin,
                stopCount);
        holder.tvRouteMetrics.setText(metrics);

        holder.btnUse.setOnClickListener(v -> listener.onUse(route));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(route));
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRouteTitle, tvRouteMetrics;
        Button btnUse;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRouteTitle = itemView.findViewById(R.id.tvRouteTitle);
            tvRouteMetrics = itemView.findViewById(R.id.tvRouteMetrics);
            btnUse = itemView.findViewById(R.id.btnUseRoute);
            btnDelete = itemView.findViewById(R.id.btnDeleteFavorite);
        }
    }
}