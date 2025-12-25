package com.example.gotaximobile.fragments;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.databinding.FragmentItemBinding;
import com.example.gotaximobile.fragments.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.gotaximobile.models.Ride;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRideRecyclerViewAdapter extends RecyclerView.Adapter<MyRideRecyclerViewAdapter.ViewHolder> {

    private final List<Ride> mValues;

    public MyRideRecyclerViewAdapter(List<Ride> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        // holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).getInfoForList());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // public final TextView mIdView;
        public final TextView mContentView;
        public Ride mItem;

        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            // mIdView = binding.itemNumber;
            mContentView = binding.content;

            itemView.setBackgroundResource(R.drawable.item_background_selector);
            itemView.setClickable(true);
            itemView.setOnClickListener(v -> {
                v.setPressed(true);
                Toast.makeText(v.getContext(), mItem.getInfoForList(), Toast.LENGTH_SHORT).show();
            });
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}