package com.melitech.karitetech.farmers;

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
import com.melitech.karitetech.model.Farmer;
import com.melitech.karitetech.utils.DateConverter;
import com.melitech.karitetech.utils.ImageUtils;
import java.util.List;

public class FarmerAdapter extends RecyclerView.Adapter<FarmerAdapter.FarmerViewHolder>  {
    Context context;
    List<Farmer> farmers;
    private OnFarmerActionListener onFarmerActionListener;



    FarmerAdapter(Context context,List<Farmer> farmers,OnFarmerActionListener onFarmerActionListener){
        this.context = context;
        this.farmers = farmers;
        this.onFarmerActionListener = onFarmerActionListener;
    }
    @NonNull
    @Override
    public FarmerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.farmer_item,parent,false);
       return new FarmerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FarmerViewHolder holder, int position) {
            Farmer farmer = farmers.get(position);
            holder.fullname.setText(farmer.getFullname());
            holder.farmerPhone.setText(farmer.getPhone());
            if(farmer.getDate_of_birth() != null) {
                holder.dateOfBirth.setText("Date de naissance : " + DateConverter.DayMonthYear(farmer.getDate_of_birth()));
            }
            holder.sexeFarmer.setText("Sexe : "+farmer.getSexe());
            holder.activityFarmer.setText("Activité : "+farmer.getJob());
            holder.editBtn.setOnClickListener(v -> onFarmerActionListener.onEditFarmer(farmer));
            holder.deleteBtn.setOnClickListener(v -> onFarmerActionListener.onDeleteFarmer(farmer));

        if(farmer.getPicture() != null) {
            ImageUtils.loadImageIfExists(farmer.getPicture(), holder.farmerPhoto, 200, 200);
        }
    }

    public class FarmerViewHolder extends RecyclerView.ViewHolder {
        ImageView farmerPhoto;
        TextView fullname,farmerPhone,dateOfBirth,sexeFarmer,activityFarmer;
        TextView editBtn,deleteBtn;
        @SuppressLint("WrongViewCast")
        public FarmerViewHolder(@NonNull View itemView) {
            super(itemView);
            farmerPhoto = itemView.findViewById(R.id.farmerPhoto);
            fullname = itemView.findViewById(R.id.fullname);
            farmerPhone = itemView.findViewById(R.id.farmerPhone);
            dateOfBirth = itemView.findViewById(R.id.dateOfBirth);
            sexeFarmer = itemView.findViewById(R.id.sexeFarmer);
            activityFarmer = itemView.findViewById(R.id.activityFarmer);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }

    @Override
    public int getItemCount() {
        return farmers.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Farmer> newFarmers) {
        this.farmers.clear();
        this.farmers.addAll(newFarmers);
        notifyDataSetChanged();
    }



}
