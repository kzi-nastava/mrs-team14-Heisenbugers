package com.example.gotaximobile.fragments.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.AdminUserListItemDTO;

import java.util.ArrayList;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.VH> {

    public interface OnClick { void onClick(AdminUserListItemDTO u); }

    private final OnClick onClick;
    private final List<AdminUserListItemDTO> data = new ArrayList<>();

    public AdminUserAdapter(OnClick onClick) { this.onClick = onClick; }

    public void submit(List<AdminUserListItemDTO> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_admin_user, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        AdminUserListItemDTO u = data.get(position);
        h.name.setText(u.getFullName());
        h.email.setText(u.getEmail());
        h.itemView.setOnClickListener(v -> onClick.onClick(u));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, email;
        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            email = itemView.findViewById(R.id.tvEmail);
        }
    }
}
