package gj.udacity.capstone.hisab.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.database.TransactionContract;
import gj.udacity.capstone.hisab.dummy.DummyContent;

public class FeedCursorAdapter extends CursorAdapter {

    public FeedCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mName,mAmount,mDate;
        public DummyContent.DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.name);
            mAmount = (TextView) view.findViewById(R.id.amount);
            mDate = (TextView) view.findViewById(R.id.date);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

       int ImageColumnIndex = cursor.getColumnIndex(TransactionContract.Transaction.COLUMN_NAME);

       cursor.getString(ImageColumnIndex);


    }
}
