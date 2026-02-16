package com.example.gotaximobile.fragments.admin.chat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.fragments.chat.ChatFragment;
import com.example.gotaximobile.models.Chat;
import com.example.gotaximobile.network.RetrofitClient;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private MaterialAutoCompleteTextView etSearch;

    private AdminChatAdapter adapter;

    private List<Chat> list = new ArrayList<>();
    private List<Chat> filteredList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_chats, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        etSearch = view.findViewById(R.id.etSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminChatAdapter(filteredList, this::openChat);
        recyclerView.setAdapter(adapter);

        setupSearch();
        loadChats();

        return view;
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void loadChats() {
        RetrofitClient.chatApi(requireContext()).getAllChats()
                .enqueue(new Callback<List<Chat>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Chat>> call,
                                           @NonNull Response<List<Chat>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            list = response.body();
                            applyFilter("");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Chat>> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "Failed to load chats", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void applyFilter(String term) {
        filteredList.clear();

        for (Chat c : list) {
            String fullName = c.driver.firstName + " " + c.driver.lastName;
            if (fullName.toLowerCase().contains(term.toLowerCase())) {
                filteredList.add(c);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void openChat(Chat chat) {
        ChatFragment fragment = ChatFragment.newInstance(chat.chatId);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}

