package com.melitech.karitetech.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Sceller implements Parcelable {
        private long id;
        private String numero;
        private String status;
        private Integer offerId;
        private String createdAt;
        private String updatedAt;
        private int isOnLine;

        public Sceller(int id, String numero, String status, Integer offerId, String createdAt, String updatedAt, int isOnLine) {
            this.id = id;
            this.numero = numero;
            this.status = status;
            this.offerId = offerId;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.isOnLine = isOnLine;

        }

        public Sceller() {}

        protected Sceller(Parcel in) {
            id = in.readInt();
            numero = in.readString();
            status = in.readString();
            if (in.readByte() == 0) {
                offerId = null;
            } else {
                offerId = in.readInt();
            }
            createdAt = in.readString();
            updatedAt = in.readString();
            isOnLine = in.readInt();
        }

        public static final Creator<Sceller> CREATOR = new Creator<Sceller>() {
            @Override
            public Sceller createFromParcel(Parcel in) {
                return new Sceller(in);
            }

            @Override
            public Sceller[] newArray(int size) {
                return new Sceller[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(numero);
            dest.writeString(status);
            if (offerId == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(offerId);
            }
            dest.writeString(createdAt);
            dest.writeString(updatedAt);
            dest.writeInt(isOnLine);
        }

        @Override
        public int describeContents() {
            return 0;
        }


        //ecrit les setters et getters
        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getNumero() {
            return numero;
        }

        public void setNumero(String numero) {
            this.numero = numero;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getOfferId() {
            return offerId;
        }

        public void setOfferId(Integer offerId) {
            this.offerId = offerId;
        }

        public int getIsOnLine() {
            return isOnLine;
        }

        public void setIsOnLine(int isOnLine) {
            this.isOnLine = isOnLine;
        }

}
