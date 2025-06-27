package com.melitech.karitetech.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.melitech.karitetech.data.local.PurchaseDao;
import com.melitech.karitetech.model.Purchase;

import java.util.List;
import java.util.concurrent.Executors;

public class PurchaseViewModel extends ViewModel {
    private final MutableLiveData<List<Purchase>> purchasesLiveData = new MutableLiveData<>();

    public PurchaseViewModel() {
        loadPurchases();
    }

    public LiveData<List<Purchase>> getPurchasesLiveData() {
        return purchasesLiveData;
    }

    public void loadPurchases() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Purchase> purchases = PurchaseDao.getLocalPurchases();
            purchasesLiveData.postValue(purchases);
        });
    }

    public void addPurchase(Purchase purchase) {
        Executors.newSingleThreadExecutor().execute(() -> {
            PurchaseDao.ajouterPurchase(purchase);
            loadPurchases(); // Recharge après ajout
        });
    }

    public void deletePurchase(Purchase purchase) {
        Executors.newSingleThreadExecutor().execute(() -> {
            PurchaseDao.deletePurchase(purchase.getId());
            loadPurchases(); // Recharge après suppression
        });
    }

}

