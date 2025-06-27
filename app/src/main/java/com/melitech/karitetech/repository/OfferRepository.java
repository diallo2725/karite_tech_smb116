package com.melitech.karitetech.repository;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.melitech.karitetech.data.remote.RestClient;
import com.melitech.karitetech.data.remote.RetrofitConfig;
import com.melitech.karitetech.model.ApiResponse;
import com.melitech.karitetech.model.Farmer;
import com.melitech.karitetech.model.RequestOffer;
import com.melitech.karitetech.model.SyncResult;
import com.melitech.karitetech.utils.ImageUtils;
import com.melitech.karitetech.utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OfferRepository extends  BaseRepository{

    private final RestClient restClient;

    public OfferRepository(Context context) {
        super(context);
        Type offerListType = new TypeToken<RequestOffer>() {}.getType();
        Retrofit retrofit = RetrofitConfig.getRetrofit(offerListType);
        this.restClient = retrofit.create(RestClient.class);
    }

    public void createOffer(RequestOffer requestOffer, ApiCallback<RequestOffer> callback) {
        Call<ApiResponse<RequestOffer>> call = restClient.createOffer(requestOffer, token);
        handleCall(call, callback);
    }

    public void getOffers(ApiCallback<List<RequestOffer>> callback){
        Call<ApiResponse<List<RequestOffer>>> call = restClient.getOffers(token);
        handleCall(call, callback);
    }

    public void syncOffers(List<RequestOffer> offers,SyncCallback callback,Context context) {
        Gson gson = new Gson();
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("offers", offers); // Important !
        String offersJson = gson.toJson(wrapper); // JSON correct
        RequestBody offersBody = RequestBody.create(
                offersJson,
                MediaType.parse("application/json")
        );
        String token = "Bearer " + new SessionManager(context).getToken();
        restClient.syncOffers(offersBody, token).enqueue(new Callback<ApiResponse<SyncResult<RequestOffer>>>() {
                @Override
                public void onResponse(Call <ApiResponse<SyncResult<RequestOffer>>> call, @NonNull Response < ApiResponse < SyncResult<RequestOffer> >> response)
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
                public void onFailure (Call < ApiResponse < SyncResult<RequestOffer> >> call, Throwable t){
                    callback.onFailure("Erreur de synchronisation : " + t.getMessage());
            }
        });
    }





    public interface SyncCallback {
        void onSuccess(ApiResponse<SyncResult<RequestOffer>> response);
        void onFailure(String errorMessage);
    }













}


