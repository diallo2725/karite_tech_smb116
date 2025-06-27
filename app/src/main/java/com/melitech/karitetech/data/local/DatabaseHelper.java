package com.melitech.karitetech.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "karite.db";
    private static final int DATABASE_VERSION = 26;

    public static final String USER_TABLE_NAME = "users";
    public static final String FARMER_TABLE_NAME = "farmers";
    public static final String CERTIFICATION_TABLE_NAME = "certifications";
    public static final String PACKAGING_TABLE_NAME = "packagings";
    public static final String OFFER_TABLE_NAME = "offers";
    public static final String PURCHASE_TABLE_NAME = "purchases";
    public static final String PARC_TABLE_NAME = "parcs";
    public static final String SCELLER_TABLE_NAME = "scellers";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createUserTable());
        db.execSQL(createFarmerTable());
        db.execSQL(createCertificationTable());
        db.execSQL(createPackagingTable());
        db.execSQL(createOfferTable());
        db.execSQL(createPurchaseTable());
        db.execSQL(createParcTable());
        db.execSQL(createScellerTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FARMER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CERTIFICATION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PACKAGING_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + OFFER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PURCHASE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PARC_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SCELLER_TABLE_NAME);
        onCreate(db);
    }

    // TABLE CREATION METHODS
    private String createUserTable() {
        return "CREATE TABLE " + USER_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "firstname TEXT, " +
                "lastname TEXT, " +
                "username TEXT, " +
                "phone TEXT)";
    }

    private String createFarmerTable() {
        return "CREATE TABLE " + FARMER_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fullname TEXT, " +
                "date_of_birth TEXT, " +
                "locality TEXT, " +
                "phone TEXT, " +
                "phone_payment TEXT, " +
                "sexe TEXT, " +
                "job TEXT, " +
                "picture TEXT, " +
                "isFromRemote INTEGER DEFAULT 0, " +
                "isUpdated INTEGER DEFAULT 0, " +
                "remoteId TEXT)";
    }

    private String createCertificationTable() {
        return "CREATE TABLE " + CERTIFICATION_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "remoteId INTEGER)";
    }

    private String createPackagingTable() {
        return "CREATE TABLE " + PACKAGING_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "remoteId INTEGER, " +
                "created_at TEXT, " +
                "updated_at TEXT)";
    }

    private String createOfferTable() {
        return "CREATE TABLE " + OFFER_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "weight REAL NOT NULL, " +
                "certification_id INTEGER NOT NULL, " +
                "price REAL NOT NULL, " +
                "packing_id INTEGER NOT NULL, " +
                "packingCount REAL NOT NULL, " +
                "created_at TEXT, " +
                "updated_at TEXT, " +
                "offer_identify TEXT, " +
                "offer_state TEXT DEFAULT waited, " +
                "remoteId INTEGER)";
    }

    private String createPurchaseTable() {
        return "CREATE TABLE " + PURCHASE_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY, " +
                "remoteid TEXT, " +
                "weight TEXT, " +
                "quality TEXT, " +
                "payment_method TEXT, " +
                "price TEXT, " +
                "amount TEXT, " +
                "cash TEXT, " +
                "fullname TEXT, " +
                "picture TEXT, " +
                "phone TEXT, " +
                "phone_payment TEXT, " +
                "farmer_id INTEGER)";
    }

    private String createParcTable() {
        return "CREATE TABLE " + PARC_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY, " +
                "remoteid TEXT, " +
                "longeur TEXT, " +
                "largeur TEXT, " +
                "longitude TEXT, " +
                "latitude TEXT, " +
                "photo TEXT)";
    }

    private String createScellerTable() {
        return "CREATE TABLE " + SCELLER_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY, " +
                "numero TEXT, " +
                "offer_id INTEGER, " +
                "status TEXT, " + //NULL NOT USED YET
                "created_at TEXT, " +
                "updated_at TEXT, " +
                "isOnLine INTEGER DEFAULT 0)"; //offline = 0, online = 1
    }


}
