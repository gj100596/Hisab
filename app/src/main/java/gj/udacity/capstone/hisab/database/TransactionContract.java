package gj.udacity.capstone.hisab.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Gaurav on 11-09-2016.
 */

public class TransactionContract {

    public static final String CONTENT_AUTHORITY = "gj.udacity.capstone.hisab";

    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public  static final String URI_PATH = "hisab";


    public static final class Transaction implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(URI_PATH).build();
        public static final Uri UNSETTLE_URI = CONTENT_URI.buildUpon().appendPath("unsettle").build();
        public static final Uri SETTLE_URI = CONTENT_URI.buildUpon().appendPath("settle").build();
        public static final Uri DELETE_SETTLE_URI = CONTENT_URI.buildUpon().appendPath("settle").build();
        public static final Uri DELETE_PERMANENT_URI = CONTENT_URI.buildUpon().appendPath("delete").build();
        public static final Uri DELETE_SETTLE_ALL_URI = CONTENT_URI.buildUpon().appendPath("settle_all").build();
        public static final Uri DELETE_PERMANENT_ALL_URI = CONTENT_URI.buildUpon().appendPath("delete_all").build();

        //URI for Content Provider
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + URI_PATH;

        //Name of table
        public static final String TABLE_NAME = "TransactionTable";

        // Name of person consider for transaction
        public static final String COLUMN_NAME = "Name";

        //Number of person for being primary key search
        public static final String COLUMN_NUMBER = "Number";

        //Reason or Message for transaction
        public static final String COLUMN_REASON = "Reason";

        //Amount given or taken
        public static final String COLUMN_AMOUNT = "Amount";

        //Date of transaction
        public static final String COLUMN_DATE = "tDate";

        //Category of transaction(will be used for graph)
        public static final String COLUMN_CATEGORY  = "Category";

        //To know if this is settled or not
        public static final String COLUMN_SETTLED = "Settled";


        //URI Functions
        public static Uri buildUnSettleDetailURI(String userString){
            return UNSETTLE_URI.buildUpon().appendPath(userString).build();
        }

        public static Uri buildSettleDetailURI(String userString) {
            return SETTLE_URI.buildUpon().appendPath(userString).build();
        }

        public static Uri buildOneSettleDeleteURI(String userString){
            return DELETE_SETTLE_URI.buildUpon().appendPath(userString).build();
        }
        public static Uri buildALLSettleDeleteURI(String userString){
            return DELETE_SETTLE_ALL_URI.buildUpon().appendPath(userString).build();
        }
        public static Uri buildOnePermanentDeleteURI(String userString){
            return DELETE_PERMANENT_URI.buildUpon().appendPath(userString).build();
        }
        public static Uri buildAllPermanentDeleteURI(String userString){
            return DELETE_PERMANENT_ALL_URI.buildUpon().appendPath(userString).build();
        }

        public static Uri getNameNoUri(){
            return CONTENT_URI.buildUpon().appendPath("list").build();
        }
        public static Uri getChartUri(){
            return CONTENT_URI.buildUpon().appendPath("chart").build();
        }
        public static Uri getDatedChartUri(String dateRegion){
            Uri temp = CONTENT_URI.buildUpon().appendPath("chart").build();
            return temp.buildUpon().appendPath(dateRegion).build();

        }
    }
}
