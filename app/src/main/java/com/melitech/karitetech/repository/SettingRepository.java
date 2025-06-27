package com.melitech.karitetech.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.melitech.karitetech.data.remote.RestClient;
import com.melitech.karitetech.data.remote.RetrofitConfig;
import com.melitech.karitetech.model.ApiResponse;
import com.melitech.karitetech.model.Certification;
import com.melitech.karitetech.model.Packaging;
import com.melitech.karitetech.model.Sceller;
import com.melitech.karitetech.utils.SessionManager;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SettingRepository extends BaseRepository {

    public SettingRepository(Context context) {
       super(context);
    }

    public void getCertifications(ApiCallback<List<Certification>> callback) {
        Type type = new TypeToken<List<Certification>>() {}.getType();
        RestClient restClient = createRestClient(type);
        Call<ApiResponse<List<Certification>>> call = restClient.getCertifications(token);
        handleCall(call, callback);
    }

    public void getPackaging(ApiCallback<List<Packaging>> callback) {
        Type type = new TypeToken<List<Packaging>>() {}.getType();
        RestClient restClient = createRestClient(type);
        Call<ApiResponse<List<Packaging>>> call = restClient.getPackaging(token);
        handleCall(call, callback);
    }


    public void getScellers(ApiCallback<List<Sceller>> callback) {
        Type type = new TypeToken<List<Sceller>>() {}.getType();
        RestClient restClient = createRestClient(type);
        Call<ApiResponse<List<Sceller>>> call = restClient.getScellers(token);
        handleCall(call, callback);
    }


}
