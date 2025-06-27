package com.melitech.karitetech.parcs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.melitech.karitetech.R;
import com.melitech.karitetech.model.Parc;
import com.melitech.karitetech.utils.ImageUtils;

import java.util.List;

public class ParcAdapter extends RecyclerView.Adapter<ParcAdapter.ParcViewHolder>  {
    Context context;
    List<Parc> parcs;
    OnParcActionListener onParcActionListener;

    ParcAdapter(Context context, List<Parc> parcs, OnParcActionListener onParcActionListener){
        this.context = context;
        this.parcs = parcs;
        this.onParcActionListener = onParcActionListener;
    }
    @NonNull
    @Override
    public ParcViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parc_item,parent,false);
       return new ParcViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ParcViewHolder holder, int position) {
            Parc parc = parcs.get(position);
            holder.latParc.setText(String.valueOf(parc.getLatitude()));
            holder.lngParc.setText(String.valueOf(parc.getLongitude()));
            holder.largeurParc.setText(String.valueOf(parc.getLargeur()));
            holder.longeurParc.setText(String.valueOf(parc.getLongeur()));
            if(holder.parcPhoto != null && parc.getPhoto() != null) {
                ImageUtils.loadImageIfExists(parc.getPhoto(), holder.parcPhoto, 200, 200);
            }

    }

    public class ParcViewHolder extends RecyclerView.ViewHolder {

        TextView latParc,lngParc,largeurParc,longeurParc;
        ImageView parcPhoto;
        @SuppressLint("WrongViewCast")
        public ParcViewHolder(@NonNull View itemView) {
            super(itemView);
            latParc = itemView.findViewById(R.id.latParc);
            lngParc = itemView.findViewById(R.id.lngParc);
            largeurParc = itemView.findViewById(R.id.largeurParc);
            longeurParc = itemView.findViewById(R.id.longeurParc);
            parcPhoto = itemView.findViewById(R.id.parcImage);
        }
    }

    @Override
    public int getItemCount() {
        return parcs.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    protected void updateData(List<Parc> parc) {
        this.parcs.clear();
        this.parcs.addAll(parc);
        notifyDataSetChanged();
    }


}
