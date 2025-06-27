package com.melitech.karitetech.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.melitech.karitetech.data.local.ParcDao;
import com.melitech.karitetech.model.Parc;

import java.util.List;
import java.util.concurrent.Executors;

public class ParcViewModel extends ViewModel {
    private final MutableLiveData<List<Parc>> parcsLiveData = new MutableLiveData<>();

    public ParcViewModel() {
        loadParcs();
    }

    public LiveData<List<Parc>> getParcsLiveData() {
        return parcsLiveData;
    }

    public void loadParcs() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Parc> parcs = ParcDao.getLocalParcs();
            parcsLiveData.postValue(parcs);
        });
    }

    public void addParc(Parc parc) {
        Executors.newSingleThreadExecutor().execute(() -> {
            ParcDao.ajouterParc(parc);
           loadParcs();// Recharge après ajout
        });
    }

    public void deleteParc(Parc parc) {
        Executors.newSingleThreadExecutor().execute(() -> {
            ParcDao.deleteParc(parc.getId());
           loadParcs();// Recharge après suppression
        });
    }

}

