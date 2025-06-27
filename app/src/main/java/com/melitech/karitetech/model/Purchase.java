package com.melitech.karitetech.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Purchase implements Parcelable {
    private transient long  id;
    @SerializedName("id")
    private String remoteid;
    @SerializedName("weight")
    private String weight;
    @SerializedName("quality")
    private String quality;
    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("price")
    private String price;

    @SerializedName("isMixte")
    private boolean isMixte;

    @SerializedName("cash_amount")
    private String cash;
    @SerializedName("momo_amount")
    private String amount;

    private String fullname;
    private String picture;
    private String phone;
    private String phonePayment;
    @SerializedName("farmer_id")
    private long farmerId;


    public Purchase() {}

    protected Purchase(Parcel in) {
        id = in.readLong();
        remoteid = in.readString();
        weight = in.readString();
        quality = in.readString();
        paymentMethod = in.readString();
        price = in.readString();
        amount = in.readString();
        cash = in.readString();
        fullname = in.readString();
        picture = in.readString();
        phone = in.readString();
        phonePayment = in.readString();
        farmerId = in.readLong();
    }

    public static final Creator<Purchase> CREATOR = new Creator<Purchase>() {
        @Override
        public Purchase createFromParcel(Parcel in) {
            return new Purchase(in);
        }

        @Override
        public Purchase[] newArray(int size) {
            return new Purchase[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(remoteid);
        dest.writeString(weight);
        dest.writeString(quality);
        dest.writeString(paymentMethod);
        dest.writeString(price);
        dest.writeString(amount);
        dest.writeString(cash);
        dest.writeString(fullname);
        dest.writeString(picture);
        dest.writeString(phone);
        dest.writeString(phonePayment);
        dest.writeLong(farmerId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters et Setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getRemoteid() { return remoteid; }
    public void setRemoteid(String remoteid) { this.remoteid = remoteid; }

    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }

    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getCash() { return cash; }
    public void setCash(String cash) { this.cash = cash; }


    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPhonePayment() { return phonePayment; }
    public void setPhonePayment(String phonePayment) { this.phonePayment = phonePayment; }

    public long getFarmerId() { return farmerId; }
    public void setFarmerId(long farmerId) { this.farmerId = farmerId; }

    public boolean getisMixte() { return isMixte; }
    public void setIsMixte(boolean mixte) { isMixte = mixte; }
}

