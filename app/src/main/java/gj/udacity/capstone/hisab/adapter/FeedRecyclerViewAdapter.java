package gj.udacity.capstone.hisab.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.dummy.DummyContent.DummyItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified { OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.ViewHolder> {

    private final List<DummyItem> mValues;
    //private final OnListFragmentInteractionListener mListener;

    public FeedRecyclerViewAdapter(List<DummyItem> items){//}, OnListFragmentInteractionListener listener) {
        mValues = items;
       // mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mAmount.setText(mValues.get(position).id);
        holder.mName.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
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
}
