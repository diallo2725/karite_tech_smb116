package com.melitech.karitetech.data.local;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.melitech.karitetech.model.Farmer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FarmerDao {
 static DatabaseHelper databaseHelper;

    public FarmerDao(Context context) { databaseHelper = new DatabaseHelper(context); }

    public static void insertFarmer(@NonNull List<Farmer> farmers) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (Farmer farmer : farmers) {
            if(!checkExistFarmer(farmer.getPhone())){
                contentValues.put("remoteId", farmer.getRemoteId());
                contentValues.put("fullname", farmer.getFullname());
                contentValues.put("date_of_birth", farmer.getDate_of_birth());
                contentValues.put("phone", farmer.getPhone());
                contentValues.put("phone_payment", farmer.getPhone_payment());
                contentValues.put("sexe", farmer.getSexe());
                contentValues.put("job", farmer.getJob());
                contentValues.put("picture", farmer.getPicture());
                contentValues.put("isFromRemote", true);
                long id = sqLiteDatabase.insert(DatabaseHelper.FARMER_TABLE_NAME, null, contentValues);
                farmer.setId(id);
            }
        }
        Log.d("FarmerDao", "Farmers inserted successfully" );
    }

    public static void insertSingleFarmer(Farmer farmer,boolean isFromRemote) {
        try (SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("fullname", farmer.getFullname());
            contentValues.put("date_of_birth", farmer.getDate_of_birth());
            contentValues.put("locality", farmer.getLocality());
            contentValues.put("phone", farmer.getPhone());
            contentValues.put("phone_payment", farmer.getPhone() != null ? farmer.getPhone() : "");
            contentValues.put("sexe", farmer.getSexe());
            contentValues.put("job", farmer.getJob());
            contentValues.put("picture", farmer.getPicture());
            contentValues.put("isFromRemote", farmer.getIsFromRemote() ? 1 : 0);
            contentValues.put("isUpdated", farmer.getIsUpdated() ? 1 : 0);
            contentValues.put("remoteId", farmer.getRemoteId());
            contentValues.put("isFromRemote", isFromRemote);

            long insertedId = sqLiteDatabase.insert(DatabaseHelper.FARMER_TABLE_NAME, null, contentValues);
            if (insertedId == -1) {
                Log.e("SQLITE_ERROR", "Insertion échouée. Contenu : " + contentValues.toString());
            } else {
                farmer.setId(insertedId);
            }

        } catch (Exception e) {
            Log.e("SQLITE_EXCEPTION", "Erreur lors de l'insertion du farmer", e);
        }
    }

    @NonNull
    public static List<Farmer> getLocalFarmers(){
        ArrayList<Farmer> farmers = new ArrayList<>();
        try {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.FARMER_TABLE_NAME+" where isFromRemote=false";
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Farmer farmer = new Farmer();
            farmer.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
            farmer.setFullname(cursor.getString(cursor.getColumnIndexOrThrow("fullname")));
            farmer.setDate_of_birth(cursor.getString(cursor.getColumnIndexOrThrow("date_of_birth")));
            farmer.setLocality(cursor.getString(cursor.getColumnIndexOrThrow("locality")));
            farmer.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            farmer.setSexe(cursor.getString(cursor.getColumnIndexOrThrow("sexe")));
            farmer.setJob(cursor.getString(cursor.getColumnIndexOrThrow("job")));
            farmer.setPicture(cursor.getString(cursor.getColumnIndexOrThrow("picture")));
            farmers.add(farmer);
        }
        Log.d("DEBUG", "Nombre de fermiers récupérés : " + farmers.size());
        db.close();
        return farmers;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Erreur lors de la récupération des fermiers : ", e);
        }
        return farmers;
    }


    public static void updateFarmer(Farmer farmers) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("fullname", farmers.getFullname());
        contentValues.put("date_of_birth", farmers.getDate_of_birth());
        contentValues.put("locality", farmers.getLocality());
        contentValues.put("phone", farmers.getPhone());
        contentValues.put("phone_payment", farmers.getPhone_payment());
        contentValues.put("sexe", farmers.getSexe());
        contentValues.put("job", farmers.getJob());
        contentValues.put("picture", farmers.getPicture());
        contentValues.put("isFromRemote", false);
        contentValues.put("isUpdated", farmers.getIsUpdated() ? 1 : 0);
       // contentValues.put("remoteId", farmers.getRemoteId());
        sqLiteDatabase.update(DatabaseHelper.FARMER_TABLE_NAME, contentValues, "id = ?", new String[]{String.valueOf(farmers.getId())});
    }


    public static void updateRemoteField(Farmer farmers) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("isFromRemote", true);
        contentValues.put("remoteId", farmers.getRemoteId());
        sqLiteDatabase.update(DatabaseHelper.FARMER_TABLE_NAME, contentValues, "id = ?", new String[]{String.valueOf(farmers.getId())});
    }


    public static void deleteFarmer(Farmer farmers) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        sqLiteDatabase.delete(DatabaseHelper.FARMER_TABLE_NAME, "id = ?",new String[] {String.valueOf(farmers.getId())});
    }

    public static Boolean checkExistFarmer(String phoneNumber) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.FARMER_TABLE_NAME+" where REPLACE(phone,' ','') = ? AND remoteId > 0";
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{phoneNumber});
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public static Farmer getFarmerByPhoneNumber(String phoneNumber) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.FARMER_TABLE_NAME+" WHERE REPLACE(phone,' ','') = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{phoneNumber});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            Farmer farmer = new Farmer();
            farmer.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
            farmer.setFullname(cursor.getString(cursor.getColumnIndexOrThrow("fullname")));
            farmer.setDate_of_birth(cursor.getString(cursor.getColumnIndexOrThrow("date_of_birth")));
            farmer.setLocality(cursor.getString(cursor.getColumnIndexOrThrow("locality")));
            farmer.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            farmer.setSexe(cursor.getString(cursor.getColumnIndexOrThrow("sexe")));
            farmer.setJob(cursor.getString(cursor.getColumnIndexOrThrow("job")));
            farmer.setPicture(cursor.getString(cursor.getColumnIndexOrThrow("picture")));
            if(cursor.getInt(cursor.getColumnIndexOrThrow("remoteId"))>0){
                farmer.setRemoteId(cursor.getInt(cursor.getColumnIndexOrThrow("remoteId")));
            }
            cursor.close();
            return farmer;
        }
        cursor.close();
        return null;
    }

    public static int getFarmerCount() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.FARMER_TABLE_NAME + " where isFromRemote=false", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }


}



