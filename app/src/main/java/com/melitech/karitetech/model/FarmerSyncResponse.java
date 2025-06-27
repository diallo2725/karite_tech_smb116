package com.melitech.karitetech.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

// Représente le résultat individuel pour chaque fermier envoyé
public class FarmerSyncResponse implements Parcelable {
    @SerializedName("status")
    private String status; // "success" ou "failed"
    @SerializedName("data")
    private Farmer farmerData; // Les données du fermier qui ont été envoyées/traitées
    @SerializedName("error")
    private String error; // Message d'erreur si le statut est "failed"
    @SerializedName("server_id") // Si ton serveur retourne un ID unique après création
    private String serverId; // À ajouter si ton API fournit un ID unique du serveur pour ce fermier

    protected FarmerSyncResponse(Parcel in) {
        status = in.readString();
        farmerData = in.readParcelable(Farmer.class.getClassLoader());
        error = in.readString();
        serverId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeParcelable(farmerData, flags);
        dest.writeString(error);
        dest.writeString(serverId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FarmerSyncResponse> CREATOR = new Creator<FarmerSyncResponse>() {
        @Override
        public FarmerSyncResponse createFromParcel(Parcel in) {
            return new FarmerSyncResponse(in);
        }

        @Override
        public FarmerSyncResponse[] newArray(int size) {
            return new FarmerSyncResponse[size];
        }
    };

    public String getStatus() {
        return status;
    }

    public Farmer getFarmerData() {
        return farmerData;
    }

    public String getError() {
        return error;
    }

    public String getServerId() {
        return serverId;
    }

    // Aide à vérifier si le résultat est un succès
    public boolean isSuccessful() {
        return "success".equalsIgnoreCase(status);
    }
}
