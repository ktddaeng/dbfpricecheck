package com.example.gadau.pricecheck.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gadau on 8/16/2017.
 */

public class DataItem implements Parcelable{
    private String ID;
    private String desc;
    private String price;

    public DataItem() {
        ID = "123451234512";
        desc = "UNKNOWN OBJECT";
        price = "123.45";
    }

    public DataItem(String ID, String desc, String price) {
        this.ID = ID;
        this.desc = desc;
        this.price = price;
    }

    public DataItem(Parcel pc) {
        ID = pc.readString();
        desc = pc.readString();
        price = pc.readString();
    }

    public String getID() {
        return ID;
    }

    public String getDesc() {
        return desc;
    }

    public String getPrice() {
        return price;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(desc);
        dest.writeString(price);
    }

    public static final Parcelable.Creator<DataItem> CREATOR = new Parcelable.Creator<DataItem>(){
        public DataItem createFromParcel(Parcel pc){
            return new DataItem(pc);
        }
        public DataItem[] newArray(int size) {
            return new DataItem[size];
        }
    };
}
