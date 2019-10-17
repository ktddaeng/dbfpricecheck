package com.example.gadau.pricecheck.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by gadau on 8/16/2017.
 */

public class DataItem implements Parcelable {
    private String ID_;
    private String desc_;
    private String price_;

    /**
     * Showroom quantity
     */
    private String sQty_;

    /**
     * Backroom quantity
     */
    private String bQty_;

    private String bDate_;
    private String sDate_;

    /**
     * Showroom location
     */
    private String location_;

    public DataItem() {
        ID_ = "123451234512";
        desc_ = "UNKNOWN OBJECT";
        price_ = "123.45";
        sQty_ = null;
        bQty_ = null;
        location_ = null;
    }

    public DataItem(String ID, String desc, String price) {
        this.ID_ = ID;
        this.desc_ = desc;
        this.price_ = price;
        this.sQty_ = null;
        this.bQty_ = null;
        this.sDate_ = null;
        this.bDate_ = null;
        this.location_ = null;
    }

    public DataItem(Parcel pc) {
        ID_ = pc.readString();
        desc_ = pc.readString();
        price_ = pc.readString();
        sQty_ = pc.readString();
        bQty_ = pc.readString();
        location_ = pc.readString();
        sDate_ = pc.readString();
        bDate_ = pc.readString();
    }

    public String getID() {
        return ID_;
    }

    public String getDesc() {
        return desc_;
    }

    public String getPrice() {
        return price_;
    }

    public String getSQty() {
        if (sQty_ == null) {
            return "";
        }
        return sQty_;
    }

    public String getBQty() {
        if (bQty_ == null) {
            return "";
        }
        return bQty_;
    }

    public String getSDateString() {
        if (sDate_ == null) {
            return "";
        }
       return sDate_;
    }

    public String getBDateString() {
        if (bDate_ == null) {
            return "";
        }
        return bDate_;
    }

    public String getLocation() {
        return location_;
    }

    public void setID(String ID) {
        this.ID_ = ID;
    }

    public void setDesc(String desc) {
        this.desc_ = desc;
    }

    public void setPrice(String price) {
        this.price_ = price;
    }

    public void setSQty(String sQty) {
        sQty_ = sQty;
    }

    public void setBQty(String bQty) {
        bQty_ = bQty;
    }

    public void setSDate (String sDate) {
        if (sDate != null) {
            sDate_ = sDate;
        } else {
            sDate_ = "MEMEULTRA";
        }
    }

    public void setBDate (String bDate) {
        bDate_ = bDate;
    }

    public void setLocation(String location) {
        location_ = location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID_);
        dest.writeString(desc_);
        dest.writeString(price_);
        dest.writeString(sQty_);
        dest.writeString(bQty_);
        dest.writeString(location_);
        dest.writeString(sDate_);
        dest.writeString(bDate_);
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
