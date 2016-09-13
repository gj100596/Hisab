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

    private static final UriMatcher sUriMatcher = createUriMatcher();
    private DBHelper mOpenHelper;


    static UriMatcher createUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = CONTENT_AUTHORITY;
        matcher.addURI(authority , URI_PATH + "/unsettle", LIST_UNSETTLE);
        matcher.addURI(authority , URI_PATH + "/unsettle/*" ,DETAIL_UNSETTLE);
        matcher.addURI(authority , URI_PATH + "/settle", LIST_SETTLE);
        matcher.addURI(authority , URI_PATH + "/settle/*" ,DETAIL_SETTLE);

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
        int match = sUriMatcher.match(uri);
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

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case LIST_UNSETTLE:
                return getTransactionList(0);
            case DETAIL_UNSETTLE:
                return getTransactionHistoryDetail(uri,0);
            case LIST_SETTLE:
                return getTransactionList(1);
            case DETAIL_SETTLE:
                return getTransactionHistoryDetail(uri,1);
        }
        return null;
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

        String sortOrder = TransactionContract.Transaction.COLUMN_AMOUNT + " DESC";

        String projection[] = new String[]{
                Transaction.COLUMN_NAME,
                Transaction.COLUMN_NUMBER,
                "SUM("+Transaction.COLUMN_AMOUNT+")",
                "MAX("+Transaction.COLUMN_DATE+")"
        };
        String groupby = Transaction.COLUMN_NAME+","+Transaction.COLUMN_NUMBER;

        String selectionString = Transaction.COLUMN_SETTLED + "="+settlementMode;

        return mOpenHelper.getReadableDatabase().query(
                Transaction.TABLE_NAME,
                projection,
                selectionString,
                null,
                groupby,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long id = mOpenHelper.getWritableDatabase().insert(Transaction.TABLE_NAME,null,values);
        getContext().getContentResolver().notifyChange(uri, null);
        return Transaction.buildUnSettleDetailURI(
                values.getAsString(Transaction.COLUMN_NAME),
                values.getAsString(Transaction.COLUMN_NUMBER)
                );
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String MovieIDSelectionString = Transaction.TABLE_NAME+
                "." + Transaction._ID + "=?";
        int rowDeleted  = mOpenHelper.getWritableDatabase().delete(Transaction.TABLE_NAME
                ,MovieIDSelectionString,selectionArgs);
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
