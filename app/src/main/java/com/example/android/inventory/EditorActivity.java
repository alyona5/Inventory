package com.example.android.inventory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.CursorLoader;
import android.content.Loader;

import com.example.android.inventory.data.ProductContract;
import com.example.android.inventory.data.ProductDbHelper;

/**
 * Allows user to create a new product in the db or update the current one.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Identifier for the product data loader
    private static final int EXISTING_PRODUCT_LOADER = 0;


    Button increaseButton, decreaseButton, callButton, deleteButton;
    private Uri mCurrentProductUri;
    private EditText mProductNameEditText, mProductPriceEditText, mProductQuantityEditText, mPhoneOfSupplier;
    private Spinner mNameOfSupplier;

    /**
     * There are only 3 suppliers the company works with and the
     * possible values are:
     * Company_1  for Billy Co., Company_2 for Dilly Inc., Company_3 for Willy Ltd.
     */
    private int mSupplier = ProductContract.ProductEntry.UNKNOWN_SUPPLIER;

    //Boolean flag that keeps track whether the product has been edited
    private boolean mProductHasChanged = false;

    //On touch listener that listens to any user touches the view, implying that they are modifying
    // the view and we change the mProductHasChanged to true
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //To figure out if we are updating the product or creating the new one
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        //Find all relevant views that will be needed to read the the input
        mProductNameEditText = (EditText) findViewById(R.id.product_name);
        mProductPriceEditText = (EditText) findViewById(R.id.price);
        mProductQuantityEditText = (EditText) findViewById(R.id.quantity);
        mNameOfSupplier = (Spinner) findViewById(R.id.spinner_supplier_name);
        mPhoneOfSupplier = (EditText) findViewById(R.id.supplier_phone);
        increaseButton = (Button) findViewById(R.id.increase);
        decreaseButton = (Button) findViewById(R.id.decrease);
        callButton = (Button) findViewById(R.id.call_button);
        deleteButton = (Button) findViewById(R.id.button_delete);

        //Set up onTouchListener on all input fields
        mProductNameEditText.setOnTouchListener(mTouchListener);
        mProductPriceEditText.setOnTouchListener(mTouchListener);
        mProductQuantityEditText.setOnTouchListener(mTouchListener);
        mNameOfSupplier.setOnTouchListener(mTouchListener);
        mPhoneOfSupplier.setOnTouchListener(mTouchListener);

        //If the intent does not contain the product Uri, then we know that we are creating the new product
        if (mCurrentProductUri == null) {
            //New product > app bar title changes to "add a product"
            setTitle(getString(R.string.title_add_product));
            invalidateOptionsMenu();
        } else {
            //Otherwise this is the existing product > app bar title is being changes to "Edit product"
            setTitle(getString(R.string.title_edit_product));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

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
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.supplier_1))) {
                        mSupplier = ProductContract.ProductEntry.COMPANY_1;
                    } else if (selection.equals(getString(R.string.supplier_2))) {
                        mSupplier = ProductContract.ProductEntry.COMPANY_2;
                    } else {
                        mSupplier = ProductContract.ProductEntry.COMPANY_3;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSupplier = ProductContract.ProductEntry.UNKNOWN_SUPPLIER;
            }
        });
    }

    private boolean saveProduct() {
        String nameString = mProductNameEditText.getText().toString().trim();
        String phoneString = mPhoneOfSupplier.getText().toString().trim();

        int priceInteger;
        try{
            priceInteger = Integer.parseInt(mProductPriceEditText.getText().toString().trim());
        }catch (NumberFormatException e){
            priceInteger = -1;
        }

        int quantityInteger;
        try{
            quantityInteger = Integer.parseInt(mProductQuantityEditText.getText().toString().trim());
        } catch (NumberFormatException e){
            quantityInteger = -1;
        }

        //Check is this is supposed to be a new product and check if the fields in the editor are blank
        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(mProductPriceEditText.getText().toString().trim()) &&
                TextUtils.isEmpty(mProductQuantityEditText.getText().toString().trim()) &&
                TextUtils.isEmpty(phoneString) &&
                mSupplier == ProductContract.ProductEntry.UNKNOWN_SUPPLIER) {
            Toast.makeText(this, "Empty fields cannot be saved", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!isValidData(nameString, priceInteger, quantityInteger, phoneString)){
            return false;
        }

        //ContentValue object where the column names are the keys and product attributes from the editor are the values
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_INV_NAME, nameString);
        values.put(ProductContract.ProductEntry.COLUMN_INV_PRICE, priceInteger);
        values.put(ProductContract.ProductEntry.COLUMN_INV_QUANTITY, quantityInteger);
        values.put(ProductContract.ProductEntry.COLUMN_INV_SUPPLIER, mSupplier);
        values.put(ProductContract.ProductEntry.COLUMN_INV_SUPPLIER_PHONE, phoneString);

        //Determine if this is a new product or existing one by checking if mCurrentProductUri is null or not
        if (mCurrentProductUri == null) {
            //Returning the content URI for the new product
            Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                //If the new content Uri is null, then there was an error with the insertion
                Toast.makeText(this, R.string.Toast1, Toast.LENGTH_SHORT).show();
            } else {
                //Otherwise,  the insertion was successful and we can display a toast with the row ID
                Toast.makeText(this, R.string.Toast2, Toast.LENGTH_SHORT).show();
            }
        } else {
            //Otherwise this is an existing product
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_string), Toast.LENGTH_SHORT).show();
            } else {
                //Otherwise the update was successful
                Toast.makeText(this, getString(R.string.editor_update_product_successful), Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private boolean isValidData(String nameString, int quantityInteger, int priceInteger, String phoneString){
        boolean isValid = true;
        String message = null;

        if (nameString == null || nameString.isEmpty()){
            message = "The name of the product is missing";
            isValid = false;
            mProductNameEditText.requestFocus();
        }

        if (priceInteger < 0 ){
            message = "The price of the product is missing";
            isValid = false;
            mProductPriceEditText.requestFocus();
        }

        if (quantityInteger < 0){
            message = "The quantity of the product is missing";
            isValid = false;
            mProductQuantityEditText.requestFocus();
        }

        if (phoneString == null || phoneString.isEmpty()){
            message = "The phone number of the supplier is missing";
            isValid = false;
            mPhoneOfSupplier.requestFocus();
        }


        if (message != null && !message.isEmpty()){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
        return isValid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    //This method is called after invalidateOptionsMenu().
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //If this is a new product, hide the delete menu item
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            //Respond to a click on the "save" menu option
            case R.id.action_save:
                if (saveProduct()){
                    startActivity(new Intent(EditorActivity.this, CatalogActivity.class));
                    finish();
                }
                return true;
            //Respond to the click on the "delete" menu option
            case R.id.action_delete:
                //Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            //Respond to the click on the "Up" arrow button in the app bar
            case android.R.id.home:
                //If the product hasn't changed then continue with navigating up to parent activity {@Link CatalogActivity}
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                //Otherwise if there are unsaved changes, setup the dialog  to warn the user.
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //User clicked discard button, navigate to parent activity
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                //Show a dialog that notifies the user that they have unsaved changed
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //This method is called when the back button is clicked
    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        //Otherwise if there are unsaved changes, setup the dialog to warn the user
        DialogInterface.OnClickListener discardButtonOnClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonOnClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_INV_NAME,
                ProductContract.ProductEntry.COLUMN_INV_PRICE,
                ProductContract.ProductEntry.COLUMN_INV_QUANTITY,
                ProductContract.ProductEntry.COLUMN_INV_SUPPLIER,
                ProductContract.ProductEntry.COLUMN_INV_SUPPLIER_PHONE};

        return new CursorLoader(this, mCurrentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_INV_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_INV_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_INV_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_INV_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_INV_SUPPLIER_PHONE);

            //Extract out the values from the cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            final int quantity = cursor.getInt(quantityColumnIndex);
            int supplier = cursor.getInt(supplierColumnIndex);
            int phone = cursor.getInt(phoneColumnIndex);

            //Update the views on the screen with the values from the database
            mProductNameEditText.setText(name);
            mProductPriceEditText.setText(Integer.toString(price));
            mProductQuantityEditText.setText(Integer.toString(quantity));
            mPhoneOfSupplier.setText(Integer.toString(phone));

            switch (supplier) {
                case ProductContract.ProductEntry.COMPANY_1:
                    mNameOfSupplier.setSelection(1);
                    break;
                case ProductContract.ProductEntry.COMPANY_2:
                    mNameOfSupplier.setSelection(2);
                    break;
                case ProductContract.ProductEntry.COMPANY_3:
                    mNameOfSupplier.setSelection(3);
                    break;
                default:
                    mNameOfSupplier.setSelection(0);
                    break;
            }

            //Increases the quantity by 1
            increaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ProductContract.ProductEntry.COLUMN_INV_QUANTITY,
                            Integer.parseInt(mProductQuantityEditText.getText().toString().trim()) + 1);
                    getContentResolver().update(mCurrentProductUri, contentValues, null, null);
                }
            });

            //Decreases the quantity by 1
            decreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ProductContract.ProductEntry.COLUMN_INV_QUANTITY,
                            Integer.parseInt(mProductQuantityEditText.getText().toString().trim()) - 1);
                    getContentResolver().update(mCurrentProductUri, contentValues, null, null);
                }
            });

            //Call button to contact the supplier
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phone = mPhoneOfSupplier.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_phone_app, Toast.LENGTH_SHORT).show();
                    }
                }
            });


            //Delete button in the detailed list item view
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteButton.setEnabled(false);
                    showDeleteConfirmationDialog();
                }
            });

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText("");
        mProductPriceEditText.setText("");
        mProductQuantityEditText.setText("");
        mNameOfSupplier.setSelection(0);
        mPhoneOfSupplier.setText("");
    }

    //Show the dialog that warns that there are unsaved changes that will be lost if they continue leaving the editor
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonOnClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_message);
        builder.setPositiveButton(R.string.discard, discardButtonOnClickListener);
        builder.setNegativeButton(R.string.keep_editing_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }

            }
        });

        //Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_product_dialog_message);
        builder.setPositiveButton(R.string.delete_dialog_message, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel_dialog_message, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        //Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Perform the deletion of the product in  the database
    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

};
