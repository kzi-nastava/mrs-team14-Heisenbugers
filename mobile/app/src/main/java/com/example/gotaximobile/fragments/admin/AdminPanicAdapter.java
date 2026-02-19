package com.example.gotaximobile.fragments.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.PanicEventDTO;

import java.util.ArrayList;
import java.util.List;


public class AdminPanicAdapter extends RecyclerView.Adapter<AdminPanicAdapter.ViewHolder> {

    public interface Listener {
        void onResolve(PanicEventDTO event);
        void onOpenDetails(PanicEventDTO event);
    }

    private final List<PanicEventDTO> items = new ArrayList<>();
    private final Listener listener;

    public AdminPanicAdapter(Listener listener) {
        this.listener = listener;
    }


    public void setItems(List<PanicEventDTO> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_panic_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        PanicEventDTO e = items.get(position);

        String route = (e.getStartAddress() != null ? e.getStartAddress() : "Unknown start")
                + " → "
                + (e.getEndAddress() != null ? e.getEndAddress() : "Unknown destination");
        h.tvRoute.setText(route);

        h.tvMessage.setText(e.getMessage() != null ? e.getMessage() : "");

        String created = e.getCreatedAt();
        h.tvCreatedAt.setText(created != null ? created : "");


        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOpenDetails(e);
        });


        h.btnResolve.setOnClickListener(v -> {
            if (listener != null) listener.onResolve(e);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoute, tvMessage, tvCreatedAt;
        Button btnResolve;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            btnResolve = itemView.findViewById(R.id.btnResolve);
        }
    }
}
