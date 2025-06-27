package com.melitech.karitetech.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.melitech.karitetech.model.Parc;

import java.util.ArrayList;
import java.util.List;

public class ParcDao {

     static  DatabaseHelper dbHelper;

    public ParcDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public static void ajouterParc(Parc parc) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("longeur", parc.getLongeur());
        values.put("largeur", parc.getLargeur());
        values.put("latitude", parc.getLatitude());
        values.put("longitude", parc.getLongitude());
        values.put("photo", parc.getPhoto());
        long id = sqLiteDatabase.insert(DatabaseHelper.PARC_TABLE_NAME, null, values);
        parc.setId(id);
    }

    public static List<Parc> getAllParcs() {
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        List<Parc> parcs = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM parcs", null);
        if (cursor.moveToFirst()) {
            do {
                Parc parc = new Parc();
                parc.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                parc.setRemoteid(cursor.getString(cursor.getColumnIndexOrThrow("remoteid")));
                parc.setLongeur(cursor.getString(cursor.getColumnIndexOrThrow("longeur")));
                parc.setLargeur(cursor.getString(cursor.getColumnIndexOrThrow("largeur")));
                parc.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow("latitude")));
                parc.setLongitude(cursor.getString(cursor.getColumnIndexOrThrow("longitude")));
                parc.setPhoto(cursor.getString(cursor.getColumnIndexOrThrow("photo")));
                parcs.add(parc);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return parcs;
    }

    public static void deleteParc(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("parcs", "id = ?", new String[]{String.valueOf(id)});
    }


    public static List<Parc> getLocalParcs() {
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        List<Parc> parcs = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DatabaseHelper.PARC_TABLE_NAME+" where remoteId is null", null);
        if (cursor.moveToFirst()) {
            do {
                Parc parc = new Parc();
                parc.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                parc.setRemoteid(cursor.getString(cursor.getColumnIndexOrThrow("remoteid")));
                parc.setLongeur(cursor.getString(cursor.getColumnIndexOrThrow("longeur")));
                parc.setLargeur(cursor.getString(cursor.getColumnIndexOrThrow("largeur")));
                parc.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow("latitude")));
                parc.setLongitude(cursor.getString(cursor.getColumnIndexOrThrow("longitude")));
                parc.setPhoto(cursor.getString(cursor.getColumnIndexOrThrow("photo")));
                parcs.add(parc);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return parcs;
    }


}

