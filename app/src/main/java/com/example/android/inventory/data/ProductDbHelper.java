package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper for the inventory app. Manages database creation and version management
 */

public class ProductDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ProductDbHelper.class.getSimpleName();

    //Name of the database file
    private static final String DATABASE_NAME = "store.db";

    //Database version
    private static final int DATABASE_VERSION = 1;

    //Construct a new instance of {@Link ProductDbHelper}
    //@param context of the app
    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //This is called when the database is created for the fits time
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creates a string that contains the SQL statement to create the inventory table
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + "("
                + ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductContract.ProductEntry.COLUMN_INV_NAME + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_INV_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + ProductContract.ProductEntry.COLUMN_INV_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductContract.ProductEntry.COLUMN_INV_SUPPLIER + " INTEGER NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_INV_SUPPLIER_PHONE + " INTEGER NOT NULL DEFAULT 0);";

        //Execute the SQL statement
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    //This is called when the database needs an upgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //The database is still at version 1, so there is nothing can be done here.
    }

}
