package gj.udacity.capstone.hisab.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static gj.udacity.capstone.hisab.database.TransactionContract.Transaction;

/**
 * Created by Gaurav on 11-09-2016.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 12;

    static final String DATABASE_NAME = "hisab.db";


    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        final String createQuery = "CREATE TABLE " + Transaction.TABLE_NAME + "("
                + Transaction._ID + " INTEGER AUTOINCREMENT,"
                + Transaction.COLUMN_NAME + " TEXT NOT NULL,"
                + Transaction.COLUMN_NUMBER + " INTEGER NOT NULL,"
                + Transaction.COLUMN_REASON + " TEXT NOT NULL,"
                + Transaction.COLUMN_AMOUNT + " REAL NOT NULL,"
                + Transaction.COLUMN_DATE + " DATE NOT NULL,"
                + Transaction.COLUMN_CATEGORY + " TEXT NOT NULL,"
                + Transaction.COLUMN_SETTLED + " INTEGER NOT NULL,"
                + "PRIMARY KEY("+ Transaction._ID+","+Transaction.COLUMN_NAME+","+Transaction.COLUMN_NUMBER
                + ");";

        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Transaction.TABLE_NAME);
        onCreate(db);
    }
}
