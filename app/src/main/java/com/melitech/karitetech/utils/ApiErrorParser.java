package com.melitech.karitetech.utils;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class ApiErrorParser {
    public static String parse(String jsonErrorMessage) {
        try {
            JSONObject errorJson = new JSONObject(jsonErrorMessage);
            String message = errorJson.optString("message", "Erreur inconnue");

            JSONObject data = errorJson.optJSONObject("data");
            StringBuilder details = new StringBuilder();

            if (data != null) {
                Iterator<String> keys = data.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONArray messages = data.optJSONArray(key);
                    if (messages != null) {
                        for (int i = 0; i < messages.length(); i++) {
                            details.append("- ").append(key).append(": ").append(messages.getString(i)).append("\n");
                        }
                    }
                }
            }

            return message + (details.length() > 0 ? "\n" + details.toString().trim() : "");

        } catch (JSONException e) {
            Log.e("ApiErrorParser", "Erreur JSON: " + e.getMessage());
            return "Erreur inconnue (réponse non exploitable)";
        }
    }
}

