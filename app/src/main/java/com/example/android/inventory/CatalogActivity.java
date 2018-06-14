package com.example.android.inventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.inventory.data.ProductContract;
import com.example.android.inventory.data.ProductDbHelper;

import java.util.PriorityQueue;

//Displays list of products that were entered and stored in the app

public class CatalogActivity extends AppCompatActivity {

    private ProductDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        //Setup FAB to open the EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        mDbHelper = new ProductDbHelper(this);
    }
    @Override
    protected void onStart(){
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the products database.
     */

    private void displayDatabaseInfo(){

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_INV_NAME,
                ProductContract.ProductEntry.COLUMN_INV_PRICE,
                ProductContract.ProductEntry.COLUMN_INV_QUANTITY,
                ProductContract.ProductEntry.COLUMN_INV_SUPPLIER,
                ProductContract.ProductEntry.COLUMN_INV_SUPPLIER_PHONE
        };

        Cursor cursor = db.query(
                ProductContract.ProductEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        //Display the number of rows in the cursor
        TextView displayView = (TextView) findViewById(R.id.text_view_product);

        try {
            displayView.setText("The inventory table contains: " + cursor.getCount() + " products.\n\n");
            displayView.append(ProductContract.ProductEntry._ID + " - " +
                    ProductContract.ProductEntry.COLUMN_INV_NAME + " - " +
                    ProductContract.ProductEntry.COLUMN_INV_PRICE + " - " +
                    ProductContract.ProductEntry.COLUMN_INV_QUANTITY + " - " +
                    ProductContract.ProductEntry.COLUMN_INV_SUPPLIER + " - " +
                    ProductContract.ProductEntry.COLUMN_INV_SUPPLIER_PHONE + "\n");

            int idColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_INV_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_INV_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_INV_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_INV_SUPPLIER);
            int phoneSupplierColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_INV_SUPPLIER_PHONE);

            while (cursor.moveToNext()){
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                int currentSupplier = cursor.getInt(supplierColumnIndex);
                int currentPhoneSupplier = cursor.getInt(phoneSupplierColumnIndex);
                displayView.append(("\n" + currentID + " - " +
                currentName + " - " +
                currentPrice + " - " +
                currentQuantity + " - " +
                currentSupplier + " - " +
                currentPhoneSupplier));
            }
        } finally {
            cursor.close();
        }
    }

    private void insertProduct(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        //Create a ContentValues object where column names are the keys, and
        //product's attributes are the values
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_INV_NAME, "Laptop");
        values.put(ProductContract.ProductEntry.COLUMN_INV_PRICE, 600);
        values.put(ProductContract.ProductEntry.COLUMN_INV_QUANTITY, 10);
        values.put(ProductContract.ProductEntry.COLUMN_INV_SUPPLIER, ProductContract.ProductEntry.UNKNOWN_SUPPLIER);
        values.put(ProductContract.ProductEntry.COLUMN_INV_SUPPLIER_PHONE, 0305550330);

        long newRowId = db.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()){
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertProduct();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
