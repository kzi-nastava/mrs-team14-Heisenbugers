package com.example.gotaximobile.fragments.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gotaximobile.R;
import com.example.gotaximobile.adapters.UserAdapter;
import com.example.gotaximobile.models.dtos.BlockableUserDTO;
import com.example.gotaximobile.models.dtos.GetProfileDTO;
import com.example.gotaximobile.network.RetrofitClient;
import com.example.gotaximobile.network.UserService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageUsersFragment extends Fragment {
    private UserAdapter adapter;
    private List<BlockableUserDTO> allUsers = new ArrayList<>();
    private String currentQuery = "";
    private String currentRole = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_manage_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = view.findViewById(R.id.recyclerUsers);

        rv.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));

        adapter = new UserAdapter((user, position) -> {
            if (user.blocked) {
                showUnblockDialog(user, position);
            } else {
                showBlockDialog(user, position);
            }
        });
        rv.setAdapter(adapter);
        loadUsers();

        MaterialToolbar toolbar = view.findViewById(R.id.topAppBarManageUsers);
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        AutoCompleteTextView roleDropdown = view.findViewById(R.id.actvRoleFilter);
        TextInputEditText etSearch = view.findViewById(R.id.etSearch);
        String[] roles = {"All", "PASSENGER", "DRIVER"};
        roleDropdown.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, roles));
        roleDropdown.setText("All", false);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString().toLowerCase();
                applyFilters();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        roleDropdown.setOnItemClickListener((parent, v, position, id) -> {
            currentRole = roles[position];
            applyFilters();
        });
    }

    private void applyFilters() {
        List<BlockableUserDTO> filteredList = new ArrayList<>();
        for (BlockableUserDTO user : allUsers) {
            boolean matchesQuery = user.firstName.toLowerCase().contains(currentQuery) ||
                    user.lastName.toLowerCase().contains(currentQuery) ||
                    user.email.toLowerCase().contains(currentQuery);

            boolean matchesRole = currentRole.equals("All") || user.role.equalsIgnoreCase(currentRole);

            if (matchesQuery && matchesRole) {
                filteredList.add(user);
            }
        }
        adapter.submitList(filteredList);
    }

    private void showBlockDialog(BlockableUserDTO user, int position) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_block_reason, null);
        TextInputLayout tilReason = dialogView.findViewById(R.id.tilBlockReason);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Block " + user.firstName + " " + user.lastName)
                .setView(dialogView)
                .setPositiveButton("Confirm", null) // Set to null first to override auto-dismiss
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                String reason = tilReason.getEditText().getText().toString().trim();

                tilReason.setError(null);
                executeBlockStatusChange(user, true, reason, position);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void showUnblockDialog(BlockableUserDTO user, int position) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Unblock User")
                .setMessage("Are you sure you want to unblock " + user.firstName + " " + user.lastName + "?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    executeBlockStatusChange(user, false, null, position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void executeBlockStatusChange(BlockableUserDTO user, boolean block, String reason, int pos) {
        if(block) {
            RetrofitClient.userService(getContext()).blockUser(String.valueOf(user.id), reason).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        user.blocked = true;
                        adapter.notifyItemChanged(pos);
                        Toast.makeText(getContext(), "User " + user.email + " blocked!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("API_ERROR", "Response failed: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.e("NETWORK_ERROR", Objects.requireNonNull(t.getMessage()));
                }
            });
        }else{
            RetrofitClient.userService(getContext()).unblockUser(String.valueOf(user.id)).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        user.blocked = false;
                        adapter.notifyItemChanged(pos);
                        Toast.makeText(getContext(), "User " + user.email + " unblocked!", Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("API_ERROR", "Response failed: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.e("NETWORK_ERROR", Objects.requireNonNull(t.getMessage()));
                }
            });
        }
    }

    public void loadUsers(){
        RetrofitClient.userService(getContext()).getBlockableUsers().enqueue(new Callback<List<BlockableUserDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<BlockableUserDTO>> call, @NonNull Response<List<BlockableUserDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BlockableUserDTO> users = response.body();
                    allUsers = users;
                    adapter.submitList(users);
                } else {
                    Log.e("API_ERROR", "Response failed: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<BlockableUserDTO>> call, @NonNull Throwable t) {
                Log.e("NETWORK_ERROR", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

}