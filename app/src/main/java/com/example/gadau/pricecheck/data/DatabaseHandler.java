package com.example.gadau.pricecheck.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.example.gadau.pricecheck.javadbf.DBFField;
import com.example.gadau.pricecheck.javadbf.DBFReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Created by gadau on 8/16/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "priceInventory";
    public static final String TABLE_INVENTORY = "inventory";
    public static final String TABLE_LOG = "shiplog";
    public static final int DATABASE_VERSION = 1;
    private String[] months = {"Month", "Jan", "Feb", "Mar", "Apr",
                    "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private static final String KEY_ID = "inventory_id";
    private static final String KEY_ITEMNO = "iventory_barcode";
    private static final String KEY_DESC = "inventory_desc";
    private static final String KEY_PRICE = "inventory_price";
    private static final String KEY_VENDOR = "log_vendor";
    private static final String KEY_RECEIVED = "log_received";
    private static final String KEY_DATE = "log_date";

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
        String CREATE_INVENTORY = "CREATE TABLE " +
                TABLE_INVENTORY + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_ITEMNO + " TEXT, " + KEY_DESC + " TEXT, " + KEY_PRICE + " TEXT)";
        db.execSQL(CREATE_INVENTORY);

        String CREATE_LOG = "CREATE TABLE " +
                TABLE_LOG + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_ITEMNO + " TEXT, " + KEY_VENDOR  + " TEXT, " + KEY_RECEIVED  + " INTEGER, " + KEY_DATE + " TEXT)";
        db.execSQL(CREATE_LOG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    public DataItem getItem(int id ) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_INVENTORY,
                new String[]{ KEY_ID, KEY_ITEMNO, KEY_DESC, KEY_PRICE},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);
        if (cursor == null && cursor.moveToFirst()){
            DataItem item = new DataItem(cursor.getString(1), cursor.getString(2),
                    cursor.getString(3));
            return item;
        }
        return null;
    }


    public DataItem getItemByID(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_INVENTORY,
                new String[]{ KEY_ID, KEY_ITEMNO, KEY_DESC, KEY_PRICE},
                KEY_ITEMNO + "=?",
                new String[]{id},
                null, null, null, null);
        Log.i("DB Handler", "Counted " + getItemCount() + " items");
        //Log.i("DB Handler", cursor.getString(1) + ", " + cursor.getString(2) + ", " + cursor.getString(3));
        if (cursor != null && cursor.moveToFirst()) {
            DataItem item = new DataItem(cursor.getString(1), cursor.getString(2),
                    cursor.getString(3));
            return item;
        }
        return null;
    }

    private void addItem(DataItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ITEMNO, item.getID());
        values.put(KEY_DESC, item.getDesc());
        values.put(KEY_PRICE, item.getPrice());

        db.insert(TABLE_INVENTORY, KEY_ITEMNO, values);
        db.close();
    }

    public int getItemCount() {
        String countQuery = "SELECT * FROM " + TABLE_INVENTORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int ct = cursor.getCount();
        cursor.close();
        return ct;
    }

    public int getItemLogCount() {
        String countQuery = "SELECT * FROM " + TABLE_LOG;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int ct = cursor.getCount();
        cursor.close();
        return ct;
    }

    public List<LogItem> getListofData(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<LogItem> listItems = new ArrayList<>();
        Cursor cursor = db.query(TABLE_LOG,
                new String[]{ KEY_ID, KEY_ITEMNO, KEY_VENDOR, KEY_RECEIVED, KEY_DATE},
                KEY_ITEMNO + "=?",
                new String[]{id},
                null, null, KEY_DATE + " ASC", "5");

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

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG);
        onCreate(db);
    }

    public void importDatabase(boolean master) {
        SQLiteDatabase db = this.getWritableDatabase();

        //String filename = "SCINVT.csv";

        //TODO: Convert DBF into SQLite, save to internal CSV file
        //Currently cannot import DBF directly into app, must be converted into CSV using Excel
        //We need the following columns: ID(0), Desc(3), Price1(4)
        try {
            db.beginTransaction();
            //clear table
            clearDatabase();
            String filename = "SCINVT.dbf";
            final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile(), filename);

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
                values.put(KEY_ITEMNO, rowObjects[0].toString().trim());
                values.put(KEY_DESC, rowObjects[3].toString().trim());
                values.put(KEY_PRICE, rowObjects[4].toString().trim());

                db.insertOrThrow(TABLE_INVENTORY, KEY_ITEMNO, values);
            }
            db.setTransactionSuccessful();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        //TODO: Load Inventory Exchange
        try {
            db.beginTransaction();
            String filename = "STKADD.dbf";
            final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile(), filename);

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
                values.put(KEY_ITEMNO, rowObjects[0].toString().trim());
                String s = rowObjects[2].toString().trim();
                int index = s.indexOf(".");
                if (index > 10) { index = 0; }
                String d = s.substring(0,index+3);
                values.put(KEY_VENDOR, d);
                values.put(KEY_RECEIVED, rowObjects[10].toString().trim());
                String[] date = rowObjects[11].toString().split(" ");
                s = Arrays.asList(months).lastIndexOf(date[1])
                        + "/" + date[2]
                        + "/" + date[5].substring(2);
                values.put(KEY_DATE, s);
                //values.put(KEY_DATE, rowObjects[11].toString().trim());
                db.insertOrThrow(TABLE_LOG, KEY_ITEMNO, values);
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
    }

    public void checkData() {
        String selectQuery = "SELECT * FROM " + TABLE_INVENTORY ;// + " ORDER BY " + KEY_QTY;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Log.i("DB Handler", cursor.getString(1) + ", " + cursor.getString(2) + ", " + cursor.getString(3));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }


    public void checkLogData() {
        Log.i("DB Handler", "We are here!");
        Log.i("DB Handler", getItemLogCount() + " Items Found");
        String selectQuery = "SELECT * FROM " + TABLE_LOG;// + " ORDER BY " + KEY_QTY;

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