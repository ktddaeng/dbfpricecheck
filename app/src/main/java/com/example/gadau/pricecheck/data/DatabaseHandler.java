package com.example.gadau.pricecheck.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.example.gadau.pricecheck.R;
import com.example.gadau.pricecheck.data.DatabaseContract.InvEntry;
import com.example.gadau.pricecheck.data.DatabaseContract.OutputInvEntry;
import com.example.gadau.pricecheck.data.DatabaseContract.RestockEntry;
import com.example.gadau.pricecheck.data.DatabaseContract.OrderLogEntry;
import com.example.gadau.pricecheck.data.DatabaseContract.NewItemEntry;
import com.example.gadau.pricecheck.javadbf.DBFField;
import com.example.gadau.pricecheck.javadbf.DBFReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Created by gadau on 8/16/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "priceInventory";
    public static final int DATABASE_VERSION = 1;
    private String[] months = {"Month", "Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private static DatabaseHandler instance;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHandler getInstance(Context context) {
        if (instance == null ){
            instance = new DatabaseHandler(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INVENTORY = "CREATE TABLE " + InvEntry.TABLE_NAME + " ("
                + InvEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InvEntry.COLUMN_BARCODE + " TEXT, "
                + InvEntry.COLUMN_DESC + " TEXT, "
                + InvEntry.COLUMN_PRICE + " TEXT)";
        db.execSQL(CREATE_INVENTORY);

        String CREATE_LOG = "CREATE TABLE " +
                OrderLogEntry.TABLE_NAME + " ("
                + OrderLogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + OrderLogEntry.COLUMN_BARCODE + " TEXT, "
                + OrderLogEntry.COLUMN_VENDOR  + " TEXT, "
                + OrderLogEntry.COLUMN_QTY_RECEIVED  + " INTEGER, "
                + OrderLogEntry.COLUMN_DATE + " TEXT)";
        db.execSQL(CREATE_LOG);

        onCreateRestock(db);
        onCreateNewItem(db);
    }

    public void onCreateRestock(SQLiteDatabase db) {
        String CREATE_RESTOCK = "CREATE TABLE " + RestockEntry.TABLE_NAME + "("
                + RestockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RestockEntry.COLUMN_BARCODE + " TEXT, "
                + RestockEntry.COLUMN_DATE + " TEXT, "
                + RestockEntry.COLUMN_LOCATION + " TEXT, "
                + RestockEntry.COLUMN_QTY_S + " TEXT, "
                + RestockEntry.COLUMN_QTY_B + " TEXT, "
                + RestockEntry.COLUMN_OTHER1 + " TEXT, "
                + RestockEntry.COLUMN_OTHER2 + " TEXT, "
                + RestockEntry.COLUMN_OTHER3 + " TEXT, "
                + RestockEntry.COLUMN_OTHER4 + " TEXT)";
        db.execSQL(CREATE_RESTOCK);
    }

    public void onCreateNewItem(SQLiteDatabase db) {
        String CREATE_RESTOCK = "CREATE TABLE " + NewItemEntry.TABLE_NAME + "("
                + NewItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NewItemEntry.COLUMN_BARCODE + " TEXT, "
                + NewItemEntry.COLUMN_DESC + " TEXT, "
                + NewItemEntry.COLUMN_PRICE + " TEXT)";
        db.execSQL(CREATE_RESTOCK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + InvEntry.TABLE_NAME);
        onCreate(db);
    }

    private int getItemID (String id ) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                InvEntry._ID,
                InvEntry.COLUMN_BARCODE,
                InvEntry.COLUMN_DESC,
                InvEntry.COLUMN_PRICE
        };

        Cursor cursor = db.query(InvEntry.TABLE_NAME,
                projection,
                InvEntry._ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);
        if (cursor == null && cursor.moveToFirst()){
            return Integer.parseInt(cursor.getString(0));
        }
        return 0;
    }

    public DataItem getItemByID(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                InvEntry._ID,
                InvEntry.COLUMN_BARCODE,
                InvEntry.COLUMN_DESC,
                InvEntry.COLUMN_PRICE
        };

        Cursor cursor = db.query(InvEntry.TABLE_NAME,
                projection,
                InvEntry.COLUMN_BARCODE + "=?",
                new String[]{id},
                null, null, null, null);
        //Log.i("DB Handler", cursor.getString(1) + ", " + cursor.getString(2) + ", " + cursor.getString(3));
        if (cursor != null && cursor.moveToFirst()) {
            DataItem item = new DataItem(cursor.getString(1), cursor.getString(2),
                    cursor.getString(3));
            return item;
        }
        return null;
    }

    public DataItem getNewItembyID(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                NewItemEntry._ID,
                NewItemEntry.COLUMN_BARCODE,
                NewItemEntry.COLUMN_DESC,
                NewItemEntry.COLUMN_PRICE
        };

        Cursor cursor = db.query(NewItemEntry.TABLE_NAME,
                projection,
                NewItemEntry.COLUMN_BARCODE + "=?",
                new String[]{id},
                null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            DataItem item = new DataItem(cursor.getString(1), cursor.getString(2),
                    cursor.getString(3));
            return item;
        }
        return null;
    }

    public void addNewItem(DataItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NewItemEntry.COLUMN_BARCODE, item.getID());
        values.put(NewItemEntry.COLUMN_DESC, item.getDesc());
        values.put(NewItemEntry.COLUMN_PRICE, item.getPrice());

        db.insert(NewItemEntry.TABLE_NAME, NewItemEntry.COLUMN_BARCODE, values);
        db.close();
    }

    public void deleteNewItem(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NewItemEntry.TABLE_NAME, NewItemEntry.COLUMN_BARCODE + " = ?",
                new String[] {id});
        db.close();
    }

    public int getItemCount() {
        String countQuery = "SELECT * FROM " + InvEntry.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int ct = cursor.getCount();
        cursor.close();
        return ct;
    }

    public int getItemLogCount() {
        String countQuery = "SELECT * FROM " + OrderLogEntry.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int ct = cursor.getCount();
        cursor.close();
        return ct;
    }

    public Cursor getUnfinishCursor(){
        String selectQuery = "SELECT * FROM " + NewItemEntry.TABLE_NAME
                + " ORDER BY " + NewItemEntry.COLUMN_BARCODE;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public List<DataItem> getListOfDataItem(){
        List<DataItem> listItems = new ArrayList<>();
        Cursor cursor = getUnfinishCursor();

        DataItem di;
        if (cursor.moveToFirst()) {
            do {
                di = new DataItem();
                di.setID(cursor.getString(1));
                di.setDesc(cursor.getString(2));
                di.setPrice(cursor.getString(3));
                listItems.add(di);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return listItems;
    }

    public void clearNewItemTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + NewItemEntry.TABLE_NAME);
        onCreateNewItem(db);
    }

    public void exportUnfinishTable(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd__HH_mm",
                Locale.getDefault());
        String date = df.format(new Date());
        Cursor cursor = getUnfinishCursor();
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile();
        String filename = "NewItemTable_"+ date +".csv";

        //TODO: account for gaps and unfilled spaces in the original form
        try {
            File saveFile = new File(path, filename);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            int rowCount = cursor.getCount();
            int colCount = cursor.getColumnCount();
            if (rowCount > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < rowCount; i++) {
                    cursor.moveToPosition(i);
                    bw.write(cursor.getString(1) + ",,,");
                    bw.write(cursor.getString(2) + "," + cursor.getString(3));
                    bw.newLine();
                }
                bw.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<LogItem> getListofData(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<LogItem> listItems = new ArrayList<>();
        String[] projection = {
                OrderLogEntry._ID,
                OrderLogEntry.COLUMN_BARCODE,
                OrderLogEntry.COLUMN_VENDOR,
                OrderLogEntry.COLUMN_QTY_RECEIVED,
                OrderLogEntry.COLUMN_DATE
        };

        Cursor cursor = db.query(OrderLogEntry.TABLE_NAME,
                projection,
                OrderLogEntry.COLUMN_BARCODE + "=?",
                new String[]{id},
                null, null, OrderLogEntry._ID + " DESC", "5");

        LogItem di = null;
        if (cursor.moveToFirst()) {
            do {
                di = new LogItem();
                di.setID(cursor.getString(1));
                di.setVendor(cursor.getString(2));
                di.setReceive(cursor.getString(3));
                di.setDate(cursor.getString(4));
                listItems.add(di);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return listItems;
    }

    public void addRestockItem(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RestockEntry.COLUMN_BARCODE, id);

        db.insert(RestockEntry.TABLE_NAME, RestockEntry.COLUMN_BARCODE, values);
        db.close();
    }

    public void addRestockItem(RestockItem ri){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RestockEntry.COLUMN_BARCODE, ri.getID());
        values.put(RestockEntry.COLUMN_DATE, ri.getLo_logdate());
        values.put(RestockEntry.COLUMN_LOCATION, ri.getLo_location());
        values.put(RestockEntry.COLUMN_QTY_B, ri.getLo_bqty());
        values.put(RestockEntry.COLUMN_QTY_S, ri.getLo_sqty());
        values.put(RestockEntry.COLUMN_OTHER1, ri.getLo_other1());
        values.put(RestockEntry.COLUMN_OTHER2, ri.getLo_other2());
        values.put(RestockEntry.COLUMN_OTHER3, ri.getLo_other3());
        values.put(RestockEntry.COLUMN_OTHER4, ri.getLo_other4());

        db.insert(RestockEntry.TABLE_NAME, RestockEntry.COLUMN_BARCODE, values);
        db.close();
    }

    public void updateRestockItem(RestockItem ri){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(RestockEntry.TABLE_NAME, RestockEntry.COLUMN_BARCODE + " = ?",
                new String[] {ri.getID()});
        addRestockItem(ri);
    }

    public void deleteRestockItem(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(RestockEntry.TABLE_NAME, RestockEntry.COLUMN_BARCODE + " = ?",
                new String[] {id});
        db.close();
    }

    public boolean isOnRestock(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectRestockQuery = "SELECT COUNT(*) FROM " + RestockEntry.TABLE_NAME;
        String[] projection = {
                RestockEntry._ID,
                RestockEntry.COLUMN_BARCODE
        };

        Cursor cursor = db.query(RestockEntry.TABLE_NAME,
                projection,
                RestockEntry.COLUMN_BARCODE + "=?",
                new String[]{id},
                null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public RestockItem getRestockItem(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                RestockEntry._ID,
                RestockEntry.COLUMN_BARCODE,
                RestockEntry.COLUMN_DATE,
                RestockEntry.COLUMN_LOCATION,
                RestockEntry.COLUMN_QTY_S,
                RestockEntry.COLUMN_QTY_B,
                RestockEntry.COLUMN_OTHER1,
                RestockEntry.COLUMN_OTHER2,
                RestockEntry.COLUMN_OTHER3,
                RestockEntry.COLUMN_OTHER4
        };

                Cursor cursor = db.query(RestockEntry.TABLE_NAME,
                projection,
                RestockEntry.COLUMN_BARCODE + "=?",
                new String[]{id},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            RestockItem item = new RestockItem();
            item.setID(cursor.getString(1));
            item.setLo_logdate(cursor.getString(2));
            item.setLo_location(cursor.getString(3));
            item.setLo_sqty(cursor.getString(4));
            item.setLo_bqty(cursor.getString(5));
            item.setLo_other1(cursor.getString(6));
            item.setLo_other2(cursor.getString(7));
            item.setLo_other3(cursor.getString(8));
            item.setLo_other4(cursor.getString(9));
            return item;
        }
        return null;
    }

    public List<RestockItem> getRestockLog() {
        List<RestockItem> listItems = new ArrayList<>();
        Cursor cursor = getRestockLogCursor();

        RestockItem di = null;
        if (cursor.moveToFirst()) {
            do {
                di = new RestockItem();
                di.setID(cursor.getString(1));
                di.setDesc(cursor.getString(2));
                di.setPrice(cursor.getString(3));
                LogItem li = getListofData(di.getID()).get(0);
                RestockItem ri = getRestockItem(di.getID());
                if (li != null) {
                    di.setLo_desc(li.getVendor());
                    di.setLo_qty(li.getReceive());
                    di.setLo_date(li.getDate());
                    di.setLo_sqty(ri.getLo_sqty());
                    di.setLo_bqty(ri.getLo_bqty());
                    di.setLo_logdate(ri.getLo_logdate());
                }
                listItems.add(di);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return listItems;
    }

    public Cursor getRestockLogCursor() {
        String selectRestockQuery = "SELECT " + RestockEntry.COLUMN_BARCODE +  " FROM " + RestockEntry.TABLE_NAME;
        String selectQuery = "SELECT * FROM " + InvEntry.TABLE_NAME
                + " WHERE " + InvEntry.COLUMN_BARCODE + " IN ("
                + selectRestockQuery + ") ORDER BY " + InvEntry.COLUMN_BARCODE;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public void clearRestockTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + RestockEntry.TABLE_NAME);
        onCreateRestock(db);
    }

    public void exportRestockTable(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd__HH_mm",
                Locale.getDefault());
        String date = df.format(new Date());
        Cursor cursor = getRestockLogCursor();
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile();
        String filename = "RestockTable_"+ date +".csv";

        try {
            File saveFile = new File(path, filename);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            int rowCount = cursor.getCount();
            int colCount = cursor.getColumnCount();
            if (rowCount > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < colCount; i++){
                    if (i != colCount -1) {
                        bw.write(cursor.getColumnName(i) + ",");
                    } else {
                        bw.write(cursor.getColumnName(i));
                    }
                }
                bw.write(",logdate,location,showroom,backstore,other1,other2,other3,other4");
                bw.newLine();
                for (int i = 0; i < rowCount; i++) {
                    cursor.moveToPosition(i);
                    for (int j = 0; j < colCount; j++) {
                        if (j != colCount - 1)
                            bw.write(cursor.getString(j) + ",");
                        else
                            bw.write(cursor.getString(j));
                    }
                    RestockItem ri = getRestockItem(cursor.getString(1));
                    bw.write("," + ri.getLo_logdate() + "," + ri.getLo_location()
                            + "," + ri.getLo_sqty() + "," + ri.getLo_sqty()
                            + "," + ri.getLo_other1() + "," + ri.getLo_other2()
                            + "," + ri.getLo_other3() + "," + ri.getLo_other4());
                    bw.newLine();
                }
                bw.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + InvEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + OrderLogEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RestockEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NewItemEntry.TABLE_NAME);
        onCreate(db);
    }

    public String importDatabase(boolean master) {
        SQLiteDatabase db = this.getWritableDatabase();
        //String filename = "SCINVT.csv";
        //We need the following columns: ID(0), Desc(3), Price1(4)
        try {
            db.beginTransaction();
            //clear table
            clearDatabase();
            String filename = "SCINVT.DBF";
            final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile(), filename);
            if (!file.exists()){
                return "Cannot Find SCINVT.dbf";
            }

            FileInputStream in = new FileInputStream(file);
            DBFReader reader = new DBFReader(in);
            int fields = reader.getFieldCount();

            for (int i = 0; i < fields; i++) {
                DBFField field = reader.getField(i);
            }

            Object []rowObjects;

            while ((rowObjects = reader.nextRecord()) != null){
                //Log.i("DB Handler", rowObjects[0].toString().trim() + ", " + rowObjects[3].toString().trim() + ", " + rowObjects[4].toString().trim());
                ContentValues values = new ContentValues();
                values.put(InvEntry.COLUMN_BARCODE, rowObjects[0].toString().trim());
                values.put(InvEntry.COLUMN_DESC, rowObjects[3].toString().trim());
                values.put(InvEntry.COLUMN_PRICE, rowObjects[4].toString().trim());

                db.insertOrThrow(InvEntry.TABLE_NAME, InvEntry.COLUMN_BARCODE, values);
            }
            db.setTransactionSuccessful();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        try {
            db.beginTransaction();
            String filename = "STKADD.DBF";
            final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile(), filename);
            if (!file.exists()){
                return "Cannot Find STKADD.dbf";
            }

            FileInputStream in = new FileInputStream(file);
            DBFReader reader = new DBFReader(in);
            int fields = reader.getFieldCount();

            for (int i = 0; i < fields; i++) {
                DBFField field = reader.getField(i);
            }

            Object []rowObjects;

            while ((rowObjects = reader.nextRecord()) != null){
                //Log.i("DB Handler", rowObjects[0].toString().trim() + ", " + rowObjects[2].toString().trim() + ", " + rowObjects[10].toString().trim() + ", " + rowObjects[11].toString().trim());

                ContentValues values = new ContentValues();
                values.put(OrderLogEntry.COLUMN_BARCODE, rowObjects[0].toString().trim());
                String s = rowObjects[2].toString().trim();
                if (s.length() > 10){
                    s = s.substring(0,10);
                }
                /*
                int index = s.indexOf(".");
                if (index > 10) { index = 0; }
                String d = s.substring(0,index+3);
                */
                values.put(OrderLogEntry.COLUMN_VENDOR, s);
                values.put(OrderLogEntry.COLUMN_QTY_RECEIVED, rowObjects[10].toString().trim());
                String[] date = rowObjects[11].toString().split(" ");
                s = Arrays.asList(months).lastIndexOf(date[1])
                        + "/" + date[2]
                        + "/" + date[5].substring(2);
                values.put(OrderLogEntry.COLUMN_DATE, s);
                //values.put(KEY_DATE, rowObjects[11].toString().trim());
                db.insertOrThrow(OrderLogEntry.TABLE_NAME, OrderLogEntry.COLUMN_BARCODE, values);
            }
            db.setTransactionSuccessful();
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            //checkLogData();
        }

        /*
        try{
            CSVReader reader = new CSVReader(new FileReader(file), ',');
            List<String[]> allRows = reader.readAll();
            for (String[] row: allRows) {
                Log.i("DB Handler", row[0] + ", " + row[3] + ", " + row[4]);
                ContentValues values = new ContentValues();
                values.put(KEY_ITEMNO, row[0]);
                values.put(KEY_DESC, row[3]);
                values.put(KEY_PRICE, row[4]);
                db.insert(TABLE_INVENTORY, KEY_ITEMNO, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            //checkData();
        }
        */
        return "Data Sync Successful!";
    }

    private void checkData() {
        String selectQuery = "SELECT * FROM " + InvEntry.TABLE_NAME;// + " ORDER BY " + KEY_QTY;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Log.i("DB Handler", cursor.getString(1) + ", " + cursor.getString(2) + ", " + cursor.getString(3));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void checkLogData() {
        Log.i("DB Handler", "We are here!");
        Log.i("DB Handler", getItemLogCount() + " Items Found");
        String selectQuery = "SELECT * FROM " + OrderLogEntry.TABLE_NAME;// + " ORDER BY " + KEY_QTY;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Log.i("DB Handler", cursor.getString(1) + ", " + cursor.getString(2) + ", " + cursor.getString(3) + ", " + cursor.getString(4));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}
