package com.melitech.karitetech.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Farmer implements Parcelable {

        private transient long id;
        @SerializedName("id")
        private long remoteId;
        @SerializedName("fullname")
        private String fullname;
        @SerializedName("date_of_birth")
        private String date_of_birth;
        @SerializedName("locality")
        private String locality;
        @SerializedName("phone")
        private String phone;
        @SerializedName("phone_payment")
        private String phone_payment;
        @SerializedName("sexe")
        private String sexe;
        @SerializedName("job")
        private String job;
        @SerializedName("picture")
        private String picture;
        private boolean  isFromRemote = false;
        private  boolean isUpdated = false;

        public Farmer() {}

        // Constructor
        public Farmer(long id,long remoteId, String fullname, String date_of_birth, String locality,
                      String phone, String phone_payment, String sexe, String job, String picture,
                      boolean isFromRemote, boolean isUpdated) {
            this.id = id;
            this.remoteId = remoteId;
            this.fullname = fullname;
            this.locality = locality;
            this.phone = phone;
            this.phone_payment = phone_payment;
            this.sexe = sexe;
            this.job = job;
            this.picture = picture;
            this.date_of_birth = date_of_birth;
            this.isFromRemote = isFromRemote;
            this.isUpdated = isUpdated;
        }

        // Parcelable implementation
        protected Farmer(Parcel in) {
            id = in.readLong();
            fullname = in.readString();
            date_of_birth = in.readString();
            locality = in.readString();
            phone = in.readString();
            phone_payment = in.readString();
            sexe = in.readString();
            job = in.readString();
            picture = in.readString();
            isFromRemote = in.readByte() != 0;
            isUpdated = in.readByte() != 0;
            remoteId = in.readLong();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(fullname);
            dest.writeString(date_of_birth);
            dest.writeString(locality);
            dest.writeString(phone);
            dest.writeString(phone_payment);
            dest.writeString(sexe);
            dest.writeString(job);
            dest.writeString(picture);
            dest.writeByte((byte) (isFromRemote ? 1 : 0));
            dest.writeByte((byte) (isUpdated ? 1 : 0));
            dest.writeLong(remoteId);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Farmer> CREATOR = new Creator<Farmer>() {
            @Override
            public Farmer createFromParcel(Parcel in) {
                return new Farmer(in);
            }

            @Override
            public Farmer[] newArray(int size) {
                return new Farmer[size];
            }
        };

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getRemoteId() {
            return remoteId;
        }

        public void setRemoteId(long remoteId) {
            this.remoteId = remoteId;
        }


        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public String getDate_of_birth() {
            return date_of_birth;
        }

        public void setDate_of_birth(String born_date) {
            this.date_of_birth = born_date;
        }


        public String getLocality() {
            return locality;
        }

        public void setLocality(String locality) {
            this.locality = locality;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPhone_payment() {
            return phone_payment;
        }

        public void setPhone_payment(String phone_payment) {
            this.phone_payment = phone_payment;
        }

        public String getSexe() {
            return sexe;
        }

        public void setSexe(String sexe) {
            this.sexe = sexe;
        }

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }
        public void setIsFromRemote(boolean isFromRemote) {
            this.isFromRemote = isFromRemote;
        }

        public boolean getIsFromRemote() {
            return isFromRemote;
        }

        public void setIsUpdated(boolean isUpdated) {
            this.isUpdated = isUpdated;
        }

        public boolean getIsUpdated() {
            return isUpdated;
        }

    }


