package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract;

import org.w3c.dom.Text;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */

public class ProductCursorAdapter extends CursorAdapter{

    Button soldButton;
    /**
     * Constructs a new {@link ProductCursorAdapter}
     *
     * @param context, the Context.
     * @param c,       the cursor from which to get the data
     */

    public ProductCursorAdapter(Context context, Cursor c){
        super(context, c, 0);
    }

    /**
     * @param context, app context.
     * @param cursor, the cursor from which to get the data.
     * @param parent, the parent to which the new view is attached to
     * @return, the newly created item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the product data.
     *
     * @param view, existing view.
     * @param context, app context.
     * @param  cursor, the cursor from which to get the data.
     */
    @Override
    public void bindView(View view, final Context context, Cursor  cursor){
        TextView nameTextView = (TextView)view.findViewById(R.id.name_item);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity_item);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_item);
        soldButton = (Button)view.findViewById(R.id.button_sold);

        //Extract properties from Cursor
        final int columnIndex = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry._ID));
        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_INV_NAME);
        final int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_INV_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_INV_PRICE);

        //Read the product attributes from the cursor to the current product
        String itemName = cursor.getString(nameColumnIndex);
        Integer itemQuantity = cursor.getInt(quantityColumnIndex);
        Integer itemPrice = cursor.getInt(priceColumnIndex);

        //Populate fields with extracted properties
        nameTextView.setText(itemName);
        quantityTextView.setText(String.valueOf(itemQuantity));
        priceTextView.setText(String.valueOf(itemPrice));
        soldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, columnIndex);
                if (quantityColumnIndex == 0){
                    Toast.makeText(context, "Nothing to sale", Toast.LENGTH_SHORT).show();
                }else{
                    int newQuantity = quantityColumnIndex - 1;
                    if (newQuantity == 0){
                        Toast.makeText(context, "Out of stock", Toast.LENGTH_SHORT).show();
                    }

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ProductContract.ProductEntry.COLUMN_INV_QUANTITY, newQuantity);
                    context.getContentResolver().update(uri, contentValues, null, null);
                }

            }
        });

    }

}
