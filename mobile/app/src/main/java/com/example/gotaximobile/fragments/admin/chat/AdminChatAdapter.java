package com.example.gotaximobile.fragments.admin.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.Chat;

import java.util.List;

public class AdminChatAdapter extends RecyclerView.Adapter<AdminChatAdapter.ViewHolder> {

    public interface OnChatClickListener {
        void onClick(Chat chat);
    }

    private List<Chat> chats;
    private OnChatClickListener listener;

    public AdminChatAdapter(List<Chat> chats, OnChatClickListener listener) {
        this.chats = chats;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        String name = chat.driver.firstName + " " + chat.driver.lastName;
        holder.tvName.setText(name);

        holder.itemView.setOnClickListener(v -> listener.onClick(chat));
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}

