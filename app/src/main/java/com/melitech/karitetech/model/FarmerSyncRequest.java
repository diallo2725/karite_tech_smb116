package com.melitech.karitetech.model;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FarmerSyncRequest implements Parcelable {

    @SerializedName("farmers")
    private List<Farmer> farmers;

    public FarmerSyncRequest(List<Farmer> farmers) {
        this.farmers = farmers;
    }

    protected FarmerSyncRequest(Parcel in) {
        farmers = in.createTypedArrayList(Farmer.CREATOR);
    }

    public static final Creator<FarmerSyncRequest> CREATOR = new Creator<FarmerSyncRequest>() {
        @Override
        public FarmerSyncRequest createFromParcel(Parcel in) {
            return new FarmerSyncRequest(in);
        }

        @Override
        public FarmerSyncRequest[] newArray(int size) {
            return new FarmerSyncRequest[size];
        }
    };

    public List<Farmer> getFarmers() {
        return farmers;
    }

    public void setFarmers(List<Farmer> farmers) {
        this.farmers = farmers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeTypedList(farmers);
    }
}
