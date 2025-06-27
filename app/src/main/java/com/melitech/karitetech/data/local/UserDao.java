package com.melitech.karitetech.data.local;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.melitech.karitetech.model.User;

public class UserDao {
    static DatabaseHelper databaseHelper;
    public UserDao(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public static void insertUser(String firstname, String lastname, String username, String phone) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if (db == null) return;

        ContentValues values = new ContentValues();
        values.put("firstname", firstname);
        values.put("lastname", lastname);
        values.put("username", username);
        values.put("phone", phone);

        db.insert("users", null, values);
        db.close();
    }


    @SuppressLint("Range")
    public static User getUser() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        if (db == null) return null;

        String query = "SELECT * FROM users LIMIT 1";

        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                User user = new User();
                user.setFirstname(cursor.getString(cursor.getColumnIndexOrThrow("firstname")));
                user.setLastname(cursor.getString(cursor.getColumnIndexOrThrow("lastname")));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
                user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                return user;
            }
        }

        return null;
    }


    public static  void deleteUser() {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        sqLiteDatabase.delete(DatabaseHelper.USER_TABLE_NAME, null, null);
    }
}
