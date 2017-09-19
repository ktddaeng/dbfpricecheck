package com.example.gadau.pricecheck.data;

import android.os.Parcel;

/**
 * Created by gadau on 9/14/2017.
 */

public class RestockItem extends DataItem {
    String lo_desc;
    String lo_qty;
    String lo_date;

    public RestockItem() {
    }

    public RestockItem(String ID, String desc, String price, String lo_desc, String lo_qty, String lo_date) {
        super(ID, desc, price);

        this.lo_desc = lo_desc;
        this.lo_qty = lo_qty;
        this.lo_date = lo_date;
    }

    public String getLo_desc() {
        return lo_desc;
    }

    public String getLo_qty() {
        return lo_qty;
    }

    public String getLo_date() {
        return lo_date;
    }

    public DataItem getDataItem(){
        return new DataItem(this.getID(), this.getDesc(), this.getPrice());
    }

    public void setLo_desc(String lo_desc) {
        this.lo_desc = lo_desc;
    }

    public void setLo_qty(String lo_qty) {
        this.lo_qty = lo_qty;
    }

    public void setLo_date(String lo_date) {
        this.lo_date = lo_date;
    }
}
