package gj.udacity.capstone.hisab.fragment;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.adapter.DetailRecyclerViewAdapter;
import gj.udacity.capstone.hisab.database.TransactionContract;

public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_1 = "userString";
    private static final String ARG_2 = "mode";

    //Combination of name and number of particular friend
    private String userString, userName, userNumber;
    private int fragmentMode;

    private DetailRecyclerViewAdapter detailRecyclerViewAdapter;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(String id,int mode) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_1, id);
        args.putInt(ARG_2, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userString = getArguments().getString(ARG_1);
            String[] split = userString.split("_");
            userName = split[0];
            userNumber = split[1];
            fragmentMode = getArguments().getInt(ARG_2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView nameTextView = (TextView) view.findViewById(R.id.history_name);
        nameTextView.setText(userName);

        RecyclerView historyList = (RecyclerView) view.findViewById(R.id.history_list);
        historyList.setLayoutManager(new LinearLayoutManager(getActivity()));

        Cursor cursor;
        if(fragmentMode == 0) {
            cursor = getActivity().getContentResolver()
                    .query(
                            TransactionContract.Transaction.buildUnSettleDetailURI(userString),
                            null, null, null, null);
        }
        else{
            cursor = getActivity().getContentResolver()
                    .query(
                            TransactionContract.Transaction.buildSettleDetailURI(userString),
                            null, null, null, null);
        }
        detailRecyclerViewAdapter = new DetailRecyclerViewAdapter(getActivity(), cursor, userName, userNumber);
        historyList.setAdapter(detailRecyclerViewAdapter);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detail_fragment_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.settleTransaction) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Do You want to settle Whole Transaction?");
            builder.setTitle("Settle?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String args = userName + "_" + userNumber;

                    getActivity().getContentResolver().delete(
                            TransactionContract.Transaction.buildALLSettleDeleteURI(args),
                            null, null);
                    getActivity().onBackPressed();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            builder.show();
        }

        return super.onOptionsItemSelected(item);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if(fragmentMode == 0) {
            return new CursorLoader(getActivity(),
                    TransactionContract.Transaction.UNSETTLE_URI,
                    null,   //new String[]{DBContract.MovieEntry.COLUMN_IMAGE_URL},
                    null,
                    null,
                    null);
        }
        else{
            return new CursorLoader(getActivity(),
                    TransactionContract.Transaction.SETTLE_URI,
                    null,   //new String[]{DBContract.MovieEntry.COLUMN_IMAGE_URL},
                    null,
                    null,
                    null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        detailRecyclerViewAdapter.swapCursor(data);
        detailRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        detailRecyclerViewAdapter.swapCursor(null);
        detailRecyclerViewAdapter.notifyDataSetChanged();
    }

}
