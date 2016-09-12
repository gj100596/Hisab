package gj.udacity.capstone.hisab.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Gaurav on 11-09-2016.
 */

public class TransactionContract {

    public static final String CONTENT_AUTHORITY = "gj.udacity.capstone.hisab.database";

    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public  static final String URI_PATH = "hisab";


    public static final class Transaction implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(URI_PATH).build();

        //URI for Content Provider
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH;

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

        //To know if this is settled or not
        public static final String COLUMN_SETTLED = "Settled";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
