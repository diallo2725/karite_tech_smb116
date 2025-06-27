package com.melitech.karitetech;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class TableRowFragment extends Fragment {
    private static final String ARG_LABEL = "label";
    private static final String ARG_VALUE = "value";

    public static TableRowFragment newInstance(String label, String value) {
        TableRowFragment fragment = new TableRowFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LABEL, label);
        args.putString(ARG_VALUE, value);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table_row, container, false);

        TextView labelText = view.findViewById(R.id.labelText);
        TextView valueText = view.findViewById(R.id.valueText);

        if (getArguments() != null) {
            labelText.setText(getArguments().getString(ARG_LABEL));
            valueText.setText(getArguments().getString(ARG_VALUE));
        }

        return view;
    }
}
