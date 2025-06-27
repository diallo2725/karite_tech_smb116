package com.melitech.karitetech.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.melitech.karitetech.data.local.FarmerDao;
import com.melitech.karitetech.model.Farmer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FarmerViewModel extends ViewModel {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<List<Farmer>> farmersLiveData = new MutableLiveData<>();

    public FarmerViewModel() {
        loadFarmers();
    }

   public LiveData<List<Farmer>> getFarmersLiveData() {return farmersLiveData;}

    public void addFarmer(Farmer farmer,boolean isFromRemote) {
        executor.execute(() -> {
            FarmerDao.insertSingleFarmer(farmer,isFromRemote);
            loadFarmers();
        });
    }


    public void updateFarmer(Farmer farmer) {
        executor.execute(() -> {
            FarmerDao.updateFarmer(farmer);
            loadFarmers();
        });
    }


    public void deleteFarmer(Farmer farmer) {
        executor.execute(() -> {
            FarmerDao.deleteFarmer(farmer);
            loadFarmers();
        });
    }

    public void loadFarmers() {
        executor.execute(() -> {
            List<Farmer> list = FarmerDao.getLocalFarmers();
            farmersLiveData.postValue(list);
        });
    }

}
