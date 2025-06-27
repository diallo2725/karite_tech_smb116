package com.melitech.karitetech.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Packaging implements Parcelable {
    private  transient long id;
    @SerializedName("id")
    private long remoteId;
    @SerializedName("name")
    private String name;
    private String created_at;
    private String updated_at;

    // Constructeur
    public Packaging(long remoteId, String name, String created_at, String updated_at) {
        this.remoteId = remoteId;
        this.name = name;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public  Packaging() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    // Getters et Setters
    public long getRemoteId() { return remoteId; }
    public void setRemoteId(long remoteId) { this.remoteId = remoteId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    public String getUpdated_at() { return updated_at; }
    public void setUpdated_at(String updated_at) { this.updated_at = updated_at; }

    // Parcelable
    protected Packaging(Parcel in) {
        id = in.readLong();
        remoteId = in.readLong();
        name = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
    }

    public static final Creator<Packaging> CREATOR = new Creator<Packaging>() {
        @Override
        public Packaging createFromParcel(Parcel in) {
            return new Packaging(in);
        }

        @Override
        public Packaging[] newArray(int size) {
            return new Packaging[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(remoteId);
        dest.writeString(name);
        dest.writeString(created_at);
        dest.writeString(updated_at);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
