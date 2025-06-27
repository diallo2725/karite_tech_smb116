package com.melitech.karitetech.repository;

import android.util.Log;

import androidx.annotation.NonNull;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.melitech.karitetech.data.remote.RestClient;
import com.melitech.karitetech.data.remote.RetrofitConfig;
import com.melitech.karitetech.model.ApiResponse;
import com.melitech.karitetech.model.Farmer;
import com.melitech.karitetech.model.RequestLogin;
import com.melitech.karitetech.model.ResponseError;
import com.melitech.karitetech.model.LoginResponse;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginRepository {
    private final RestClient restClient;
    public LoginRepository() {
        Type responseLogin = new TypeToken<LoginResponse>() {}.getType();
        Retrofit retrofit = RetrofitConfig.getRetrofit(responseLogin);
        this.restClient = retrofit.create(RestClient.class);
    }

    public void login(ApiCallback callback, RequestLogin requestLogin) {
        Call<ApiResponse<LoginResponse>> call = restClient.login(requestLogin);
        call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<LoginResponse>> call, @NonNull Response<ApiResponse<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if(response.body().isStatus()){
                        callback.onSuccess(response.body());
                    }else{
                        callback.onFailure(response.body().getMessage());
                    }
                } else {
                    callback.onFailure("Erreur inconnue");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<LoginResponse>> call, @NonNull Throwable t) {
                String errorMessage;
                if (t instanceof java.net.UnknownHostException) {
                    errorMessage = "Pas de connexion internet.";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMessage = "Délai d'attente dépassé. Vérifiez votre connexion.";
                } else if (t instanceof java.io.IOException) {
                    errorMessage = "Erreur réseau. Veuillez réessayer.";
                } else {
                    errorMessage = "Erreur inconnue : " + t.getMessage();
                }
                Log.e("LoginRepository", errorMessage, t);
                callback.onFailure(errorMessage);
            }
        });
    }


    public interface ApiCallback {
        void onSuccess(ApiResponse<LoginResponse> response);
        void onFailure(String errorMessage);
    }
}
