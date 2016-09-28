package gj.udacity.capstone.hisab.fragment;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.activity.MainActivity;
import gj.udacity.capstone.hisab.adapter.DetailRecyclerViewAdapter;
import gj.udacity.capstone.hisab.adapter.DetailViewNotificationAdapter;
import gj.udacity.capstone.hisab.database.TransactionContract;

import static gj.udacity.capstone.hisab.database.TransactionContract.BASE_URI;

public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_1 = "userString";
    private static final String ARG_2 = "mode";
    private static final String ARG_3 = "totalAmount";
    private static final int CURSORLOADER_ID = 750;

    //Combination of name and number of particular friend
    private String userString, userName, userNumber;
    //fragmentMode has 2 value: 0 for Unsettle Transaction and 1 for settled transaction
    private int fragmentMode, totalAmount;
    //If this is null then a normal start otherwise it will have data from notification
    private Bundle notificationBundle = null;

    private DetailRecyclerViewAdapter detailRecyclerViewAdapter;
    private RecyclerView historyList;
    public static TextView bottomTotalAmount;
    public static TextView detailMsg1;
    public static ImageView detailEmptyImage;
    public static CardView detailCardView;
    Cursor cursor;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(String id, int totalAmount, int mode) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_1, id);
        args.putInt(ARG_2, mode);
        args.putInt(ARG_3, totalAmount);
        fragment.setArguments(args);
        return fragment;
    }

    public static DetailFragment newInstance(Bundle args) {
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().getString("Type") == null) {
                userString = getArguments().getString(ARG_1);
                String[] split = userString.split("_");
                userName = split[0];
                userNumber = split[1];
                fragmentMode = getArguments().getInt(ARG_2);
                totalAmount = getArguments().getInt(ARG_3);
            } else {
                notificationBundle = getArguments();
                userName = notificationBundle.getString("User");
                userNumber = notificationBundle.getString("User");
                totalAmount = notificationBundle.getInt("Sum");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView nameTextView = (TextView) view.findViewById(R.id.history_name);
        nameTextView.setText(userName);

        historyList = (RecyclerView) view.findViewById(R.id.history_list);
        historyList.setLayoutManager(new LinearLayoutManager(getActivity()));

        final RelativeLayout bottomBar = (RelativeLayout) view.findViewById(R.id.bottomBar);
        bottomTotalAmount = (TextView) view.findViewById(R.id.bottomAmount);
        bottomTotalAmount.setText("" + totalAmount);

        detailMsg1 = (TextView) view.findViewById(R.id.msg1);
        detailEmptyImage = (ImageView) view.findViewById(R.id.emptyImage);
        detailCardView = (CardView) view.findViewById(R.id.detailCardView);


        historyList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    bottomBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 || (dy < 0 && bottomBar.getVisibility() == View.VISIBLE))
                    bottomBar.setVisibility(View.INVISIBLE);
            }
        });
        Uri uri;
        if (fragmentMode == 0) {
            uri = TransactionContract.Transaction.UNSETTLE_URI;
            cursor = getActivity().getContentResolver()
                    .query(
                            TransactionContract.Transaction.buildUnSettleDetailURI(userString),
                            null, null, null, null);
        } else {
            uri = TransactionContract.Transaction.SETTLE_URI;
            cursor = getActivity().getContentResolver()
                    .query(
                            TransactionContract.Transaction.buildSettleDetailURI(userString),
                            null, null, null, null);
        }
        cursor.setNotificationUri(getActivity().getContentResolver(), uri);
        if (notificationBundle == null) {
            detailRecyclerViewAdapter = new DetailRecyclerViewAdapter(
                    getActivity(), cursor, userName, userNumber, fragmentMode, bottomTotalAmount);
            historyList.setAdapter(detailRecyclerViewAdapter);

        } else {
            try {
                JSONArray array = new JSONArray(notificationBundle.getString("data"));
                DetailViewNotificationAdapter detailViewNotificationAdapter = new DetailViewNotificationAdapter(array);
                historyList.setAdapter(detailViewNotificationAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (notificationBundle == null)
            inflater.inflate(R.menu.detail_fragment_menu, menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (notificationBundle != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Do you Want to Save This Transactions?");
            builder.setTitle("Save");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        JSONArray array = new JSONArray(notificationBundle.getString("data"));

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            String reason = jsonObject.getString("Reason");
                            String date = jsonObject.getString("Date");
                            int amount = jsonObject.getInt("Amount");

                            cursor.moveToFirst();
                            boolean already = false;
                            for(int j=0;j<cursor.getCount();j++) {
                                cursor.move(j);
                                if (cursor.getString(2).equalsIgnoreCase(date)
                                        &&
                                        cursor.getInt(3) == amount) {
                                    already=true;
                                    break;
                                }
                            }
                            if(!already) {
                                amount *= -1;

                                ContentValues values = new ContentValues();
                                values.put(TransactionContract.Transaction.COLUMN_NAME, userName);
                                values.put(TransactionContract.Transaction.COLUMN_NUMBER, userNumber);
                                values.put(TransactionContract.Transaction.COLUMN_REASON, reason);
                                values.put(TransactionContract.Transaction.COLUMN_AMOUNT, amount);
                                values.put(TransactionContract.Transaction.COLUMN_SETTLED, 0);
                                values.put(TransactionContract.Transaction.COLUMN_CATEGORY, "Misc");
                                values.put(TransactionContract.Transaction.COLUMN_DATE, date);

                                MainActivity.thisAct.getContentResolver().insert(BASE_URI, values);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.settleTransaction) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            if (fragmentMode == 0) {
                builder.setMessage("Do You want to settle Complete Transaction?");
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
            } else {
                builder.setMessage("Do You want to Delete Complete Transaction Permanently?");
                builder.setTitle("Delete?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String args = userName + "_" + userNumber;
                        getActivity().getContentResolver().delete(
                                TransactionContract.Transaction.buildAllPermanentDeleteURI(args),
                                null, null);
                        getActivity().onBackPressed();
                    }
                });
            }
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (notificationBundle == null)
            getLoaderManager().initLoader(CURSORLOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (fragmentMode == 0) {
            return new CursorLoader(getActivity(),
                    TransactionContract.Transaction.buildUnSettleDetailURI(userString),
                    null,
                    null,
                    null,
                    null);
        } else {
            return new CursorLoader(getActivity(),
                    TransactionContract.Transaction.buildSettleDetailURI(userString),
                    null,
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
