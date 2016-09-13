package gj.udacity.capstone.hisab.adapter;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.dummy.DummyContent.DummyItem;
import gj.udacity.capstone.hisab.fragment.DetailFragment;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified { OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.ViewHolder> {

    private final List<DummyItem> mValues;
    private FragmentActivity context;
    private Cursor cursor;
    private boolean dataValid;
    private int rowIdColumn;
    private DataSetObserver dataSetObserver;

    private final int COLUMN_NAME_INDEX = 0;
    private final int COLUMN_NUMBER_INDEX = 1;
    private final int COLUMN_SUM_INDEX = 2;
    private final int COLUMN_DATE_INDEX = 3;

    public FeedRecyclerViewAdapter(List<DummyItem> items,FragmentActivity context, Cursor cursor){//}, OnListFragmentInteractionListener listener) {
        mValues = items;
        this.context = context;
        this.cursor = cursor;
        dataValid = cursor != null;
        rowIdColumn = dataValid ? cursor.getColumnIndex("_id") : -1;
        dataSetObserver = new FeedRecyclerViewAdapter.NotifyingDataSetObserver();
        if (cursor != null) {
            cursor.registerDataSetObserver(dataSetObserver);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        cursor.moveToPosition(position);
        holder.mAmount.setText(cursor.getString(COLUMN_SUM_INDEX));//mValues.get(position).id);
        holder.mName.setText(cursor.getString(COLUMN_NAME_INDEX));//mValues.get(position).content);
        holder.mName.setTag(cursor.getString(COLUMN_NUMBER_INDEX));//mValues.get(position).content);
        holder.mDate.setText(cursor.getString(COLUMN_DATE_INDEX));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.feed, DetailFragment.newInstance(holder.mName.getTag().toString()))
                        .addToBackStack("Details")
                        .commit();
            }
        });
    }

    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public int getItemCount() {
        if (dataValid && cursor != null) {
            return cursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (dataValid && cursor != null && cursor.moveToPosition(position)) {
            return cursor.getLong(rowIdColumn);
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mName,mAmount,mDate;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.name);
            mAmount = (TextView) view.findViewById(R.id.amount);
            mDate = (TextView) view.findViewById(R.id.date);
        }
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == cursor) {
            return null;
        }
        final Cursor oldCursor = cursor;
        if (oldCursor != null && dataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(dataSetObserver);
        }
        cursor = newCursor;
        if (cursor != null) {
            if (dataSetObserver != null) {
                cursor.registerDataSetObserver(dataSetObserver);
            }
            rowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            dataValid = true;
            notifyDataSetChanged();
        } else {
            rowIdColumn = -1;
            dataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            dataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            dataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }
}
