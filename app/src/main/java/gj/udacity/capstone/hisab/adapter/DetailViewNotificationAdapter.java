package gj.udacity.capstone.hisab.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gj.udacity.capstone.hisab.R;

import static gj.udacity.capstone.hisab.R.id.amount;

public class DetailViewNotificationAdapter extends RecyclerView.Adapter<DetailViewNotificationAdapter.ViewHolder> {

    private JSONArray data;

    public DetailViewNotificationAdapter(JSONArray data){
        this.data = data;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        try {
            JSONObject jsonObject = data.getJSONObject(position);
            Integer amount = jsonObject.getInt("Amount");
            holder.mAmount.setText(""+amount);
            if(amount<0)
                holder.mAmount.setTextColor(Color.RED);
            else
                holder.mAmount.setTextColor(Color.GREEN);
            holder.mReason.setText(jsonObject.getString("Reason"));
            holder.mDate.setText(jsonObject.getString("Date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.length();
    }

    @Override
    public long getItemId(int position) {
        try {
            return Long.valueOf(data.getJSONObject(position).getString("User"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mReason, mAmount, mDate;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mReason = (TextView) view.findViewById(R.id.reason);
            mAmount = (TextView) view.findViewById(amount);
            mDate = (TextView) view.findViewById(R.id.date);

        }
    }
}
