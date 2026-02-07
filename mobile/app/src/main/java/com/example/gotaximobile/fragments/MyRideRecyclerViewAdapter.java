package com.example.gotaximobile.fragments;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotaximobile.R;
import com.example.gotaximobile.databinding.FragmentItemBinding;
import com.example.gotaximobile.models.Ride;

import java.util.List;
import java.util.function.Consumer;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Ride}.
 */
public class MyRideRecyclerViewAdapter extends RecyclerView.Adapter<MyRideRecyclerViewAdapter.ViewHolder> {

    private final List<Ride> mValues;
    private final Consumer<Ride> handler;

    public MyRideRecyclerViewAdapter(List<Ride> items, Consumer<Ride> handler) {
        mValues = items;
        this.handler = handler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false),
                handler);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Ride currentRide = mValues.get(position);
        holder.mContentView.setText(currentRide.getInfoForList());
        holder.mRightSideView.setText(currentRide.getFormatedPrice());

        // Animation
        holder.itemView.setAlpha(0f);
        holder.itemView.animate().alpha(1f).setDuration(300).start();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mRightSideView;
        public Ride mItem;

        public ViewHolder(FragmentItemBinding binding, Consumer<Ride> handler) {
            super(binding.getRoot());
            mContentView = binding.content;
            mRightSideView = binding.rightSide;

            itemView.setBackgroundResource(R.drawable.item_background_selector);
            itemView.setClickable(true);
            itemView.setOnClickListener(v -> {
                v.setPressed(true);
                handler.accept(mItem);

            });
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + mRightSideView.getText() + "'";
        }
    }
}