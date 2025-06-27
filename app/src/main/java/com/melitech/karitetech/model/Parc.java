package com.melitech.karitetech.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Parc implements Parcelable {

    private transient long id;
   @SerializedName("id")
    private String remoteid;
    private String longeur;
    private String largeur;
    private String longitude;
    private String latitude;
    private String photo;

    // Constructeur
    public Parc(long id, String remoteid, String longeur, String largeur, String longitude, String latitude, String photo) {
        this.id = id;
        this.remoteid = remoteid;
        this.longeur = longeur;
        this.largeur = largeur;
        this.longitude = longitude;
        this.latitude = latitude;
        this.photo = photo;
    }


    public Parc() {}
    // Getters
    public long getId() {
        return id;
    }

    public String getRemoteid() {
        return remoteid;
    }

    public String getLongeur() {
        return longeur;
    }

    public String getLargeur() {
        return largeur;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getPhoto() {
        return photo;
    }

    // Parcelable
    protected Parc(Parcel in) {
        id = in.readInt();
        remoteid = in.readString();
        longeur = in.readString();
        largeur = in.readString();
        longitude = in.readString();
        latitude = in.readString();
        photo = in.readString();
    }

    public static final Creator<Parc> CREATOR = new Creator<Parc>() {
        @Override
        public Parc createFromParcel(Parcel in) {
            return new Parc(in);
        }

        @Override
        public Parc[] newArray(int size) {
            return new Parc[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(remoteid);
        dest.writeString(longeur);
        dest.writeString(largeur);
        dest.writeString(longitude);
        dest.writeString(latitude);
        dest.writeString(photo);
    }


    public void setId(long id) {this.id = id;}
    public void setRemoteid(String remoteid) {this.remoteid = remoteid;}
    public void setLongeur(String longeur) {this.longeur = longeur;}
    public void setLargeur(String largeur) {this.largeur = largeur;}
    public void setLongitude(String longitude) {this.longitude = longitude;}
    public void setLatitude(String latitude) {this.latitude = latitude;}
    public void setPhoto(String photo) {this.photo = photo;}


}
