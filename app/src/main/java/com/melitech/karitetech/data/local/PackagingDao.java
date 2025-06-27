package com.melitech.karitetech.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.melitech.karitetech.model.Certification;
import com.melitech.karitetech.model.Packaging;

import java.util.ArrayList;
import java.util.List;

public class PackagingDao {
    static DatabaseHelper databaseHelper;

    public PackagingDao(Context context) { databaseHelper = new DatabaseHelper(context); }

    public static void insertPackaging(List<Packaging> packagings) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (Packaging certification : packagings) {
            contentValues.put("name", certification.getName());
            contentValues.put("remoteId", certification.getRemoteId());
            sqLiteDatabase.insert(DatabaseHelper.PACKAGING_TABLE_NAME, null, contentValues);
            long id = sqLiteDatabase.insert(DatabaseHelper.PACKAGING_TABLE_NAME, null, contentValues);
            certification.setId(id);
        }
    }

    public static List<Packaging> getPackagings() {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.PACKAGING_TABLE_NAME+" GROUP BY remoteId";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        List<Packaging> packagings = new ArrayList<>();
        while (cursor.moveToNext()) {
            Packaging certification = new Packaging();
            certification.setId(cursor.getLong(0));
            certification.setName(cursor.getString(1));
            certification.setRemoteId(cursor.getLong(2));
            packagings.add(certification);
        }
        return packagings;
    }

    public static Packaging getPackaging(long id) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.PACKAGING_TABLE_NAME + " WHERE id = " + id;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        Packaging certification = new Packaging();
        while (cursor.moveToNext()) {
            certification.setId(cursor.getLong(0));
            certification.setName(cursor.getString(1));
            certification.setRemoteId(cursor.getLong(2));
        }
        return certification;
    }

    public static void deleteAllPackagings() {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        sqLiteDatabase.delete(DatabaseHelper.PACKAGING_TABLE_NAME, null, null);
    }


    public static Packaging findPackaging(String name) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.PACKAGING_TABLE_NAME +
                " WHERE name = '" + name + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        Packaging packaging = new Packaging();
        while (cursor.moveToNext()) {
            packaging.setId(cursor.getLong(0));
            packaging.setName(cursor.getString(1));
            packaging.setRemoteId(cursor.getLong(2));
        }
        return packaging;
    }

    public static Packaging findById(String packagingId) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.PACKAGING_TABLE_NAME +
                " WHERE remoteId = '" + packagingId + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        Packaging packaging = new Packaging();
        while (cursor.moveToNext()) {
            packaging.setId(cursor.getLong(0));
            packaging.setName(cursor.getString(1));
            packaging.setRemoteId(cursor.getLong(2));
        }
        return packaging;
    }


}
