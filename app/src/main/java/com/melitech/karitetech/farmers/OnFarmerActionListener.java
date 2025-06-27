package com.melitech.karitetech.farmers;

import com.melitech.karitetech.model.Farmer;

public interface OnFarmerActionListener {
    void onEditFarmer(Farmer farmer);
    void onDeleteFarmer(Farmer farmer);
}
