package com.example.gadau.pricecheck.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gadau on 8/16/2017.
 */

public class DataItem implements Parcelable{
    private String ID_;
    private String desc_;
    private String price_;

    /**
     * Showroom quantity
     */
    private String sqty_;

    /**
     * Backroom quantity
     */
    private String bqty_;

    /**
     * Showroom location
     */
    private String location_;

    public DataItem() {
        ID_ = "123451234512";
        desc_ = "UNKNOWN OBJECT";
        price_ = "123.45";
        sqty_ = null;
        bqty_ = null;
        location_ = null;
    }

    public DataItem(String ID, String desc, String price) {
        this.ID_ = ID;
        this.desc_ = desc;
        this.price_ = price;
    }

    public DataItem(String ID, String desc, String price, String sqty, String bqty, String location) {
        this.ID_ = ID;
        this.desc_ = desc;
        this.price_ = price;
        this.sqty_ = sqty;
        this.bqty_ = bqty;
        this.location_ = location;
    }

    public DataItem(Parcel pc) {
        ID_ = pc.readString();
        desc_ = pc.readString();
        price_ = pc.readString();
        sqty_ = pc.readString();
        bqty_ = pc.readString();
        location_ = pc.readString();
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
        return sqty_;
    }

    public String getBQty() {
        return bqty_;
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
        sqty_= sQty;
    }

    public void setBQty(String bQty) {
        bqty_ = bQty;
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
        dest.writeString(sqty_);
        dest.writeString(bqty_);
        dest.writeString(location_);
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
