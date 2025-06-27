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
import com.melitech.karitetech.model.Parc;
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

public class ParcRepository extends  BaseRepository{

    private final RestClient restClient;


    public ParcRepository(Context context) {
        super(context);
        Type parcListType = new TypeToken<List<Parc>>() {}.getType();
        Retrofit retrofit = RetrofitConfig.getRetrofit(parcListType);
        this.restClient = retrofit.create(RestClient.class);
    }

    public void getParcs(ApiCallback<List<Parc>> callback) {
        Call<ApiResponse<List<Parc>>> call = restClient.getParcs(token);
        handleCall(call, callback);
    }

    public void addParc(Parc parc,
                        AddParcCallback addParcCallback,
                        MultipartBody.Part pictureParc,
                        Context context) {
        try {
            SessionManager sessionManager = new SessionManager(context);
            String token = "Bearer " + sessionManager.getToken();
            // Préparer les champs
            RequestBody longeur = RequestBody.create(parc.getLongeur(), MediaType.parse("text/plain"));
            RequestBody largeur = RequestBody.create(parc.getLargeur(), MediaType.parse("text/plain"));
            RequestBody longitude = RequestBody.create(parc.getLongitude(), MediaType.parse("text/plain"));
            RequestBody latitude = RequestBody.create(parc.getLatitude(), MediaType.parse("text/plain"));
            Call<ApiResponse<Parc>> call = restClient.addParc(
                    longeur, largeur, longitude, latitude, pictureParc, token
            );

            call.enqueue(new Callback<ApiResponse<Parc>>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse<Parc>> call, @NonNull Response<ApiResponse<Parc>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<Parc> apiResponse = response.body();
                        if (apiResponse.isStatus()) {
                            addParcCallback.onSuccess(apiResponse);
                        } else {
                            Log.w("Sync Failure", apiResponse.getMessage());
                            addParcCallback.onFailure(apiResponse.getMessage());
                        }
                    } else {
                        String message = "Erreur API : " + response.code() + " - " + response.body().getMessage();
                        Log.e("API Response Error", message);
                        addParcCallback.onFailure(message);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse<Parc>> call, @NonNull Throwable t) {
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

                    Log.e("ParcRepository", errorMessage, t);
                    addParcCallback.onFailure(errorMessage);
                    // Broadcast avec échec
                    Intent intent = new Intent("SYNC_COMPLETED");
                    intent.putExtra("success", false);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });

        } catch (Exception e) {
            String errorMessage = "Erreur lors de la préparation des données : " + e.getMessage();
            Log.e("ParcRepository", errorMessage, e);
            addParcCallback.onFailure(errorMessage);
        }
    }


    public void synchronizeParcs(List<Parc> parcs, Context context, SyncCallback callback) {
        try {

            Gson gson = new Gson();
            String parcJson = gson.toJson(parcs);
            RequestBody parcsBody = RequestBody.create(
                    parcJson,
                    MediaType.parse("application/json")
            );

            List<MultipartBody.Part> imageParts = new ArrayList<>();
            for (int i = 0; i < parcs.size(); i++) {
                Parc parc = parcs.get(i);
                if (parc.getPhoto() != null && !parc.getPhoto().isEmpty()) {
                    File file = new File(parc.getPhoto());
                    if (file.exists()) {
                        MultipartBody.Part photoPart = ImageUtils.compressAndPrepareFile("images[" + i + "]", parc.getPhoto());
                        imageParts.add(photoPart);
                    }
                }
            }
            String token = "Bearer " + new SessionManager(context).getToken();
            restClient.synchroniseParcs(parcsBody, imageParts, token).enqueue(new Callback<ApiResponse<SyncResult>>() {
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





    public interface AddParcCallback {
         boolean onSuccess(ApiResponse<Parc> response);
         boolean onFailure(String errorMessage);
    }

    public interface SyncCallback {
        void onSuccess(ApiResponse<SyncResult> response);
        void onFailure(String errorMessage);
    }

}


