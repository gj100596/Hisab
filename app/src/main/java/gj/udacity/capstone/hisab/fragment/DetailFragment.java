package gj.udacity.capstone.hisab.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.adapter.DetailRecyclerViewAdapter;
import gj.udacity.capstone.hisab.database.TransactionContract;

public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String ARG = "userString";

    //Combination of name and number of particular friend
    private String userString,userName,userNumber;

    private DetailRecyclerViewAdapter detailRecyclerViewAdapter;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(String id){
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userString = getArguments().getString(ARG);
            String[] split = userString.split("_");
            userName=split[0];
            userNumber=split[1];
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

        Cursor cursor = getActivity().getContentResolver()
                .query(
                        TransactionContract.Transaction.buildUnSettleDetailURI(userString),
                        null, null, null, null);
        detailRecyclerViewAdapter = new DetailRecyclerViewAdapter(getActivity(),cursor,userName,userNumber);
        historyList.setAdapter(detailRecyclerViewAdapter);
        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),
                TransactionContract.Transaction.UNSETTLE_URI,
                null,   //new String[]{DBContract.MovieEntry.COLUMN_IMAGE_URL},
                null,
                null,
                null);
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
