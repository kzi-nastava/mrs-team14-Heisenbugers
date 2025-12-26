package com.example.gotaximobile.fragments.profile;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gotaximobile.R;

public class ProfileCardView extends FrameLayout {
    private TextView titleTv, valueTv;
    private ImageView iconIv;

    public ProfileCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.profile_card, this);

        titleTv = findViewById(R.id.component_title);
        valueTv = findViewById(R.id.component_value);
        iconIv = findViewById(R.id.component_icon);
    }

    public void setData(String title, String value, int iconResId) {
        titleTv.setText(title);
        valueTv.setText(value);
        iconIv.setImageResource(iconResId);
    }
}