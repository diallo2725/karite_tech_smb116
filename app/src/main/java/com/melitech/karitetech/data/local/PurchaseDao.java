package com.melitech.karitetech.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.melitech.karitetech.model.Purchase;

import java.util.ArrayList;
import java.util.List;

public class PurchaseDao {

     static  DatabaseHelper dbHelper;

    public PurchaseDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }


    public static void ajouterPurchase(Purchase purchase) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("weight", purchase.getWeight());
        values.put("quality", purchase.getQuality());
        values.put("payment_method", purchase.getPaymentMethod());
        values.put("price", purchase.getPrice());
        values.put("amount", purchase.getAmount());
        values.put("cash", purchase.getCash());
        values.put("fullname", purchase.getFullname());
        values.put("picture", purchase.getPicture());
        values.put("phone", purchase.getPhone());
        values.put("phone_payment", purchase.getPhonePayment());
        long id = sqLiteDatabase.insert(DatabaseHelper.PURCHASE_TABLE_NAME, null, values);
        purchase.setId(id);
    }


    public static List<Purchase> getAllPurchases() {
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        List<Purchase> achats = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM purchases", null);

        if (cursor.moveToFirst()) {
            do {
                Purchase achat = new Purchase();
                achat.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                achat.setRemoteid(cursor.getString(cursor.getColumnIndexOrThrow("remoteid")));
                achat.setWeight(cursor.getString(cursor.getColumnIndexOrThrow("weight")));
                achat.setQuality(cursor.getString(cursor.getColumnIndexOrThrow("quality")));
                achat.setPaymentMethod(cursor.getString(cursor.getColumnIndexOrThrow("payment_method")));
                achat.setPrice(cursor.getString(cursor.getColumnIndexOrThrow("price")));
                achat.setAmount(cursor.getString(cursor.getColumnIndexOrThrow("amount")));
                achat.setCash(cursor.getString(cursor.getColumnIndexOrThrow("cash")));
                achat.setFullname(cursor.getString(cursor.getColumnIndexOrThrow("fullname")));
                achat.setPicture(cursor.getString(cursor.getColumnIndexOrThrow("picture")));
                achat.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                achat.setPhonePayment(cursor.getString(cursor.getColumnIndexOrThrow("phone_payment")));
                achats.add(achat);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return achats;
    }


    public static List<Purchase> getLocalPurchases() {
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        List<Purchase> purchases = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DatabaseHelper.PURCHASE_TABLE_NAME+" where remoteId is null", null);
        if (cursor.moveToFirst()) {
            do {
                Purchase purchase = new Purchase();
                purchase.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                purchase.setRemoteid(cursor.getString(cursor.getColumnIndexOrThrow("remoteid")));
                purchase.setWeight(cursor.getString(cursor.getColumnIndexOrThrow("weight")));
                purchase.setQuality(cursor.getString(cursor.getColumnIndexOrThrow("quality")));
                purchase.setPaymentMethod(cursor.getString(cursor.getColumnIndexOrThrow("payment_method")));
                purchase.setPrice(cursor.getString(cursor.getColumnIndexOrThrow("price")));
                purchase.setAmount(cursor.getString(cursor.getColumnIndexOrThrow("amount")));
                purchase.setCash(cursor.getString(cursor.getColumnIndexOrThrow("cash")));
                purchase.setFarmerId(cursor.getColumnIndexOrThrow("farmer_id"));
                purchases.add(purchase);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return purchases;
    }

    public static void deletePurchase(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
         db.delete("purchases", "id = ?", new String[]{String.valueOf(id)});
    }


}

