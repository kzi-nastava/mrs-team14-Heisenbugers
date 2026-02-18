package com.example.gotaximobile.fragments.chat;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.models.Message;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private final List<Message> messages;
    private final String currentUser;

    public ChatAdapter(List<Message> messages, String currentUser) {
        this.messages = messages;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.textContent.setText(message.content);
        holder.textTime.setText(
                message.sentAt.format(DateTimeFormatter.ofPattern("HH:mm"))
        );

        boolean isMe = message.from.equals(currentUser);

        FrameLayout.LayoutParams params =
                (FrameLayout.LayoutParams) holder.container.getLayoutParams();

        if (isMe) {
            params.gravity = Gravity.END;
            holder.container.setBackgroundResource(R.drawable.bg_message_me);
            holder.textContent.setTextColor(Color.WHITE);
            holder.textTime.setTextColor(Color.LTGRAY);
        } else {
            params.gravity = Gravity.START;
            holder.container.setBackgroundResource(R.drawable.bg_message_other);
            holder.textContent.setTextColor(Color.BLACK);
            holder.textTime.setTextColor(Color.DKGRAY);
        }

        holder.container.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textContent, textTime;
        LinearLayout container;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textContent = itemView.findViewById(R.id.textContent);
            textTime = itemView.findViewById(R.id.textTime);
            container = itemView.findViewById(R.id.container);
        }
    }
}

