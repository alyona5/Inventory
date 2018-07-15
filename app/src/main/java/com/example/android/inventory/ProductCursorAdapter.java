package com.example.android.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventory.data.ProductContract;

import org.w3c.dom.Text;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */

public class ProductCursorAdapter extends CursorAdapter{

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
    public void bindView(View view, Context context, Cursor  cursor){
        TextView nameTextView = (TextView)view.findViewById(R.id.name_item);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity_item);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_item);

        //Extract properties from Cursor
        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_INV_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_INV_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_INV_PRICE);

        //Read the product attributes from the cursor to the current product
        String itemName = cursor.getString(nameColumnIndex);
        Integer itemQuantity = cursor.getInt(quantityColumnIndex);
        Integer itemPrice = cursor.getInt(priceColumnIndex);

        //Populate fields with extracted properties
        nameTextView.setText(itemName);
        quantityTextView.setText(String.valueOf(itemQuantity));
        priceTextView.setText(String.valueOf(itemPrice));
    }
}
