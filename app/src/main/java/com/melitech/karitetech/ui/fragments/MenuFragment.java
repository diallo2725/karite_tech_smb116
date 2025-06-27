package com.melitech.karitetech.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.melitech.karitetech.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment {
   private static final String LIBELLES = "libelle";
   private static final String ICON = "icon";
   private static final String BACKGROUND = "background";
   private static final String TEXTCOLOR = "textcolor";
   private static final String COUNT = "count";

    private Class<?> targetActivity;

    public void setTargetActivity(Class<?> targetActivity) {
        this.targetActivity = targetActivity;
    }


   private String libelle;
   private int icon;
   private int background;
   private int textcolor;
   private int count;



    public MenuFragment() {}


    public static MenuFragment newInstance(String libelle, int icon, int background, int textcolor, int count) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(LIBELLES, libelle);
        args.putInt(ICON, icon);
        args.putInt(BACKGROUND, background);
        args.putInt(TEXTCOLOR, textcolor);
        args.putInt(COUNT, count);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }



    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_menu, container, false);
        if(getArguments() != null){
            libelle = getArguments().getString(LIBELLES);
            icon = getArguments().getInt(ICON);
            background = getArguments().getInt(BACKGROUND);
            textcolor = getArguments().getInt(TEXTCOLOR);
            count = getArguments().getInt(COUNT);
        }

        LinearLayout rootLayout = view.findViewById(R.id.rootLayout);
        ImageView iconView = view.findViewById(R.id.iconView);
        TextView libelleView = view.findViewById(R.id.libelleView);
        TextView countView = view.findViewById(R.id.countView);
        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.rounded_background);
        if (drawable != null) {
            drawable.setColor(background);
            rootLayout.setBackground(drawable);
        }
        iconView.setImageResource(icon);
        libelleView.setText(libelle);
        libelleView.setTextColor(textcolor);
        countView.setText(String.valueOf(count));

        rootLayout.setOnClickListener(v -> {
            if (targetActivity != null) {
                Intent intent = new Intent(requireContext(), targetActivity);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Aucune activité définie", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}