package com.melitech.karitetech.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.melitech.karitetech.utils.NetworkMonitor;

public class NetworkViewModel extends ViewModel {

    private NetworkMonitor networkLiveData;

    public LiveData<Boolean> getNetworkStatus(Context context) {
        if (networkLiveData == null) {
            networkLiveData = new NetworkMonitor(context);
        }
        return networkLiveData;
    }
}
