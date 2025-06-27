package com.melitech.karitetech.offers;

import com.melitech.karitetech.model.RequestOffer;

public interface OnOfferActionListener {
    void onEditOffer(RequestOffer offer);
    void onDeleteOffer(RequestOffer offer);
    void onScanOffer(RequestOffer offer);
}
