package gj.udacity.capstone.hisab.adapter;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import gj.udacity.capstone.hisab.R;

import static gj.udacity.capstone.hisab.database.TransactionContract.BASE_URI;

public class DetailRecyclerViewAdapter extends RecyclerView.Adapter<DetailRecyclerViewAdapter.ViewHolder> {

    private FragmentActivity context;
    private String name,number,sum;
    private Cursor cursor;
    private boolean dataValid;
    private DataSetObserver dataSetObserver;

    private final int COLUMN_ID_INDEX = 0;
    private final int COLUMN_REASON_INDEX = 1;
    private final int COLUMN_DATE_INDEX = 2;
    private final int COLUMN_AMOUNT_INDEX = 3;

    public DetailRecyclerViewAdapter (FragmentActivity context, Cursor cursor,String name,String number){
        this.context = context;
        this.cursor = cursor;
        dataValid = cursor != null;
        dataSetObserver = new DetailRecyclerViewAdapter.NotifyingDataSetObserver();
        if (cursor != null) {
            cursor.registerDataSetObserver(dataSetObserver);
        }
        this.name = name;
        this.number = number;

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
        holder.mAmount.setText(cursor.getString(COLUMN_AMOUNT_INDEX));//mValues.get(position).id);
        holder.mReason.setText(cursor.getString(COLUMN_REASON_INDEX));//mValues.get(position).content);
        holder.delete.setTag(cursor.getString(COLUMN_ID_INDEX));//mValues.get(position).content);
        holder.mDate.setText(cursor.getString(COLUMN_DATE_INDEX));
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
            return cursor.getInt(COLUMN_ID_INDEX);
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mReason, mAmount, mDate;
        public final LinearLayout mSlidePanel,mMainLinear;
        public final ImageView delete;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mReason = (TextView) view.findViewById(R.id.reason);
            mAmount = (TextView) view.findViewById(R.id.amount);
            mDate = (TextView) view.findViewById(R.id.date);
            mSlidePanel = (LinearLayout) view.findViewById(R.id.sidePane);
            mMainLinear = (LinearLayout) view.findViewById(R.id.mainLinear);
            delete = (ImageView) view.findViewById(R.id.delete);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Do You want to settle this transaction?");
                    builder.setTitle("Settle?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            context.getContentResolver().delete(BASE_URI, null,new String[]{
                                    v.getTag().toString(),
                                    name,
                                    number
                            });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    builder.show();
                }
            });


            setUpSlider();
        }

        private static final int DEFAULT_THRESHOLD = 128;

        private void setUpSlider() {
            mView.setOnTouchListener(new View.OnTouchListener() {
                int initialX = 0;
                final float slop = ViewConfiguration.get(context).getScaledTouchSlop();

                public boolean onTouch(final View view, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        initialX = (int) event.getX();
                        view.setPadding(0, 0, 0, 0);
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        int currentX = (int) event.getX();
                        int offset = currentX - initialX;
                        if (Math.abs(offset) > slop) {
                            view.setPadding(offset, 0, 0, 0);

                            if (offset > DEFAULT_THRESHOLD) {
                                mSlidePanel.setVisibility(View.GONE);
                                mMainLinear.setVisibility(View.VISIBLE);
                            } else if (offset < -DEFAULT_THRESHOLD) {
                                mSlidePanel.setVisibility(View.VISIBLE);
                                mMainLinear.setVisibility(View.GONE);
                            }
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                        // Animate back if no action was performed.
                        ValueAnimator animator = ValueAnimator.ofInt(view.getPaddingLeft(), 0);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                view.setPadding((Integer) valueAnimator.getAnimatedValue(), 0, 0, 0);
                            }
                        });
                        animator.setDuration(150);
                        animator.start();
                    }
                    return true;
                }
            });
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
            dataValid = true;
            notifyDataSetChanged();
        } else {
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
