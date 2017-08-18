package com.example.gadau.pricecheck.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
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
    public static final int DATABASE_VERSION = 1;

    private static final String KEY_ID = "inventory_id";
    private static final String KEY_ITEMNO = "iventory_barcode";
    private static final String KEY_DESC = "inventory_desc";
    private static final String KEY_PRICE = "inventory_price";

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

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    public void importDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        //clear table
        clearDatabase();

        String filename = "SCINVT.csv";
        final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile(), filename);
        //TODO: Convert DBF into SQLite
        //Currently cannot import DBF directly into app, must be converted into CSV using Excel
        //We need the following columns: ID(0), Desc(3), Price1(4)
        //Remember to skip first line
        Log.i("DB Handler: ", "We are here!");
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

    }

    public void checkData() {
        String selectQuery = "SELECT * FROM " + TABLE_INVENTORY ;// + " ORDER BY " + KEY_QTY;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        DataItem di = null;
        if (cursor.moveToFirst()) {
            do {
                Log.i("DB Handler", cursor.getString(1) + ", " + cursor.getString(2) + ", " + cursor.getString(3));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

}