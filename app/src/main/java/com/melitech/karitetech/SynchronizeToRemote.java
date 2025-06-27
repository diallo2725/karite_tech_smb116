package com.melitech.karitetech;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.melitech.karitetech.data.local.FarmerDao;
import com.melitech.karitetech.data.local.OfferDao;
import com.melitech.karitetech.data.local.ParcDao;
import com.melitech.karitetech.data.local.PurchaseDao;
import com.melitech.karitetech.model.ApiResponse;
import com.melitech.karitetech.model.Farmer;
import com.melitech.karitetech.model.Parc;
import com.melitech.karitetech.model.Purchase;
import com.melitech.karitetech.model.RequestOffer;
import com.melitech.karitetech.model.SyncResult;
import com.melitech.karitetech.repository.FarmerRepository;
import com.melitech.karitetech.repository.OfferRepository;
import com.melitech.karitetech.repository.ParcRepository;
import com.melitech.karitetech.repository.PurchaseRepository;
import com.melitech.karitetech.utils.ApiErrorParser;
import com.melitech.karitetech.viewmodels.HomeViewModel;

import java.util.List;

public class SynchronizeToRemote{
    Context context;
    HomeViewModel homeViewModel;
    private ProgressDialog progressDialog;
    private int totalTasks = 4; // farmers + offers + purchases + parcs
    private int completedTasks = 0;
    private int totalSuccess = 0;
    private int totalFailed = 0;


    public SynchronizeToRemote(Context context, HomeViewModel homeViewModel) {
        this.context = context;
        this.homeViewModel = homeViewModel;
    }

    public void  synchronizeFarmer(){
        FarmerRepository farmerRepository = new FarmerRepository(context);
        List<Farmer> farmersToSync = FarmerDao.getLocalFarmers();
        isEmptyOrNull(farmersToSync, "Aucun producteur à synchroniser");
        farmerRepository.synchronizeFarmers(farmersToSync, context, new FarmerRepository.SyncCallback() {
            @Override
            public void onSuccess(ApiResponse<SyncResult> response) {
                if(response.isStatus()) {
                    List<Farmer> farmers = FarmerDao.getLocalFarmers();
                    for (Farmer farmer : farmers) {
                        FarmerDao.updateRemoteField(farmer);
                    }
                    if(homeViewModel!=null){
                        homeViewModel.getFarmerCount();
                    }
                    updateProgress(true);
                }else {
                    updateProgress(false);
                }
            }
            @Override
            public void onFailure(String errorMessage) {
                updateProgress(false);
                Log.e("SYNC", "Échec : " + ApiErrorParser.parse(errorMessage));
            }
        });
    }

    public void saveRemoteFarmersOnLocal() {
        FarmerRepository farmerRepository = new FarmerRepository(context);
        farmerRepository.getFarmers(new FarmerRepository.ApiCallback() {
            @Override
            public void onSuccess(ApiResponse response) {
                if (response.isStatus()) {
                    List<Farmer> farmers = (List<Farmer>) response.getData();
                    FarmerDao.insertFarmer(farmers);
                    Log.d("SYNC", "Succès : " + response.getMessage());
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(ApiErrorParser.parse(errorMessage))
                        .setTitle("Error")
                        .setPositiveButton("OK", (dialog, id) -> {
                            dialog.dismiss();
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                Log.e("SYNC", "Échec : " + ApiErrorParser.parse(errorMessage));
            }
        });


    }

    public void synchronizeOffer(){
        OfferRepository offerRepository = new OfferRepository(context);
        List<RequestOffer> offersToSync = OfferDao.getLocalOffers();
        isEmptyOrNull(offersToSync, "Aucune offre à synchroniser");
        offerRepository.syncOffers(offersToSync, new OfferRepository.SyncCallback() {
            @Override
            public void onSuccess(ApiResponse<SyncResult<RequestOffer>> response) {
                if(response.isStatus()){
                    List<SyncResult.ResultItem<RequestOffer>> items = response.getData().getResults();
                    for (SyncResult.ResultItem<RequestOffer> item : items) {
                        RequestOffer offer = item.getData();
                        OfferDao.insertLocalOffer(offer);
                    }
                    for (RequestOffer offer : offersToSync) {
                        OfferDao.deleteOffer(offer);
                    }
                    if(homeViewModel!=null){
                        homeViewModel.getOfferCount();
                    }
                    updateProgress(true);
                }else{
                    updateProgress(false);

                }
            }
            @Override
            public void onFailure(String errorMessage) {
                updateProgress(false);
                Log.e("SYN OFFER", "Échec : " + ApiErrorParser.parse(errorMessage));

            }
        },context);

    }

    public void synchronizePurchase(){
        PurchaseRepository purchaseRepository = new PurchaseRepository(context);
        List<Purchase> purchases = PurchaseDao.getLocalPurchases();
        isEmptyOrNull(purchases, "Aucun achat à synchroniser");
        purchaseRepository.syncPurchases(purchases, new PurchaseRepository.SyncCallback() {
            @Override
            public void onSuccess(ApiResponse<SyncResult<Purchase>> response) {
                if(response.isStatus()){
                    for (Purchase purchase : purchases) {
                        PurchaseDao.deletePurchase(purchase.getId());
                    }
                    if(homeViewModel!=null){
                        homeViewModel.getAchatCount();
                    }
                    updateProgress(true);
                }else{
                    updateProgress(false);
                }
            }
            @Override
            public void onFailure(String errorMessage) {
                Log.e("SYNC PURCHASE", "Échec : " + ApiErrorParser.parse(errorMessage));
                updateProgress(false);
            }
        },context);

    }

    public void synchronizeParcs(){
        ParcRepository parcRepository = new ParcRepository(context);
        List<Parc> parcsToSync = ParcDao.getLocalParcs();
        isEmptyOrNull(parcsToSync, "Aucun parc à synchroniser");
        parcRepository.synchronizeParcs(parcsToSync, context, new ParcRepository.SyncCallback() {

            @Override
            public void onSuccess(ApiResponse<SyncResult> response) {
                if(response.isStatus()){
                    List<SyncResult.ResultItem<Parc>> items = response.getData().getResults();
                    if(homeViewModel!=null){
                        homeViewModel.getParcCount();
                    }
                    updateProgress(true);
                }else{
                    updateProgress(false);
                }
            }
            @Override
            public void onFailure(String errorMessage) {
                Log.e("SYNC PARCS", "Échec : " + ApiErrorParser.parse(errorMessage));
                updateProgress(false);
            }
        });
    }

    private void showProgress() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Synchronisation");
        progressDialog.setMessage("Synchronisation en cours...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(totalTasks);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void updateProgress(boolean success) {
        completedTasks++;
        if (success) totalSuccess++;
        else totalFailed++;

        progressDialog.setProgress(completedTasks);

        if (completedTasks == totalTasks) {
            progressDialog.dismiss();
            showSummaryDialog();
        }
    }

    private void showSummaryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Résumé de la synchronisation")
                .setMessage("Succès : " + totalSuccess + "\nÉchecs : " + totalFailed)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void startSync() {
        showProgress();
        synchronizeFarmer();
        synchronizeParcs();
        synchronizeOffer();
        synchronizePurchase();
    }


    private <T> boolean isEmptyOrNull(List<T> list, String logMessage) {
        if (list == null || list.isEmpty()) {
            Log.d("SYNC", logMessage);
            return true;
        }
        return false;
    }



}