package gj.udacity.capstone.hisab.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.activity.MainActivity;
import gj.udacity.capstone.hisab.adapter.FeedRecyclerViewAdapter;
import gj.udacity.capstone.hisab.database.TransactionContract;

import static gj.udacity.capstone.hisab.R.id.amount;

public class FeedFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CURSORLOADER_ID = 1;
    private FeedRecyclerViewAdapter feedRecyclerViewAdapter;

    private static final String ARG = "mode";
    private int fragmentMode;

    public static TextView msg1,msg2;
    public static ImageView arrow,emptyImage;
    public static RecyclerView recyclerView;

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

        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        msg1 = (TextView) view.findViewById(R.id.msg1);
        msg2 = (TextView) view.findViewById(R.id.msg2);
        arrow = (ImageView) view.findViewById(R.id.arrow);
        emptyImage = (ImageView) view.findViewById(R.id.emptyImage);


        Cursor cursor;
        if (fragmentMode == 0) {
            cursor = getActivity().getContentResolver()
                    .query(
                            TransactionContract.Transaction.UNSETTLE_URI,
                            null, null, null, null);
        } else {
            cursor = getActivity().getContentResolver()
                    .query(
                            TransactionContract.Transaction.SETTLE_URI,
                            null, null, null, null);
        }
        feedRecyclerViewAdapter = new FeedRecyclerViewAdapter(getActivity(), cursor, fragmentMode);
        recyclerView.setAdapter(feedRecyclerViewAdapter);

        if (MainActivity.tabletDevice) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                DetailFragment detailFragment = DetailFragment.newInstance(
                        cursor.getString(0) + "_" + cursor.getString(1),
                        amount,
                        fragmentMode);
                detailFragment.setHasOptionsMenu(true);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detailInMain, detailFragment)
                        .addToBackStack("Details")
                        .commit();
            }
        }

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 || (dy < 0 && fab.isShown()))
                    fab.setVisibility(View.INVISIBLE);
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddSliderFragment fabBottomSheetDialogFragment = new AddSliderFragment();
                fabBottomSheetDialogFragment.show(
                        getActivity().getSupportFragmentManager(), fabBottomSheetDialogFragment.getTag());
            }
        });
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (fragmentMode == 0) {
            return new CursorLoader(getActivity(),
                    TransactionContract.Transaction.UNSETTLE_URI,
                    null,
                    null,
                    null,
                    null);
        } else {
            return new CursorLoader(getActivity(),
                    TransactionContract.Transaction.SETTLE_URI,
                    null,
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
}
