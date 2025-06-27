package com.melitech.karitetech.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.melitech.karitetech.data.remote.RestClient;
import com.melitech.karitetech.data.remote.RetrofitConfig;
import com.melitech.karitetech.model.ApiResponse;
import com.melitech.karitetech.model.Purchase;
import com.melitech.karitetech.model.SyncResult;
import com.melitech.karitetech.utils.SessionManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PurchaseRepository extends  BaseRepository{

    private final RestClient restClient;

    public PurchaseRepository(Context context) {
        super(context);
        Type offerListType = new TypeToken<Purchase>() {}.getType();
        Retrofit retrofit = RetrofitConfig.getRetrofit(offerListType);
        this.restClient = retrofit.create(RestClient.class);
    }

    public void makePurchase(Purchase achat, ApiCallback<Purchase> callback) {
        Call<ApiResponse<Purchase>> call = restClient.makePurchase(achat, token);
        handleCall(call, callback);
    }

    public void getPurchases(ApiCallback<List<Purchase>> callback){
        Call<ApiResponse<List<Purchase>>> call = restClient.getPurchases(token);
        handleCall(call, callback);
    }

    public void syncPurchases(List<Purchase> purchases,SyncCallback callback,Context context) {
        Gson gson = new Gson();
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("purchases", purchases); // Important !
        String offersJson = gson.toJson(wrapper); // JSON correct
        RequestBody offersBody = RequestBody.create(
                offersJson,
                MediaType.parse("application/json")
        );
        String token = "Bearer " + new SessionManager(context).getToken();
        restClient.syncPurchases(offersBody, token).enqueue(new Callback<ApiResponse<SyncResult<Purchase>>>() {
                @Override
                public void onResponse(Call <ApiResponse<SyncResult<Purchase>>> call, @NonNull Response < ApiResponse < SyncResult<Purchase> >> response)
                {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Erreur inconnue";
                            Log.e("SYNC_ERROR_BODY", errorBody); // Pour le debug
                            callback.onFailure(errorBody);
                        } catch (IOException e) {
                            callback.onFailure("Erreur lors de la lecture de l'erreur : " + e.getMessage());
                        }
                    }
                }

            @Override
                public void onFailure (Call < ApiResponse < SyncResult<Purchase> >> call, Throwable t){
                    callback.onFailure("Erreur de synchronisation : " + t.getMessage());
            }
        });
    }





    public interface SyncCallback {
        void onSuccess(ApiResponse<SyncResult<Purchase>> response);
        void onFailure(String errorMessage);
    }













}


