package com.melitech.karitetech.offers;

import android.annotation.SuppressLint;
import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.melitech.karitetech.R;
import com.melitech.karitetech.data.local.CertificationDao;
import com.melitech.karitetech.data.local.PackagingDao;
import com.melitech.karitetech.model.Certification;
import com.melitech.karitetech.model.Packaging;
import com.melitech.karitetech.model.RequestOffer;
import com.melitech.karitetech.utils.AmountFormatter;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder>  {
    Context context;
    List<RequestOffer> offers;
    private OnOfferActionListener onOfferActionListener;

    OfferAdapter(Context context, List<RequestOffer> offers, OnOfferActionListener onOfferActionListener){
        this.context = context;
        this.offers = offers;
        this.onOfferActionListener = onOfferActionListener;
    }
    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_item,parent,false);
       return new OfferViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
             Certification certification =CertificationDao.findById(String.valueOf(offers.get(position).getCertification_id()));
             Packaging packaging = PackagingDao.findById(String.valueOf(offers.get(position).getPacking_id()));
             RequestOffer offer = offers.get(position);

            holder.weight.setText(String.valueOf(offer.getWeight()));
            String amount = AmountFormatter.formatSansDecimales(offer.getPrice());
            String quantity = offer.getPackingCount() +" " + (offer.getPackingCount()>1 ? packaging.getName()+"s" : packaging.getName());
            holder.price.setText(amount);
            holder.quantity.setText(quantity);
            holder.certification.setText(certification.getName());
            holder.packaging.setText(packaging.getName());
           // holder.remoteId.setText(String.valueOf(offer.getRemoteId()));
            if(offer.getOffer_identify() != null){
                holder.offerIdentity.setText(offer.getOffer_identify());
            }else {
                holder.offerIdentity.setVisibility(View.GONE);
                holder.identRow.setVisibility(View.GONE);
            }

            if(offer.getOffer_state() != null){
                switch (offer.getOffer_state()){
                    case "published":
                        holder.offerState.setText("Publiée");
                        break;
                    case "bought":
                        holder.offerState.setText("Vendue");
                        break;
                    default:
                        holder.offerState.setText("En attente");
                }
            }else {
                holder.offerState.setText("En attente");
            }
            holder.editBtn.setOnClickListener(v -> onOfferActionListener.onEditOffer(offer));
            holder.deleteBtn.setOnClickListener(v -> onOfferActionListener.onDeleteOffer(offer));
            holder.scanBtn.setOnClickListener(v -> onOfferActionListener.onScanOffer(offer));
    }

    public class OfferViewHolder extends RecyclerView.ViewHolder {

        TextView weight,price,quantity,certification,packaging,offerIdentity,offerState,remoteId;
        TextView editBtn,deleteBtn,scanBtn;
        TableRow identRow;
        @SuppressLint("WrongViewCast")
        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            weight = itemView.findViewById(R.id.weightOffer);
            price = itemView.findViewById(R.id.priceOffer);
            quantity = itemView.findViewById(R.id.quantityOffer);
            certification = itemView.findViewById(R.id.certificationOffer);
            packaging = itemView.findViewById(R.id.packagingOffer);
            offerIdentity = itemView.findViewById(R.id.offerIdentity);
            offerState = itemView.findViewById(R.id.stateOffre);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            scanBtn = itemView.findViewById(R.id.scanBtn);
            identRow = itemView.findViewById(R.id.identityRow);
            //remoteId= itemView.findViewById(R.id.remoteIdOffer);
        }
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    protected void updateData(List<RequestOffer> newOffers) {
        this.offers.clear();
        this.offers.addAll(newOffers);
        notifyDataSetChanged();
    }


}
