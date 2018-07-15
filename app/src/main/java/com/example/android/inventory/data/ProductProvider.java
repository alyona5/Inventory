package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class ProductProvider extends ContentProvider {

    //Tag for the log messages
    public static final String LOG_TAG = ProductContract.ProductEntry.class.getSimpleName();
    // URI matcher code for the content URI for the inventory table //
    private static final int PRODUCTS = 100;
    // URI matcher code for the content URI for a single product in the pets table //
    private static final int PRODUCT_ID = 101;
    /**
     * Uri matcher object to match a content URI to a corresponding code.
     * The input passed in the constructor represents the code to return for the root URI.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //static initializer. This is run the first time anything is called from the class
    static {
        //All paths added to the UriMatcher have a corresponding code to return when a match is found.
        sUriMatcher.addURI(ProductContract.ProductEntry.CONTENT_AUTHORITY, ProductContract.ProductEntry.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.ProductEntry.CONTENT_AUTHORITY, ProductContract.ProductEntry.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    private ProductDbHelper mDbHelper;

    //Initialize the provider and the database helper object
    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    //Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //This cursor will hold the result of the query
        Cursor cursor;

        //Figure out if the UriMatcher can match to the specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //Set the notification Uri on the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    //Insert new data into the provider with the given ContentValues.
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a product into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertProduct(Uri uri, ContentValues values) {
        //Check that the name is not null
        String name = values.getAsString(ProductContract.ProductEntry.COLUMN_INV_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Name field is empty");
        }

        Integer price = values.getAsInteger(ProductContract.ProductEntry.COLUMN_INV_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Valid price is required");
        }

        Integer quantity = values.getAsInteger(ProductContract.ProductEntry.COLUMN_INV_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Valid quantity is required");
        }

        Integer supplier = values.getAsInteger(ProductContract.ProductEntry.COLUMN_INV_SUPPLIER);
        if (supplier == null || !ProductContract.ProductEntry.isValidCompany(supplier)) {
            throw new IllegalArgumentException("Supplier field is empty");
        }

        Integer supplierPhone = values.getAsInteger(ProductContract.ProductEntry.COLUMN_INV_SUPPLIER_PHONE);
        if (supplierPhone != null && supplierPhone < 0) {
            throw new IllegalArgumentException("Valid phone number is required");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify all listeners that the data has changed
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    //Updates the data at the given selection and selection arguments, with the new ContentValues.
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProducts(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProducts(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProducts(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_INV_NAME)) {
            String name = values.getAsString(ProductContract.ProductEntry.COLUMN_INV_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Name field is empty");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_INV_PRICE)) {
            Integer price = values.getAsInteger(ProductContract.ProductEntry.COLUMN_INV_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Valid price is required");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_INV_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductContract.ProductEntry.COLUMN_INV_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Valid quantity is required");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_INV_SUPPLIER)) {
            Integer supplier = values.getAsInteger(ProductContract.ProductEntry.COLUMN_INV_SUPPLIER);
            if (supplier == null || !ProductContract.ProductEntry.isValidCompany(supplier)) {
                throw new IllegalArgumentException("Supplier field is empty");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_INV_SUPPLIER_PHONE)) {
            Integer supplierPhone = values.getAsInteger(ProductContract.ProductEntry.COLUMN_INV_SUPPLIER_PHONE);
            if (supplierPhone != null && supplierPhone < 0) {
                throw new IllegalArgumentException("Valid phone number is required");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Perform the update on the database and get the number of the rows affected
        int rowsUpdated = database.update(ProductContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        //If 1 or more rows are updated, then notify all the listeners that the data at the given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    //Delete the data at the given selection and selection arguments
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                //Delete all rows that match the selection and selection arguments
                rowsDeleted = database.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                //Delete a single row given by ID in the uri
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        //If 1 or more rows are deleted,  then notify all the listeners that the data at the given Uri has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }
}
