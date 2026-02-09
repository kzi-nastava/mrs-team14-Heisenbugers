package com.example.gotaximobile.fragments.ride;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gotaximobile.R;

public class DuringRideFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_during_ride, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Note input and icon
        TextView noteLabel = view.findViewById(R.id.noteLabel);
        ImageButton noteButton = view.findViewById(R.id.noteButton);

        noteButton.setOnClickListener(v -> openModal());
        noteLabel.setOnClickListener(v -> openModal());

    }

    private void openModal() {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Add a note");

        final android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setHint("Enter your note...");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String note = input.getText().toString();
            // handle note, e.g., save or send to map overlay
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}
