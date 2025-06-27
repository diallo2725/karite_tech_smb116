package com.melitech.karitetech.repository;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.melitech.karitetech.data.remote.RestClient;
import com.melitech.karitetech.data.remote.RetrofitConfig;
import com.melitech.karitetech.model.ApiResponse;
import com.melitech.karitetech.model.Farmer;
import com.melitech.karitetech.model.SyncResult;
import com.melitech.karitetech.utils.ImageUtils;
import com.melitech.karitetech.utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

public class FarmerRepository extends  BaseRepository{

    private final RestClient restClient;


    public FarmerRepository(Context context) {
        super(context);
        Type farmerListType = new TypeToken<List<Farmer>>() {}.getType();
        Retrofit retrofit = RetrofitConfig.getRetrofit(farmerListType);
        this.restClient = retrofit.create(RestClient.class);
    }

    public void getFarmers(ApiCallback<List<Farmer>> callback) {
        Call<ApiResponse<List<Farmer>>> call = restClient.getFarmers(token);
        handleCall(call, callback);
    }

    public void addFarmer(Farmer farmer,
                          MultipartBody.Part picturePart,
                          AddFarmerCallback callback,
                          Context context) {
        try {
            SessionManager sessionManager = new SessionManager(context);
            String token = "Bearer " + sessionManager.getToken();
            // Préparer les champs
            RequestBody fullname = RequestBody.create(farmer.getFullname(), MediaType.parse("text/plain"));
            RequestBody dateOfBirth = RequestBody.create(farmer.getDate_of_birth(), MediaType.parse("text/plain"));
            RequestBody locality = RequestBody.create(farmer.getLocality(), MediaType.parse("text/plain"));
            RequestBody sexe = RequestBody.create(farmer.getSexe(), MediaType.parse("text/plain"));
            RequestBody job = RequestBody.create(farmer.getJob(), MediaType.parse("text/plain"));
            RequestBody phone = RequestBody.create(farmer.getPhone(), MediaType.parse("text/plain"));
            RequestBody isUpdated = RequestBody.create(String.valueOf(farmer.getIsUpdated()), MediaType.parse("text/plain"));

            Call<ApiResponse<Farmer>> call = restClient.addFarmer(
                    fullname, dateOfBirth, locality, sexe, job, phone, isUpdated, picturePart, token
            );

            call.enqueue(new Callback<ApiResponse<Farmer>>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse<Farmer>> call, @NonNull Response<ApiResponse<Farmer>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<Farmer> apiResponse = response.body();
                        if (apiResponse.isStatus()) {
                            callback.onSuccess(apiResponse);
                        } else {
                            Log.w("Sync Failure", apiResponse.getMessage());
                            callback.onFailure(apiResponse.getMessage());
                        }
                    } else {
                        String message = "Erreur API : " + response.code() + " - " + response.body().getMessage();
                        Log.e("API Response Error", message);
                        callback.onFailure(message);
                    }

                    // Broadcast de fin
                   // Intent intent = new Intent("SYNC_COMPLETED");
                    //intent.putExtra("success", response.isSuccessful());
                    //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse<Farmer>> call, @NonNull Throwable t) {
                    String errorMessage;
                    if (t instanceof UnknownHostException) {
                        errorMessage = "Pas de connexion internet.";
                    } else if (t instanceof SocketTimeoutException) {
                        errorMessage = "Délai d'attente dépassé. Vérifiez votre connexion.";
                    } else if (t instanceof IOException) {
                        errorMessage = "Erreur réseau. Veuillez réessayer.";
                    } else {
                        errorMessage = "Erreur inconnue : " + t.getMessage();
                    }

                    Log.e("FarmerRepository", errorMessage, t);
                    callback.onFailure(errorMessage);

                    // Broadcast avec échec
                    Intent intent = new Intent("SYNC_COMPLETED");
                    intent.putExtra("success", false);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });

        } catch (Exception e) {
            String errorMessage = "Erreur lors de la préparation des données : " + e.getMessage();
            Log.e("FarmerRepository", errorMessage, e);
            callback.onFailure(errorMessage);
        }
    }


    public void synchronizeFarmers(List<Farmer> farmers, Context context, SyncCallback callback) {
        try {
            Gson gson = new Gson();
            String farmersJson = gson.toJson(farmers);
            RequestBody farmersBody = RequestBody.create(
                    farmersJson,
                    MediaType.parse("application/json")
            );

            List<MultipartBody.Part> imageParts = new ArrayList<>();
            for (int i = 0; i < farmers.size(); i++) {
                Farmer farmer = farmers.get(i);
                if (farmer.getPicture() != null && !farmer.getPicture().isEmpty()) {
                    File file = new File(farmer.getPicture());
                    if (file.exists()) {
                        MultipartBody.Part photoPart = ImageUtils.compressAndPrepareFile("images[" + i + "]", farmer.getPicture());
                        imageParts.add(photoPart);
                    }
                }
            }
            String token = "Bearer " + new SessionManager(context).getToken();
            restClient.syncFarmers(farmersBody, imageParts, token).enqueue(new Callback<ApiResponse<SyncResult>>() {
                @Override
                public void onResponse(Call<ApiResponse<SyncResult>> call, Response<ApiResponse<SyncResult>> response) {
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
                public void onFailure(Call<ApiResponse<SyncResult>> call, Throwable t) {
                    callback.onFailure("Erreur de synchronisation : " + t.getMessage());
                }
            });

        } catch (Exception e) {
            callback.onFailure("Erreur lors de la préparation des données : " + e.getMessage());
        }
    }





    public interface AddFarmerCallback {
        boolean onSuccess(ApiResponse<Farmer> response);
        boolean onFailure(String errorMessage);
    }

    public interface SyncCallback {
        void onSuccess(ApiResponse<SyncResult> response);
        void onFailure(String errorMessage);
    }

}


