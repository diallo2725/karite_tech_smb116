package com.melitech.karitetech.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.melitech.karitetech.model.Certification;

import java.util.ArrayList;
import java.util.List;

public class CertificationDao {
    static DatabaseHelper databaseHelper;

    public CertificationDao(Context context) { databaseHelper = new DatabaseHelper(context); }

    public static void insertCertification(List<Certification> certifications) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (Certification certification : certifications) {
            contentValues.put("name", certification.getName());
            contentValues.put("remoteId", certification.getRemoteId());
            sqLiteDatabase.insert(DatabaseHelper.CERTIFICATION_TABLE_NAME, null, contentValues);
            long id = sqLiteDatabase.insert(DatabaseHelper.CERTIFICATION_TABLE_NAME, null, contentValues);
            certification.setId(id);
        }
    }

    public static List<Certification> getCertifications() {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.CERTIFICATION_TABLE_NAME+" GROUP BY remoteId";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        List<Certification> certifications = new ArrayList<>();
        while (cursor.moveToNext()) {
            Certification certification = new Certification();
            certification.setId(cursor.getLong(0));
            certification.setName(cursor.getString(1));
            certification.setRemoteId(cursor.getLong(2));
            certifications.add(certification);
        }
        return certifications;
    }

    public static Certification getCertification(long id) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.CERTIFICATION_TABLE_NAME + " WHERE id = " + id;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        Certification certification = new Certification();
        while (cursor.moveToNext()) {
            certification.setId(cursor.getLong(0));
            certification.setName(cursor.getString(1));
            certification.setRemoteId(cursor.getLong(2));
        }
        return certification;
    }

    public static void deleteAllCertifications() {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        sqLiteDatabase.delete(DatabaseHelper.CERTIFICATION_TABLE_NAME, null, null);
    }


    public static Certification findCertificaiton(String name) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.CERTIFICATION_TABLE_NAME +
                " WHERE name = '" + name + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        Certification certification = new Certification();
        while (cursor.moveToNext()) {
            certification.setId(cursor.getLong(0));
            certification.setName(cursor.getString(1));
            certification.setRemoteId(cursor.getLong(2));
        }
        return certification;
    }


    public static Certification findById(String certificationId) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.CERTIFICATION_TABLE_NAME +
                " WHERE remoteId = '" + certificationId + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        Certification certification = new Certification();
        while (cursor.moveToNext()) {
            certification.setId(cursor.getLong(0));
            certification.setName(cursor.getString(1));
            certification.setRemoteId(cursor.getLong(2));
        }
        return certification;
    }



}
