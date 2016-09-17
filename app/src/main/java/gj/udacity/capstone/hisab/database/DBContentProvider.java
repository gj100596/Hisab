package gj.udacity.capstone.hisab.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import static gj.udacity.capstone.hisab.database.TransactionContract.*;

/*
Unsettle list
select Name,Number,sum(Amount),max(tDate) from tran where Settled=0 group by Name,Number;

Unsettle detail
select tran.Reason,tran.tDate,tran.Amount from tran where name="AB" and Number=735 and Settled=0;

and rest 2 r same with Settled=1;
 */

public class DBContentProvider extends ContentProvider {

    private static final int LIST_UNSETTLE = 1;
    private static final int DETAIL_UNSETTLE = 2;
    private static final int LIST_SETTLE = 3;
    private static final int DETAIL_SETTLE = 4;

    private static final int DELETE_ONE_SETTLE = 10;
    private static final int DELETE_ALL_SETTLE = 11;
    private static final int DELETE_ONE_PERMANENT = 12;
    private static final int DELETE_ALL_PERMANENT = 13;

    private static final UriMatcher uriQueryMatcher = createUriQueryMatcher();
    private static final UriMatcher uriDeleteMatcher = createUriDeleteMatcher();
    private DBHelper mOpenHelper;


    static UriMatcher createUriQueryMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = CONTENT_AUTHORITY;
        matcher.addURI(authority , URI_PATH + "/unsettle", LIST_UNSETTLE);
        matcher.addURI(authority , URI_PATH + "/unsettle/*" ,DETAIL_UNSETTLE);
        matcher.addURI(authority , URI_PATH + "/settle", LIST_SETTLE);
        matcher.addURI(authority , URI_PATH + "/settle/*" ,DETAIL_SETTLE);

        return  matcher;
    }

    static UriMatcher createUriDeleteMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = CONTENT_AUTHORITY;
        matcher.addURI(authority , URI_PATH + "/settle/*", DELETE_ONE_SETTLE);
        matcher.addURI(authority , URI_PATH + "/settle_all/*" ,DELETE_ALL_SETTLE);
        matcher.addURI(authority , URI_PATH + "/delete/*", DELETE_ONE_PERMANENT);
        matcher.addURI(authority , URI_PATH + "/delete_all/*" ,DELETE_ALL_PERMANENT);

        return  matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = uriQueryMatcher.match(uri);
        return  Transaction.CONTENT_TYPE;

        /*
        switch (match){
            case LIST_UNSETTLE:
            case LIST_SETTLE:
                case L
                return Transaction.CONTENT_TYPE;
            case DETAIL_UNSETTLE:
                return  Transaction.CONTENT_TYPE;
            default:
                return null;

        }*/
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor = null;
        switch (uriQueryMatcher.match(uri)) {
            case LIST_UNSETTLE:
                retCursor = getTransactionList(0);
                break;
            case DETAIL_UNSETTLE:
                retCursor = getTransactionHistoryDetail(uri,0);
                break;
            case LIST_SETTLE:
                retCursor = getTransactionList(1);
                break;
            case DETAIL_SETTLE:
                retCursor = getTransactionHistoryDetail(uri,1);
                break;
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getTransactionHistoryDetail(Uri uri,int settlementMode){

        /*
            select tran.Reason,tran.tDate,tran.Amount from tran where name="AB" and Number=735 and Settled=0;
         */
        String uriInfo = uri.getPathSegments().get(2);

        String sortOrder = Transaction.COLUMN_DATE + " DESC";

        String projection[] = new String[]{
                Transaction._ID,
                Transaction.COLUMN_REASON,
                Transaction.COLUMN_DATE,
                Transaction.COLUMN_AMOUNT,
        };

        String selectionString =
                Transaction.TABLE_NAME + "." + Transaction.COLUMN_NAME + "=?"
                        + " AND "+
                Transaction.TABLE_NAME + "." + Transaction.COLUMN_NUMBER + "=?"
                        + " AND "+
                Transaction.TABLE_NAME + "." + Transaction.COLUMN_SETTLED + "="+settlementMode;

        Cursor cursor =  mOpenHelper.getReadableDatabase().query(
                Transaction.TABLE_NAME,
                projection,
                selectionString,
                uriInfo.split("_"),
                null,
                null,
                sortOrder
        );
        cursor.moveToFirst();
        return cursor;
    }

    private Cursor getTransactionList(int settlementMode){
        /*
        select Name,Number,sum(Amount),max(tDate) from tran where Settled=0 group by Name,Number;
         */

        String sortOrder = Transaction.COLUMN_DATE + " DESC";

        String projection[] = new String[]{
                Transaction.COLUMN_NAME,
                Transaction.COLUMN_NUMBER,
                "SUM("+Transaction.COLUMN_AMOUNT+")",
                "MAX("+Transaction.COLUMN_DATE+")"
        };
        String groupby = Transaction.COLUMN_NAME+","+Transaction.COLUMN_NUMBER;

        String selectionString = Transaction.COLUMN_SETTLED + "=?";

        return mOpenHelper.getReadableDatabase().query(
                Transaction.TABLE_NAME,
                projection,
                selectionString,
                new String[]{""+settlementMode},
                groupby,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        String projection[] = new String[]{
                "MAX("+Transaction._ID+")"
        };
        Cursor cursor = mOpenHelper.getReadableDatabase().query(
                Transaction.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        values.put(Transaction._ID,cursor.getInt(0)+1);

        long id = mOpenHelper.getWritableDatabase().insert(Transaction.TABLE_NAME,null,values);
        getContext().getContentResolver().notifyChange(uri, null);
        return Transaction.buildUnSettleDetailURI(
                values.getAsString(Transaction.COLUMN_NAME)+"_"+
                values.getAsString(Transaction.COLUMN_NUMBER)
                );
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int rowDeleted = 0;
        switch (uriDeleteMatcher.match(uri)) {
            case DELETE_ONE_SETTLE:
                rowDeleted = settleOne(uri);
                break;
            case DELETE_ALL_SETTLE:
                rowDeleted = settleAll(uri);
                break;
            case DELETE_ONE_PERMANENT:
                rowDeleted = deleteOne(uri);
                break;
            case DELETE_ALL_PERMANENT:
                rowDeleted = deleteAll(uri);
                break;
        }


        return rowDeleted;
    }

    private int settleOne(Uri uri) {
        String args = uri.getPathSegments().get(2);
        String conditionString =
                Transaction.TABLE_NAME + "." + Transaction._ID + "=? "
                        + " AND "+
                        Transaction.TABLE_NAME + "." + Transaction.COLUMN_NAME + "=? "
                        + " AND "+
                        Transaction.TABLE_NAME + "." + Transaction.COLUMN_NUMBER + "=? " ;

        ContentValues values = new ContentValues();
        values.put(Transaction.COLUMN_SETTLED,1);

        int rowDeleted  = mOpenHelper.getWritableDatabase().update(Transaction.TABLE_NAME,values
                ,conditionString,args.split("_"));
        if(rowDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowDeleted;
    }

    private int settleAll(Uri uri) {
        String args = uri.getPathSegments().get(2);
        String conditionString =
                        Transaction.TABLE_NAME + "." + Transaction.COLUMN_NAME + "=? "
                        + " AND "+
                        Transaction.TABLE_NAME + "." + Transaction.COLUMN_NUMBER + "=? " ;

        ContentValues values = new ContentValues();
        values.put(Transaction.COLUMN_SETTLED,1);

        int rowDeleted  = mOpenHelper.getWritableDatabase().update(Transaction.TABLE_NAME,values
                ,conditionString,args.split("_"));
        if(rowDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowDeleted;
    }

    private int deleteAll(Uri uri) {
        String args = uri.getPathSegments().get(2);
        String conditionString =
                        Transaction.TABLE_NAME + "." + Transaction.COLUMN_NAME + "=? "
                        + " AND "+
                        Transaction.TABLE_NAME + "." + Transaction.COLUMN_NUMBER + "=? " ;

        int rowDeleted  = mOpenHelper.getWritableDatabase().delete(Transaction.TABLE_NAME
                ,conditionString,args.split("_"));
        if(rowDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowDeleted;
    }

    private int deleteOne(Uri uri) {
        String args = uri.getPathSegments().get(2);
        String conditionString =
                Transaction.TABLE_NAME + "." + Transaction._ID + "=? "
                        + " AND "+
                        Transaction.TABLE_NAME + "." + Transaction.COLUMN_NAME + "=? "
                        + " AND "+
                        Transaction.TABLE_NAME + "." + Transaction.COLUMN_NUMBER + "=? " ;

        int rowDeleted  = mOpenHelper.getWritableDatabase().delete(Transaction.TABLE_NAME
                ,conditionString,args.split("_"));
        if(rowDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = mOpenHelper.getWritableDatabase()
                .update( Transaction.TABLE_NAME,values,selection,selectionArgs);

        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
