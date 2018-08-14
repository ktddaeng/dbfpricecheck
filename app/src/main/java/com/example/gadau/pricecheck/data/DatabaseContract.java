package com.example.gadau.pricecheck.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for Price Check app
 */
public final class DatabaseContract {

    /**
     * An empty constructor to avoid having anyone accidentally instantiate
     */
    private DatabaseContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.gadau.pricecheck";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_INV = "inv";
    public static final String PATH_OUTINV = "outputinv";
    public static final String PATH_LOG = "shiplog";
    public static final String PATH_RESTOCK = "restock";
    public static final String PATH_NEW = "newitem";

    /**
     * Inner class to define the inventory table
     */
    public static final class InvEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INV);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INV;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INV;

        public static final String TABLE_NAME = "inventory";

        public static final String _ID = BaseColumns._ID;

        /**
         * Primary key as barcode since this is the "master" list
         */
        public static final String COLUMN_BARCODE = "barcode";

        public static final String COLUMN_DESC = "description";

        public static final String COLUMN_PRICE = "price";
    }

    /**
     * Inner class representing table of stocked items
     * Includes data on quantity currently in the stockroom & showroom
     * and location
     */
    public static final class OutputInvEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_OUTINV);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_OUTINV;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_OUTINV;

        public static final String TABLE_NAME ="OutputInv";

        /**
         *  Unique id for every entry (not the barcode)
         */
        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_BARCODE = "barcode";

        /**
         * Column representing quantity of item in stockroom
         */
        public static final String COLUMN_BQTY = "stockroom_qty";

        /**
         * Column representing quantity of item in the showroom
         */
        public static final String COLUMN_SQTY = "showroom_qty";

        /**
         * Column representing the location of the item in showroom
         */
        public static final String COLUMN_LOC = "location";

        /**
         * Column representing the latest log date of the stockroom
         */
        public static final String COLUMN_DATE_B = "log_date_b";

        /**
         * Column representing the latest log date of the showroom
         */
        public static final String COLUMN_DATE_S = "log_date_s";
    }

    /**
     * Inner class to define table with shipped log entries
     */
    public static final class OrderLogEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_LOG);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOG;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOG;

        public static final String TABLE_NAME = "ShipLog";


        /**
         *  Unique id for every entry (NOT barcode)
         */
        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_BARCODE = "barcode";

        public static final String COLUMN_VENDOR = "vendor";

        /**
         * Column representing the qty of the item received
         */
        public static final String COLUMN_QTY_RECEIVED = "qty_received";

        /**
         * Column representing the log date
         * todo timestamp? (possibly make this primary )
         */
        public static final String COLUMN_DATE = "date";
    }

    /**
     * Inner class to define the a table with entries of items to
     * restock
     */
    public static final class RestockEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_RESTOCK);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RESTOCK;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RESTOCK;

        public static final String TABLE_NAME = "restock";

        /**
         * ID for restock table (not barcode)
         */
        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_BARCODE = "barcode";

        public static final String COLUMN_DATE = "date";

        /**
         * Quantity to restock for stockroom
         */
        public static final String COLUMN_QTY_B = "stockroom_qty";

        /**
         * Quantity to restock for showroom
         */
        public static final String COLUMN_QTY_S = "showroom_qty";

        public static final String COLUMN_LOCATION = "location";

        public static final String COLUMN_OTHER1 = "other1";

        public static final String COLUMN_OTHER2 = "other2";

        public static final String COLUMN_OTHER3 = "other3";

        public static final String COLUMN_OTHER4 = "other4";
    }

    public static final class NewItemEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NEW);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEW;

        public static final String TABLE_NAME = "newitem";

        /**
         * Primary key (not barcode)
         */
        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_BARCODE = "barcode";

        public static final String COLUMN_DESC = "description";

        public static final String COLUMN_PRICE = "price";
    }
}
