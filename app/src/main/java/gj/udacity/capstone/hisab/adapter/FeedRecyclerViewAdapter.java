package gj.udacity.capstone.hisab.adapter;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.activity.MainActivity;
import gj.udacity.capstone.hisab.fragment.DetailFragment;

import static gj.udacity.capstone.hisab.fragment.FeedFragment.arrow;
import static gj.udacity.capstone.hisab.fragment.FeedFragment.emptyImage;
import static gj.udacity.capstone.hisab.fragment.FeedFragment.msg1;
import static gj.udacity.capstone.hisab.fragment.FeedFragment.msg2;
import static gj.udacity.capstone.hisab.fragment.FeedFragment.recyclerView;

public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.ViewHolder> {

    private FragmentActivity context;
    private Cursor cursor;
    private boolean dataValid;
    private DataSetObserver dataSetObserver;
    private int fragmentMode;

    private final int COLUMN_NAME_INDEX = 0;
    private final int COLUMN_NUMBER_INDEX = 1;
    private final int COLUMN_SUM_INDEX = 2;
    private final int COLUMN_DATE_INDEX = 3;

    public FeedRecyclerViewAdapter(FragmentActivity context, Cursor cursor, int fragmentMode){//}, OnListFragmentInteractionListener listener) {
        this.context = context;
        this.cursor = cursor;
        this.fragmentMode = fragmentMode;
        dataValid = cursor != null;
        dataSetObserver = new FeedRecyclerViewAdapter.NotifyingDataSetObserver();
        if (cursor != null) {
            cursor.registerDataSetObserver(dataSetObserver);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        cursor.moveToPosition(position);
        final Integer amount = Integer.parseInt(cursor.getString(COLUMN_SUM_INDEX));
        holder.mAmount.setText(""+amount);//mValues.get(position).id);
        if(amount<0)
            holder.mTransactionColor.setBackgroundResource(R.color.sliderOptionBG);
        else
            holder.mTransactionColor.setBackgroundResource(R.color.materialGreen);
        holder.mName.setText(cursor.getString(COLUMN_NAME_INDEX));//mValues.get(position).content);
        holder.mName.setTag(cursor.getString(COLUMN_NAME_INDEX)+"_"+cursor.getString(COLUMN_NUMBER_INDEX));//mValues.get(position).content);
        holder.mNumber.setText(cursor.getString(COLUMN_NUMBER_INDEX));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailFragment detailFragment = DetailFragment.newInstance(
                        holder.mName.getTag().toString(),
                        amount,
                        fragmentMode);
                detailFragment.setHasOptionsMenu(true);

                if(MainActivity.tabletDevice){

                    context.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.detailInMain, detailFragment)
                            .addSharedElement(holder.mName, context.getString(R.string.shared_transition_person_name))
                            .addToBackStack("Details")
                            .commit();
                }
                else {
                    // Enter Transition on History List
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        Slide slide = new Slide(Gravity.BOTTOM);
                        slide.addTarget(R.id.history_list);
                        slide.setInterpolator(AnimationUtils
                                .loadInterpolator(
                                        context,
                                        android.R.interpolator.linear_out_slow_in
                                ));
                        slide.setDuration(1000);
                        detailFragment.setEnterTransition(slide);
                    }
                    // Exit Transition on History List
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        Slide slide = new Slide(Gravity.LEFT);
                        slide.addTarget(R.id.history_list);
                        slide.setInterpolator(AnimationUtils
                                .loadInterpolator(
                                        context,
                                        android.R.interpolator.linear_out_slow_in
                                ));
                        slide.setDuration(1000);
                        detailFragment.setExitTransition(slide);
                    }

                    /*
                    //Transition on Person Name...Shared Transition
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        ChangeBounds changeBoundsTransition = (ChangeBounds) TransitionInflater.
                                from(context).inflateTransition(R.transition.change_bound);
                        detailFragment.setSharedElementEnterTransition(changeBoundsTransition);

                    }
                    */

                    context.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.feed, detailFragment)
                            .addSharedElement(holder.mName, context.getString(R.string.shared_transition_person_name))
                            .addToBackStack("Details")
                            .commit();
                }
            }
        });
    }

    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public int getItemCount() {
        if (dataValid && cursor != null) {
            if(cursor.getCount() == 0){
                msg1.setVisibility(View.VISIBLE);
                msg2.setVisibility(View.VISIBLE);
                arrow.setVisibility(View.VISIBLE);
                emptyImage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);

                if(fragmentMode == 0){
                    msg1.setText(context.getString(R.string.msg1_unsettle));
                    msg2.setText(context.getString(R.string.msg2_unsettle));
                }
                else{
                    msg1.setText(context.getString(R.string.msg1_settle));
                    msg2.setText(context.getString(R.string.msg2_settle));
                }
            }
            else{
                msg1.setVisibility(View.INVISIBLE);
                msg2.setVisibility(View.INVISIBLE);
                arrow.setVisibility(View.INVISIBLE);
                emptyImage.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            return cursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (dataValid && cursor != null && cursor.moveToPosition(position)) {
            return Long.valueOf(cursor.getString(COLUMN_NUMBER_INDEX));
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mName,mAmount, mNumber;
        public final ImageView mTransactionColor;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.name);
            mAmount = (TextView) view.findViewById(R.id.amount);
            mNumber = (TextView) view.findViewById(R.id.date);
            mTransactionColor = (ImageView) view.findViewById(R.id.transactionTypeColor);
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
