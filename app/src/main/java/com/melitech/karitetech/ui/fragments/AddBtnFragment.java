package com.melitech.karitetech.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.melitech.karitetech.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddBtnFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddBtnFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_ICON = "icon";

    private String title;
    private int iconResId;
    private View.OnClickListener clickListener;

    public AddBtnFragment() {
        // Required empty public constructor
    }

    public static AddBtnFragment newInstance(String title, int iconResId, View.OnClickListener listener) {
        AddBtnFragment fragment = new AddBtnFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_ICON, iconResId);
        fragment.setArguments(args);
        fragment.setClickListener(listener);
        return fragment;
    }

    public void setClickListener(View.OnClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            iconResId = getArguments().getInt(ARG_ICON);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_btn, container, false);

        ImageView iconImage = view.findViewById(R.id.iconImage);
        TextView titleText = view.findViewById(R.id.title);
        View containerLayout = view.findViewById(R.id.container);

        iconImage.setImageResource(iconResId);
        titleText.setText(title);

        if (clickListener != null) {
            containerLayout.setOnClickListener(clickListener);
        }

        return view;
    }
}