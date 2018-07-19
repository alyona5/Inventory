package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract;
import com.example.android.inventory.data.ProductDbHelper;

import java.net.ProxySelector;
import java.util.PriorityQueue;

//Displays list of products that were entered and stored in the app

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Identifier for the product data loader
    private static final int PRODUCT_LOADER = 0;

    //Adapter for the list view
    ProductCursorAdapter mCursorAdapter;

    //EditText field to enter the quantity of the product
    private EditText mProductQuantityEditText;

    private EditText mPhoneOfSupplier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        mProductQuantityEditText = (EditText) findViewById(R.id.quantity);

        //Setup FAB to open the EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView productListView = (ListView) findViewById(R.id.list);

        //Find and set empty view on the ListView, so that it only shows when the list has 0 items
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        //Setup an adapter to create a list item for each row of pet data in cursor
        mCursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(mCursorAdapter);

        //Set up item click listener
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentProductUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, id);

                intent.setData(currentProductUri);

                startActivity(intent);
            }
        });

        //Kick off the loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    public void onClick(){
        //The button increases the quantity by 1
        Button increaseButton = (Button)findViewById(R.id.increase);
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText quantityEditText = findViewById(R.id.quantity);
                String quantityString = quantityEditText.getText().toString();
                int quantityInt = Integer.parseInt(quantityString);
                quantityInt++;
                quantityEditText.setText(String.valueOf(quantityInt));

            }
        });

        Button decreaseButton = (Button)findViewById(R.id.decrease);
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText quantityEditText = findViewById(R.id.quantity);
                String quantityString = quantityEditText.getText().toString();
                int quantityInt = Integer.parseInt(quantityString);
                quantityInt--;
                quantityEditText.setText(String.valueOf(quantityInt));
            }
        });

        Button callButton = (Button) findViewById(R.id.call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText phoneEditText = findViewById(R.id.supplier_phone);
                String phone = phoneEditText.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_phone_app, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Helper method to insert hard coded data into the database, for debugging purposes only
    private void insertProduct() {
        //Create a ContentValues object where column names are the keys, and
        //product's attributes are the values
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_INV_NAME, "Inferno");
        values.put(ProductContract.ProductEntry.COLUMN_INV_PRICE, 600);
        values.put(ProductContract.ProductEntry.COLUMN_INV_QUANTITY, 10);
        values.put(ProductContract.ProductEntry.COLUMN_INV_SUPPLIER, ProductContract.ProductEntry.COMPANY_1);
        values.put(ProductContract.ProductEntry.COLUMN_INV_SUPPLIER_PHONE, 0305550330);

        Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
    }

    //Helper method to delete all pets
    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(ProductContract.ProductEntry.CONTENT_URI, null, null);
        Log.v("Catalog Activity ", rowsDeleted + "rows deleted from the database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertProduct();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Projection that specifies the columns from the table
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_INV_NAME,
                ProductContract.ProductEntry.COLUMN_INV_PRICE,
                ProductContract.ProductEntry.COLUMN_INV_QUANTITY};

        //This loader will execute the ContentProvider's query method on background thread
        return new CursorLoader(this,
                ProductContract.ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Callback is called when the data is needed to be deleted
        mCursorAdapter.swapCursor(null);
    }

}
