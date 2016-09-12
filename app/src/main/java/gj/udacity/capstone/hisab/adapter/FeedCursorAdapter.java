package gj.udacity.capstone.hisab.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import gj.udacity.capstone.hisab.database.TransactionContract;

public class FeedCursorAdapter extends CursorAdapter {

    public FeedCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return  new ImageView(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int ImageColumnIndex = cursor.getColumnIndex(TransactionContract.Transaction.COLUMN_NAME);

       cursor.getString(ImageColumnIndex);


    }
}
