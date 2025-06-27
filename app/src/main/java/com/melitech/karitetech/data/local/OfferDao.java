package com.melitech.karitetech.data.local;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.melitech.karitetech.model.RequestOffer;

import java.util.ArrayList;
import java.util.List;

public class OfferDao {
    private static DatabaseHelper databaseHelper;
    public OfferDao(Context context) { databaseHelper = new DatabaseHelper(context); }

    public static void insertLocalOffer(@NonNull RequestOffer offer) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("certification_id", offer.getCertification_id());
        contentValues.put("packing_id", offer.getPacking_id());
        contentValues.put("price", offer.getPrice());
        contentValues.put("weight", offer.getWeight());
        contentValues.put("packingCount", offer.getPackingCount());
        contentValues.put("offer_identify", offer.getOffer_identify());
        contentValues.put("offer_state", offer.getOffer_state());
        if(offer.getRemoteId()>0){
            contentValues.put("remoteId", offer.getRemoteId());
        }
        long id = sqLiteDatabase.insert(DatabaseHelper.OFFER_TABLE_NAME, null, contentValues);
        offer.setId(id);
    }

    public static List<RequestOffer> getLocalOffers() {
        List<RequestOffer> offers = new ArrayList<>();

        String query = "SELECT * FROM " + DatabaseHelper.OFFER_TABLE_NAME + " WHERE remoteId IS NULL";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                do {
                    RequestOffer offer = new RequestOffer();
                    offer.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                    offer.setWeight(cursor.getDouble(cursor.getColumnIndexOrThrow("weight")));
                    offer.setCertification_id(cursor.getLong(cursor.getColumnIndexOrThrow("certification_id")));
                    offer.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                    offer.setPacking_id(cursor.getLong(cursor.getColumnIndexOrThrow("packing_id")));
                    offer.setPackingCount(cursor.getDouble(cursor.getColumnIndexOrThrow("packingCount")));
                    offer.setOffer_identify(cursor.getString(cursor.getColumnIndexOrThrow("offer_identify")));
                    offer.setOffer_state(cursor.getString(cursor.getColumnIndexOrThrow("offer_state")));
                    if(offer.getRemoteId()>0){
                        offer.setRemoteId(cursor.getLong(cursor.getColumnIndexOrThrow("remoteId")));
                    }
                    offers.add(offer);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Erreur lors de la récupération des offres locales", e);
        } finally {
            db.close();
        }

        return offers;
    }



    public static void updateOffer(RequestOffer offer) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("certification_id", offer.getCertification_id());
        contentValues.put("packing_id", offer.getPacking_id());
        contentValues.put("price", offer.getPrice());
        contentValues.put("weight", offer.getWeight());
        contentValues.put("packingCount", offer.getPackingCount());
        contentValues.put("offer_identify", offer.getOffer_identify());
        contentValues.put("offer_state", offer.getOffer_state());
        if(offer.getRemoteId()>0){
            contentValues.put("remoteId", offer.getRemoteId());
        }
        sqLiteDatabase.update(DatabaseHelper.OFFER_TABLE_NAME, contentValues, "id = ?",new String[] {String.valueOf(offer.getId())});
    }


    public static void deleteOffer(RequestOffer offer) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        sqLiteDatabase.delete(DatabaseHelper.OFFER_TABLE_NAME, "id = ?",new String[] {String.valueOf(offer.getId())});
    }


    public static boolean checkOfferIsOnLocal(RequestOffer offer){
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.OFFER_TABLE_NAME + " WHERE offer_identify = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{offer.getOffer_identify()});
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public static void saveRemoteOffersOnLocal(List<RequestOffer> offers){
        for(RequestOffer offer: offers){
            if(!checkOfferIsOnLocal(offer)){
                insertLocalOffer(offer);
            }else{
                updateOffer(offer);
            }
        }
    }

    public static boolean checkSellerExist(String seller){
        Log.d("TAG", "checkSellerExist: "+seller);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.SCELLER_TABLE_NAME+" where numero = ?";
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{seller});
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

}
