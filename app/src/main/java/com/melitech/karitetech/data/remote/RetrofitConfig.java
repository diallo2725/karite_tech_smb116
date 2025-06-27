package com.melitech.karitetech.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.melitech.karitetech.model.ApiResponse;
import com.melitech.karitetech.model.ApiResponseDeserializer;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*public class RetrofitConfig {

    private static final String BASE_URL = "https://karitetech.flexci.net/api/";

    public static <T> Retrofit getRetrofit(Type dataType) {
        Type apiResponseType = TypeToken.getParameterized(ApiResponse.class, dataType).getType();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(apiResponseType, new ApiResponseDeserializer<>(dataType))
                .create();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}*/

public class RetrofitConfig {

    private static final String BASE_URL = "https://karitetech.flexci.net/api/";

    public static Retrofit getRetrofit(Type dataType) {
        Type apiResponseType = TypeToken.getParameterized(ApiResponse.class, dataType).getType();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(apiResponseType, new ApiResponseDeserializer<>(dataType))
                .setLenient()
                .create();

        return createRetrofit(gson);
    }

    public static Retrofit getDefaultRetrofit() {
        Gson gson = new GsonBuilder().setLenient().create();
        return createRetrofit(gson);
    }

    private static Retrofit createRetrofit(Gson gson) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}


