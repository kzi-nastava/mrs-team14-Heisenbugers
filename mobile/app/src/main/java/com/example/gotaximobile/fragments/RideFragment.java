package com.example.gotaximobile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.fragments.placeholder.PlaceholderHistoryList;

/**
 * A fragment representing a list of Items.
 */
public class RideFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private Button[] buttons;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RideFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        View targetView = view.findViewById(R.id.list);

        Button btnSortDate = view.findViewById(R.id.btnSortDate);
        Button btnSortPrice = view.findViewById(R.id.btnSortPrice);
        Button btnSortPlace = view.findViewById(R.id.btnSortPlace);

        btnSortDate.setSelected(true);

        buttons = new Button[]{btnSortDate, btnSortPrice, btnSortPlace};

        for (Button btn : buttons) {
            btn.setOnClickListener(v -> {
                selectButton(btn);
            });
        }

        getParentFragmentManager();

        // Set the adapter
        if (targetView instanceof RecyclerView) {
            Context context = targetView.getContext();
            RecyclerView recyclerView = (RecyclerView) targetView;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyRideRecyclerViewAdapter(PlaceholderHistoryList.ITEMS, ride -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("ride", ride);
                DriverHistoryOneRideFragment fragment = new DriverHistoryOneRideFragment();
                fragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }));
            DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(divider);
        }
        return view;
    }

    private void selectButton(Button selected) {
        for (Button b : buttons) {
            b.setSelected(b == selected);
        }
    }

}