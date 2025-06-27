package com.melitech.karitetech.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class Certification implements Parcelable {

        private  transient long id;
        @SerializedName("name")
        private String name;
        @SerializedName("id")
        private long remoteId;

        public Certification() {}

        public Certification(long id, String name, long remoteId) {
            this.id = id;
            this.name = name;
            this.remoteId = remoteId;
        }

        protected Certification(Parcel in) {
            id = in.readLong();
            name = in.readString();
            remoteId = in.readLong();
        }

        public static final Creator<Certification> CREATOR = new Creator<Certification>() {
            @Override
            public Certification createFromParcel(Parcel in) {
                return new Certification(in);
            }

            @Override
            public Certification[] newArray(int size) {
                return new Certification[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(name);
            dest.writeLong(remoteId);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        // Getters et Setters
        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getRemoteId() {
            return remoteId;
        }

        public void setRemoteId(long remoteId) {
            this.remoteId = remoteId;
        }

}
