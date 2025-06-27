package com.melitech.karitetech.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.melitech.karitetech.data.local.PurchaseDao;
import com.melitech.karitetech.data.local.FarmerDao;
import com.melitech.karitetech.data.local.OfferDao;
import com.melitech.karitetech.data.local.ParcDao;


public class HomeViewModel extends ViewModel {
    private final MutableLiveData<Integer> farmerCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> offerCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> achatCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> parcCount = new MutableLiveData<>();

    public HomeViewModel() {
        refreshAllCounts();
    }

    public LiveData<Integer> getFarmerCount() {
        return farmerCount;
    }

    public LiveData<Integer> getOfferCount() {
        return offerCount;
    }

    public LiveData<Integer> getAchatCount() {
        return achatCount;
    }

    public LiveData<Integer> getParcCount() {
        return parcCount;
    }


    public void refreshAllCounts() {
        farmerCount.setValue(FarmerDao.getFarmerCount());
        offerCount.setValue(OfferDao.getLocalOffers().size());
        achatCount.setValue(PurchaseDao.getLocalPurchases().size()); // ou getTotalAchatCount()
        parcCount.setValue(ParcDao.getAllParcs().size()); // ou getTotalParcCount()
    }

}
