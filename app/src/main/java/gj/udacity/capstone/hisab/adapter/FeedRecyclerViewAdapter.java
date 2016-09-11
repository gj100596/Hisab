package gj.udacity.capstone.hisab.adapter;

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

    public FeedRecyclerViewAdapter(List<DummyItem> items,FragmentActivity context){//}, OnListFragmentInteractionListener listener) {
        mValues = items;
        this.context = context;
       // mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mAmount.setText(mValues.get(position).id);
        holder.mName.setText(mValues.get(position).content);
        holder.mName.setTag(mValues.get(position).content);

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
