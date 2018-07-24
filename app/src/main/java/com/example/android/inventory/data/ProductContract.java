package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;
import java.security.PublicKey;

public final class ProductContract {

    private ProductContract() {
    }

    /**
     * Inner class that defines constant values for the inventory database table.
     * Each entry in the table represents a single product.
     */

    public static final class ProductEntry implements BaseColumns {

        public static final String CONTENT_AUTHORITY = "com.example.android.inventory";

        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

        public static final String PATH_PRODUCTS = "products";

        //The MIME type of the {@Link #CONTENT_URI} for a list of products.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        //The MIME type of the {@Link #CONTENT_URI} for a single product.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        //Name of the database table for inventory
        public final static String TABLE_NAME = "inventory";

        /**
         * ID number of the product in the database of the store
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;
        /**
         * Name of the product
         * Type: TEXT
         */
        public final static String COLUMN_INV_NAME = "name";
        /**
         * Price of the product
         * Type: INTEGER
         */
        public final static String COLUMN_INV_PRICE = "price";
        /**
         * Quantity of the product
         * Type: INTEGER
         */
        public final static String COLUMN_INV_QUANTITY = "quantity";
        /**
         * Name of the supplier of the product
         * Type: INTEGER
         */
        public final static String COLUMN_INV_SUPPLIER = "supplier_name";
        /**
         * Phone number of the supplier
         * Type: INTEGER
         */
        public final static String COLUMN_INV_SUPPLIER_PHONE = "supplier_phone_number";
        //Possible variants of the supplier of the product
        public static final int UNKNOWN_SUPPLIER = 0;
        public static final int COMPANY_1 = 1;
        public static final int COMPANY_2 = 2;
        public static final int COMPANY_3 = 3;

        /**
         * Returns whether or not the given company name is {@link #COMPANY_1}, {@link #COMPANY_2},
         * or {@link #COMPANY_3}.
         */
        public static boolean isValidCompany(int company) {
            if (company == COMPANY_1 || company == COMPANY_2 || company == COMPANY_3) {
                return true;
            }
            return false;
        }
    }
}
