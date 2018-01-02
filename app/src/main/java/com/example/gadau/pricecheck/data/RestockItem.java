package com.example.gadau.pricecheck.data;

import android.os.Parcel;

/**
 * Created by gadau on 9/14/2017.
 */

public class RestockItem extends DataItem {
    String lo_desc;
    String lo_qty;
    String lo_date;
    String lo_logdate;
    String lo_location;
    String lo_bqty;
    String lo_sqty;
    String lo_other1;
    String lo_other2;
    String lo_other3;
    String lo_other4;

    public RestockItem() {
        super();
        lo_desc = "--";
        lo_qty = "--";
        lo_date = "--";
        lo_logdate = "--";
        lo_location = "--";
        lo_bqty = "--";
        lo_sqty = "--";
        lo_other1 = "--";
        lo_other2 = "--";
        lo_other3 = "--";
        lo_other4 = "--";
    }

    public RestockItem(String ID, String desc, String price, String lo_desc,
                       String lo_qty, String lo_date, String lo_logdate, String lo_location, String lo_bqty,
                       String lo_sqty, String lo_other1, String lo_other2, String lo_other3, String lo_other4) {
        super(ID, desc, price);

        this.lo_desc = lo_desc;
        this.lo_qty = lo_qty;
        this.lo_date = lo_date;
        this.lo_logdate = lo_logdate;
        this.lo_location = lo_location;
        this.lo_sqty = lo_sqty;
        this.lo_bqty = lo_bqty;
        this.lo_other1 = lo_other1;
        this.lo_other2 = lo_other2;
        this.lo_other3 = lo_other3;
        this.lo_other4 = lo_other4;
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

    public String getLo_logdate() {
        return lo_logdate;
    }

    public String getLo_location() { return lo_location;  }

    public String getLo_bqty() { return lo_bqty; }

    public String getLo_sqty() { return lo_sqty;  }

    public String getLo_other1() { return lo_other1; }

    public String getLo_other2() { return lo_other2;  }

    public String getLo_other3() { return lo_other3;  }

    public String getLo_other4() { return lo_other4;  }

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

    public void setLo_logdate(String lo_logdate) {
        this.lo_logdate = lo_logdate;
    }

    public void setLo_location(String lo_location) { this.lo_location = lo_location; }

    public void setLo_bqty(String lo_bqty) { this.lo_bqty = lo_bqty; }

    public void setLo_sqty(String lo_sqty) { this.lo_sqty = lo_sqty; }

    public void setLo_other1(String lo_other1) { this.lo_other1 = lo_other1; }

    public void setLo_other2(String lo_other2) { this.lo_other2 = lo_other2; }

    public void setLo_other3(String lo_other3) {  this.lo_other3 = lo_other3;  }

    public void setLo_other4(String lo_other4) { this.lo_other4 = lo_other4;  }
}
