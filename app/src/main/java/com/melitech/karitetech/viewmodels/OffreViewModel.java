package com.melitech.karitetech.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.melitech.karitetech.data.local.FarmerDao;
import com.melitech.karitetech.data.local.OfferDao;
import com.melitech.karitetech.model.Farmer;
import com.melitech.karitetech.model.RequestOffer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OffreViewModel extends ViewModel {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<List<RequestOffer>> offersLiveData = new MutableLiveData<>();


    public LiveData<List<RequestOffer>> getOffersLiveData() {
        return offersLiveData;
    }
    public OffreViewModel() {
        loadOffers();
    }
    public void loadOffers() {
        executor.execute(() -> {
            List<RequestOffer> list = OfferDao.getLocalOffers();
            offersLiveData.postValue(list);
        });
    }

    public void addOffer(RequestOffer offer) {
        executor.execute(() -> {
            OfferDao.insertLocalOffer(offer);
            loadOffers();
        });
    }

    public void updateOffer(RequestOffer offer) {
        executor.execute(() -> {
            OfferDao.updateOffer(offer);
            loadOffers();
        });
    }

    public void deleteOffer(RequestOffer offer) {
        executor.execute(() -> {
            OfferDao.deleteOffer(offer);
            loadOffers();
        });
    }


}
