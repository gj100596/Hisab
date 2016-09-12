package gj.udacity.capstone.hisab.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import static gj.udacity.capstone.hisab.database.TransactionContract.*;

public class DBContentProvider extends ContentProvider {

    private static final int LIST_UNSETTLE = 1;
    private static final int DETAIL_UNSETTLE = 2;
    private static final int DETAIL_SETTLE = 3;

    private static final UriMatcher sUriMatcher = createUriMatcher();
    private DBHelper mOpenHelper;


    static UriMatcher createUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = CONTENT_AUTHORITY;
        matcher.addURI(authority , URI_PATH , LIST_UNSETTLE);
        matcher.addURI(authority , URI_PATH + "/#" ,DETAIL_UNSETTLE);

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

        switch (match){
            case LIST_UNSETTLE:
                return Transaction.CONTENT_TYPE;
            case DETAIL_UNSETTLE:
                return  Transaction.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case LIST_UNSETTLE:
                return getTransactionList(projection,sortOrder);
            case DETAIL_UNSETTLE:
                return getTransactionHistoryDetail(uri,projection,sortOrder);
        }
        return null;
    }

    /**
     * This function gives a specific favorite movies
     */
    private Cursor getTransactionHistoryDetail(Uri uri, String[] projection, String sortOrder) {

        String selectionString = Transaction.TABLE_NAME+
                "." + Transaction._ID + "=?" + " AND "+
                Transaction.TABLE_NAME + "." + Transaction.COLUMN_SETTLED + "=0";

        Cursor cursor =  mOpenHelper.getReadableDatabase().query(
                Transaction.TABLE_NAME,
                projection,
                selectionString,
                new String[]{uri.getPathSegments().get(1)},
                null,
                null,
                sortOrder
        );
        cursor.moveToFirst();
        return cursor;
    }

    /**
     * This function gives all the favorite movies
     */
    private Cursor getTransactionList(String[] projection, String sortOrder) {

        return mOpenHelper.getReadableDatabase().query(
                Transaction.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long id = mOpenHelper.getWritableDatabase().insert(Transaction.TABLE_NAME,null,values);
        getContext().getContentResolver().notifyChange(uri, null);
        return Transaction.buildLocationUri(id);
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
