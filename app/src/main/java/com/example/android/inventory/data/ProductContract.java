package com.example.android.inventory.data;

import android.provider.BaseColumns;

import java.security.PublicKey;

/**
 * Inner class that defines constant values for the inventory database table.
 * Each entry in the table represents a single product.
 */

public final class ProductContract {

    private ProductContract(){}

    public static final class ProductEntry implements BaseColumns {

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
    }
}
