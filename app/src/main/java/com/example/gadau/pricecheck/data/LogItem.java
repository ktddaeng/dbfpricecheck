package com.example.gadau.pricecheck.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gadau on 8/18/2017.
 */

public class LogItem implements Parcelable{
    private String ID;
    private String vendor;
    private String receive;
    private String date;

    public LogItem() {
        ID = "123451234512";
        vendor = "UNKNOWN OBJECT";
        receive = "123.45";
        date = "03/22/1996";
    }

    public LogItem(String ID, String vendor, String receive, String date) {
        this.ID = ID;
        this.vendor = vendor;
        this.receive = receive;
        this.date = date;
    }

    public LogItem(Parcel pc) {
        ID = pc.readString();
        vendor = pc.readString();
        receive = pc.readString();
        date = pc.readString();
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive) {
        this.receive = receive;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static Creator<DataItem> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(vendor);
        dest.writeString(receive);
        dest.writeString(date);
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
