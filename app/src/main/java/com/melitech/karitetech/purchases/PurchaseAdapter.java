package com.melitech.karitetech.purchases;

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
import com.melitech.karitetech.model.Purchase;
import com.melitech.karitetech.utils.AmountFormatter;
import com.melitech.karitetech.utils.ImageUtils;

import java.util.List;

public class  PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.PurchaseViewHolder>  {
    Context context;
    List<Purchase> purchases;
    PurchaseActionListener onPurchaseActionListener;

    PurchaseAdapter(Context context, List<Purchase> purchases, PurchaseActionListener onPurchaseActionListener){
        this.context = context;
        this.purchases = purchases;
        this.onPurchaseActionListener = onPurchaseActionListener;
    }
    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.achat_item,parent,false);
       return new PurchaseViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {

            Purchase achat = purchases.get(position);
            holder.weight.setText(String.valueOf(achat.getWeight())+" Kg");
            holder.price.setText(AmountFormatter.formatSansDecimales(Double.parseDouble(achat.getPrice())));
            holder.quality.setText(String.valueOf(achat.getQuality()));
            holder.totalAmount.setText(AmountFormatter.formatSansDecimales(Double.parseDouble(achat.getAmount())));
            holder.typePurchase.setText(achat.getPaymentMethod());
            holder.fullname.setText(achat.getFullname());
            holder.farmerPhone.setText(achat.getPhone());
            if(achat.getCash() != null) {
              //  holder.cashAmount.setText(AmountFormatter.formatSansDecimales(Double.parseDouble(achat.getCash())));
            }
            if(holder.farmerPhoto != null && achat.getPicture() != null) {
                ImageUtils.loadImageIfExists(achat.getPicture(), holder.farmerPhoto, 200, 200);
            }

    }

    public class PurchaseViewHolder extends RecyclerView.ViewHolder {

        TextView weight,price,quality,typePurchase,totalAmount,cashAmount;
        TextView editBtn,deleteBtn,scanBtn, fullname,farmerPhone,activityFarmer;
        ImageView farmerPhoto;
        @SuppressLint("WrongViewCast")
        public PurchaseViewHolder(@NonNull View itemView) {
            super(itemView);
            weight = itemView.findViewById(R.id.weight);
            price = itemView.findViewById(R.id.price);
            quality = itemView.findViewById(R.id.quality);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            typePurchase = itemView.findViewById(R.id.paymentMethod);
            fullname = itemView.findViewById(R.id.fullname);
            farmerPhone = itemView.findViewById(R.id.farmerPhone);
            farmerPhoto = itemView.findViewById(R.id.farmerPhoto);
            cashAmount = itemView.findViewById(R.id.especeAmount);
        }
    }

    @Override
    public int getItemCount() {
        return purchases.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    protected void updateData(List<Purchase> achats) {
        this.purchases.clear();
        this.purchases.addAll(achats);
        notifyDataSetChanged();
    }


}
