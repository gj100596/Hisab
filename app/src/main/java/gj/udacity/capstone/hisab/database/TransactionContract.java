package gj.udacity.capstone.hisab.database;

import android.provider.BaseColumns;

/**
 * Created by Gaurav on 11-09-2016.
 */

public class TransactionContract {

    public static final class Transaction implements BaseColumns{
        //Name of table
        public static final String TABLE_NAME = "Transaction";

        // Name of person consider for transaction
        public static final String COLUMN_NAME = "Name";

        //Reason or Message for transaction
        public static final String COLUMN_REASON = "Reason";

        //Amount given or taken
        public static final String COLUMN_AMOUNT = "Amount";

        //Date of transaction
        public static final String COLUMN_DATE = "Date";

        //Category of transaction(will be used for graph)
        public static final String COLUMN_CATEGORY  = "Category";
    }
}
