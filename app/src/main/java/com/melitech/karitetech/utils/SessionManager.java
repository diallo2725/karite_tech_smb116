package com.melitech.karitetech.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "MY_APP_PREF";
    private static final String TOKEN = "TOKEN";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Enregistrer le token
    public void saveToken(String token) {
        editor.putString(TOKEN, token);
        editor.apply();
    }

    // Récupérer le token
    public String getToken() {
        return prefs.getString(TOKEN, null);
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    // Supprimer le token (ex: logout)
    public void clearToken() {
        editor.remove(TOKEN);
        editor.apply();
    }


    public void setSyncDataErrors(String errors) {
         editor.putString("sync_data_errors", errors);
        editor.apply();
    }

    public String getSyncDataErrors() {
        return prefs.getString("sync_data_errors", null);
    }

    public void clearSyncDataErrors() {
        editor.remove("sync_data_errors");
    }
}

