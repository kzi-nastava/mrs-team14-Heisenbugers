package com.example.gotaximobile.fragments.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.data.TokenStorage;
import com.example.gotaximobile.models.Message;
import com.example.gotaximobile.network.ChatApi;
import com.example.gotaximobile.network.RetrofitClient;
import com.example.gotaximobile.services.ChatWebSocketService;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Message> messages = new ArrayList<>();

    private TextInputEditText editMessage;
    private ImageButton btnSend;

    private String currentUser = "user@example.com"; // from AuthService
    private String chatId;

    private ChatWebSocketService webSocketService;

    private TokenStorage tokenStorage;


    public static ChatFragment newInstance(String chatId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString("chat_id", chatId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            chatId = getArguments().getString("chat_id");
        }

        tokenStorage = new TokenStorage(requireContext());

        try {
            currentUser = tokenStorage.getSub();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        webSocketService = new ChatWebSocketService();

        String jwtToken = tokenStorage.getAccessToken();

        webSocketService.connect(jwtToken, chatId, message -> {
            requireActivity().runOnUiThread(() -> {
                messages.add(message);
                adapter.notifyItemInserted(messages.size() - 1);
                recyclerView.scrollToPosition(messages.size() - 1);
            });
        });

        recyclerView = view.findViewById(R.id.recyclerMessages);
        editMessage = view.findViewById(R.id.editMessage);
        btnSend = view.findViewById(R.id.btnSend);

        adapter = new ChatAdapter(messages, currentUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnSend.setOnClickListener(v -> sendMessage());

        editMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
        loadOldMessages();
    }

    private void sendMessage() {
        String content = editMessage.getText().toString().trim();
        if (content.isEmpty()) return;

        Message message = new Message(content, currentUser, LocalDateTime.now());

        webSocketService.sendMessage(message, chatId);

        editMessage.setText("");
    }

    private void loadOldMessages() {

        ChatApi api = RetrofitClient.chatApi(requireContext());

        api.loadMessages(tokenStorage.getAuthHeaderValue(), chatId)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<List<Message>> call,
                                           Response<List<Message>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            messages.addAll(response.body());
                            adapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(messages.size() - 1);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Message>> call, Throwable t) {
                        Log.e("CHAT", "Load failed", t);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webSocketService != null) {
            webSocketService.disconnect();
        }
    }
}

