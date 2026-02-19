package com.example.gotaximobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.dtos.BlockableUserDTO;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserVH> {
    private List<BlockableUserDTO> users = new ArrayList<>();
    private OnUserBlockListener listener;

    public interface OnUserBlockListener {
        void onAction(BlockableUserDTO user, int position);
    }

    public UserAdapter(OnUserBlockListener listener) { this.listener = listener; }

    public void submitList(List<BlockableUserDTO> newList) {
        this.users = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_card, parent, false);
        return new UserVH(v);
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }


    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserVH holder, int position) {
        BlockableUserDTO user = users.get(position);
        holder.name.setText(user.firstName + " " + user.lastName);
        holder.email.setText(user.email);
        holder.role.setText(user.role);

        boolean isBlocked = user.blocked;
        holder.status.setText(isBlocked ? "BLOCKED" : "ACTIVE");
        holder.btnAction.setText(isBlocked ? "UNBLOCK" : "BLOCK");

        if (isBlocked) {
            holder.btnAction.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
            holder.status.setChipBackgroundColorResource(android.R.color.holo_red_light);
        } else {
            holder.btnAction.setTextColor(android.graphics.Color.parseColor("#C62828"));
            holder.status.setChipBackgroundColorResource(android.R.color.holo_green_light);
        }

        holder.btnAction.setOnClickListener(v -> listener.onAction(user, position));
    }


    public static class UserVH extends RecyclerView.ViewHolder {
        TextView name, email;
        com.google.android.material.chip.Chip role, status;
        com.google.android.material.button.MaterialButton btnAction;

        UserVH(@NonNull View item) {
            super(item);
            name = item.findViewById(R.id.tvUserName);
            email = item.findViewById(R.id.tvUserEmail);
            role = item.findViewById(R.id.chipRole);
            status = item.findViewById(R.id.chipStatus);
            btnAction = item.findViewById(R.id.btnBlockAction);
        }
    }
}