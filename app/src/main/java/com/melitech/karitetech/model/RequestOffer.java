package com.melitech.karitetech.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RequestOffer implements Parcelable {

    private transient long id;
    @SerializedName("id")
    private long remoteId;
    @SerializedName("weight")
    private double weight;
    @SerializedName("certification_id")
    private long certification_id;
    @SerializedName("price")
    private double price;
    @SerializedName("packing_id")
    private long packing_id;
    @SerializedName("packingCount")
    private double packingCount;

    @SerializedName("offer_identify")
    private String offer_identify;

    @SerializedName("offer_state")
    private String offer_state;

    // Constructor
    public RequestOffer(long id,long remoteId, double weight, long certification_id, double price,
                       long packing_id, double packingCount, String offer_identity, String offer_state) {
        this.id = id;
        this.weight = weight;
        this.certification_id = certification_id;
        this.price = price;
        this.packing_id = packing_id;
        this.packingCount = packingCount;
        this.remoteId = remoteId;
        this.offer_identify = offer_identity;
        this.offer_state = offer_state;
    }

    public RequestOffer() {}

    public long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(long remoteId) {
        this.remoteId = remoteId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // Getter & Setter
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public long getCertification_id() {
        return certification_id;
    }

    public void setCertification_id(long certification_id) {
        this.certification_id = certification_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getPacking_id() {
        return packing_id;
    }

    public void setPacking_id(long packing_id) {
        this.packing_id = packing_id;
    }

    public double getPackingCount() {
        return packingCount;
    }

    public void setPackingCount(double packingCount) {
        this.packingCount = packingCount;
    }


    public String getOffer_identify() {
        return offer_identify;
    }

    public void setOffer_identify(String offer_identify) {
        this.offer_identify = offer_identify;
    }

    public String getOffer_state() {
        return offer_state;
    }

    public void setOffer_state(String offer_state) {
        this.offer_state = offer_state;
    }

    // Parcelable implementation
    protected RequestOffer(Parcel in) {
        id = in.readLong();
        remoteId = in.readLong();
        weight = in.readDouble();
        certification_id = in.readLong();
        price = in.readDouble();
        packing_id = in.readLong();
        packingCount = in.readDouble();
        offer_identify = in.readString();
        offer_state = in.readString();
    }

    public static final Creator<RequestOffer> CREATOR = new Creator<RequestOffer>() {
        @Override
        public RequestOffer createFromParcel(Parcel in) {
            return new RequestOffer(in);
        }

        @Override
        public RequestOffer[] newArray(int size) {
            return new RequestOffer[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(remoteId);
        dest.writeDouble(weight);
        dest.writeLong(certification_id);
        dest.writeDouble(price);
        dest.writeLong(packing_id);
        dest.writeDouble(packingCount);
        dest.writeString(offer_identify);
        dest.writeString(offer_state);

    }

    @Override
    public int describeContents() {
        return 0;
    }
}

