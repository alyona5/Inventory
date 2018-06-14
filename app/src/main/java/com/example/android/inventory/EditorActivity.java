package com.example.android.inventory;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract;
import com.example.android.inventory.data.ProductDbHelper;

/**
 * Allows user to create a new product in the db or update the current one.
 */

public class EditorActivity extends AppCompatActivity{

    //EditText field to enter the name of the product
    private EditText mProductNameEditText;

    //EditText field to enter the price of the product
    private EditText mProductPriceEditText;

    //EditText field to enter the quantity of the product
    private EditText mProductQuantityEditText;

    //EditText field to enter the name of the supplier
    private Spinner mNameOfSupplier;

    //EditText field to enter the phone number of the supplier
    private EditText mPhoneOfSupplier;

    /**
     * There are only 3 suppliers the company works with and the
     * possible values are:
     * Company_1  for Billy Co., Company_2 for Dilly Inc., Company_3 for Willy Ltd.
     */
    private int mSupplier = ProductContract.ProductEntry.COMPANY_1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Find all relevant views that will be needed to read the the input
        mProductNameEditText = (EditText) findViewById(R.id.product_name);
        mProductPriceEditText = (EditText) findViewById(R.id.price);
        mProductQuantityEditText = (EditText) findViewById(R.id.quantity);
        mNameOfSupplier = (Spinner) findViewById(R.id.spinner_supplier_name);
        mPhoneOfSupplier = (EditText) findViewById(R.id.supplier_phone);

        setupSpinner();
    }

    //Setup the spinner that allows the user to select the company name from the dropdown list
    private void setupSpinner() {
        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);

        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        //Applying adapter to the spinner
        mNameOfSupplier.setAdapter(supplierSpinnerAdapter);

        mNameOfSupplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)){
                    if (selection.equals(getString(R.string.supplier_1))) {
                        mSupplier = ProductContract.ProductEntry.COMPANY_1;
                    } else if (selection.equals(getString(R.string.supplier_2))){
                        mSupplier = ProductContract.ProductEntry.COMPANY_2;
                    }else{
                        mSupplier = ProductContract.ProductEntry.COMPANY_3;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            mSupplier = ProductContract.ProductEntry.UNKNOWN_SUPPLIER;
            }
        });
    }

    private void insertProduct(){
        String nameString = mProductNameEditText.getText().toString().trim();
        String priceString = mProductPriceEditText.getText().toString().trim();
        String quantityString = mProductQuantityEditText.getText().toString().trim();
        String phoneString = mPhoneOfSupplier.getText().toString().trim();
        int price = Integer.parseInt(priceString);
        int quantity = Integer.parseInt(quantityString);
        int phone = Integer.parseInt(phoneString);

        ProductDbHelper mDbHelper = new ProductDbHelper(this);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //ContentValue object where the column names are the keys and product attributes from the editor are the values
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_INV_NAME, nameString);
        values.put(ProductContract.ProductEntry.COLUMN_INV_PRICE, price);
        values.put(ProductContract.ProductEntry.COLUMN_INV_QUANTITY, quantity);
        values.put(ProductContract.ProductEntry.COLUMN_INV_SUPPLIER, mSupplier);
        values.put(ProductContract.ProductEntry.COLUMN_INV_SUPPLIER_PHONE, phone);

        //Inserting a new row for product into the database, returning the ID for that row
        long newRowId = db.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);

        //Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1){
            //If the row ID is -1, then there was an error with the insertion
            Toast.makeText(this, "Error with saving the product", Toast.LENGTH_SHORT).show();
        } else {
            //Otherwise,  the insertion was successful and we can display a toast with the row ID
            Toast.makeText(this, "Product is saved with the row ID: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //Respond to a click on the "save" menu option
            case R.id.action_save:
                //Save the product to the database
                insertProduct();
                finish();
                return true;
                //Respond to the click on the "delete" menu option
            case R.id.action_delete:
                return true;
            case android.R.id.home:
                //Navigate back to the parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
