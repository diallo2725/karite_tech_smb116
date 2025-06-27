package com.melitech.karitetech.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.melitech.karitetech.data.remote.RestClient;
import com.melitech.karitetech.data.remote.RetrofitConfig;
import com.melitech.karitetech.model.ApiResponse;
import com.melitech.karitetech.utils.SessionManager;

import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public abstract class BaseRepository {
    protected final String token;

    public BaseRepository(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        this.token = "Bearer " + sessionManager.getToken();
    }

    protected <T> RestClient createRestClient(Type type) {
        Retrofit retrofit = RetrofitConfig.getRetrofit(type);
        return retrofit.create(RestClient.class);
    }

    protected <T> void handleCall(Call<ApiResponse<T>> call, ApiCallback<T> callback) {
        call.enqueue(new Callback<ApiResponse<T>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<T>> call, @NonNull Response<ApiResponse<T>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Erreur de l'API ou réponse invalide.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<T>> call, @NonNull Throwable t) {
                String errorMessage;
                if (t instanceof UnknownHostException) {
                    errorMessage = "Pas de connexion internet.";
                } else if (t instanceof SocketTimeoutException) {
                    errorMessage = "Délai d'attente dépassé.";
                } else {
                    errorMessage = "Erreur réseau : " + t.getMessage();
                }
                callback.onFailure(errorMessage);
            }
        });
    }

    public interface ApiCallback<T> {
        void onSuccess(ApiResponse<T> response);
        void onFailure(String errorMessage);
    }
}

