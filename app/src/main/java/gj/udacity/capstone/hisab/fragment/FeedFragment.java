package gj.udacity.capstone.hisab.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.adapter.FeedRecyclerViewAdapter;
import gj.udacity.capstone.hisab.database.TransactionContract;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@k OnListFragmentInteractionListener}
 * interface.
 */
public class FeedFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CURSORLOADER_ID = 1;
    private FeedRecyclerViewAdapter feedRecyclerViewAdapter;

    private static final String ARG = "mode";
    private int fragmentMode;


   // private OnListFragmentInteractionListener mListener;

    public FeedFragment() {
    }

    public static FeedFragment newInstance(int mode) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            fragmentMode = getArguments().getInt(ARG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            Cursor cursor;
            if(fragmentMode == 0) {
                cursor = getActivity().getContentResolver()
                        .query(
                                TransactionContract.Transaction.UNSETTLE_URI,
                                null, null, null, null);
            }
            else{
                cursor = getActivity().getContentResolver()
                        .query(
                                TransactionContract.Transaction.SETTLE_URI,
                                null, null, null, null);
            }
            feedRecyclerViewAdapter = new FeedRecyclerViewAdapter(getActivity(),cursor);
            recyclerView.setAdapter(feedRecyclerViewAdapter);

        }
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURSORLOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
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
        feedRecyclerViewAdapter.swapCursor(data);
        feedRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        feedRecyclerViewAdapter.swapCursor(null);
        feedRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }*/
}
