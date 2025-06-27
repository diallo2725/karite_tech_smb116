package com.melitech.karitetech.data.remote;

import com.melitech.karitetech.model.Parc;
import com.melitech.karitetech.model.Purchase;
import com.melitech.karitetech.model.ApiResponse;
import com.melitech.karitetech.model.Certification;
import com.melitech.karitetech.model.CreateResponse;
import com.melitech.karitetech.model.Farmer;
import com.melitech.karitetech.model.LoginResponse;
import com.melitech.karitetech.model.Packaging;
import com.melitech.karitetech.model.RequestLogin;
import com.melitech.karitetech.model.RequestOffer;
import com.melitech.karitetech.model.Sceller;
import com.melitech.karitetech.model.SyncResult;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface RestClient {
    @POST("login")
    Call<ApiResponse<LoginResponse>> login(@Body RequestLogin requestLogin);
    @GET("farmers")
    Call<ApiResponse<List<Farmer>>> getFarmers(@Header("Authorization") String token);
    @Multipart
    @POST("farmers")
    Call<ApiResponse<SyncResult>> synchroniseFarmers(
            @Part("farmer") RequestBody farmersJson,
            @Part MultipartBody.Part images,
            @Header("Authorization") String token
    );

    @Multipart
    @POST("farmers")
    Call<ApiResponse<Farmer>> addFarmer(
            @Part("fullname") RequestBody fullname,
            @Part("date_of_birth") RequestBody dateOfBirth,
            @Part("locality") RequestBody locality,
            @Part("sexe") RequestBody sexe,
            @Part("job") RequestBody job,
            @Part("phone") RequestBody phone,
            @Part("isUpdated") RequestBody isUpdated,
            @Part MultipartBody.Part picture,
            @Header("Authorization") String token
    );

    @Multipart
    @POST("farmers/sync")
    Call<ApiResponse<SyncResult>> syncFarmers(
            @Part("farmers") RequestBody farmersBody,
            @Part List<MultipartBody.Part> images,
            @Header("Authorization") String token
    );

    @GET("certifications")
    Call<ApiResponse<List<Certification>>> getCertifications(@Header("Authorization") String token);
    @GET("packagings")
    Call<ApiResponse<List<Packaging>>> getPackaging(@Header("Authorization") String token);
    @GET("scelles")
    Call<ApiResponse<List<Sceller>>> getScellers(@Header("Authorization") String token);


    @POST("offers")
    Call<ApiResponse<RequestOffer>> createOffer(
            @Body RequestOffer requestOffer,
            @Header("Authorization") String token
    );

    @GET("offers")
    Call<ApiResponse<List<RequestOffer>>> getOffers(@Header("Authorization") String token);

    @POST("offers/sync")
    Call<ApiResponse<SyncResult<RequestOffer>>> syncOffers(
            @Body RequestBody offers,
            @Header("Authorization") String token
    );

    @POST("purchases")
    Call<ApiResponse<Purchase>> makePurchase(
            @Body Purchase achat,
            @Header("Authorization") String token
    );

    @GET("purchases")
    Call<ApiResponse<List<Purchase>>> getPurchases(@Header("Authorization") String token);

    @POST("purchases/sync")
    Call<ApiResponse<SyncResult<Purchase>>> syncPurchases(
            @Body RequestBody purchases,
            @Header("Authorization") String token
    );


    @Multipart
    @POST("parcs-a-bois/sync")
    Call<ApiResponse<SyncResult>> synchroniseParcs(
            @Part("parcs") RequestBody parcBody,
            @Part List<MultipartBody.Part> images,
            @Header("Authorization") String token
    );

    @Multipart
    @POST("parcs-a-bois")
    Call<ApiResponse<Parc>> addParc(
            @Part("longeur") RequestBody longueur,
            @Part("largeur") RequestBody largeur,
            @Part("longitude") RequestBody longitude,
            @Part("latitude") RequestBody latitude,
            @Part MultipartBody.Part picture,
            @Header("Authorization") String token
    );

    @GET("parcs-a-bois")
    Call<ApiResponse<List<Parc>>> getParcs(@Header("Authorization") String token);

}
