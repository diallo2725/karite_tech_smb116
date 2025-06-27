package com.melitech.karitetech.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.melitech.karitetech.model.Sceller;

import java.util.ArrayList;
import java.util.List;

public class ScellerDao {
    static DatabaseHelper databaseHelper;

    public ScellerDao(Context context) { databaseHelper = new DatabaseHelper(context); }

    public static void insertSceller(List<Sceller> scellers) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (Sceller sceller : scellers) {
            contentValues.put("numero", sceller.getNumero());
            contentValues.put("offer_id", sceller.getOfferId());
            contentValues.put("status", sceller.getStatus());
            sqLiteDatabase.insert(DatabaseHelper.SCELLER_TABLE_NAME, null, contentValues);
            long id = sqLiteDatabase.insert(DatabaseHelper.SCELLER_TABLE_NAME, null, contentValues);
            sceller.setId(id);
        }
    }

    public static List<Sceller> getScellers() {
        List<Sceller> scellers = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        if (db == null) return scellers;
        String query = "SELECT * FROM " + DatabaseHelper.SCELLER_TABLE_NAME + " ORDER BY id DESC";
        try (Cursor cursor = db.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                Sceller sceller = new Sceller();
                sceller.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                sceller.setOfferId(Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("offer_id"))));
                sceller.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                sceller.setIsOnLine(cursor.getInt(cursor.getColumnIndexOrThrow("isOnLine")));
                scellers.add(sceller);
            }
        }
        return scellers;
    }

    public static Sceller findSceller(String numero) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.SCELLER_TABLE_NAME +
                " WHERE numero = '" + numero + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        Sceller sceller = new Sceller();
        while (cursor.moveToNext()) {
            sceller.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
            sceller.setOfferId(Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("offer_id"))));
            sceller.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
            sceller.setIsOnLine(cursor.getInt(cursor.getColumnIndexOrThrow("isOnLine")));
        }
        return sceller;
    }


}
