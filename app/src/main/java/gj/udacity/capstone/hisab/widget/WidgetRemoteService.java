package gj.udacity.capstone.hisab.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.database.TransactionContract;

public class WidgetRemoteService extends RemoteViewsService {
    private final int COLUMN_NAME_INDEX = 0;
    private final int COLUMN_NUMBER_INDEX = 1;
    private final int COLUMN_SUM_INDEX = 2;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver()
                        .query(
                                TransactionContract.Transaction.UNSETTLE_URI,
                                null, null, null, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {

                //Set The data cursor position
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }

                /*
                Now for each row create(i.e. here the row for which view is asked)
                get the remote view.
                 */
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_list_item);

                views.setTextViewText(R.id.reason, data.getString(COLUMN_NAME_INDEX));

                views.setTextViewText(R.id.date, data.getString(COLUMN_NUMBER_INDEX));

                int amount = data.getInt(COLUMN_SUM_INDEX);
                if ( amount < 0) {
                    views.setInt(R.id.amount,"setTextColor", getResources().getColor(R.color.sliderOptionBG));

                } else {
                    views.setInt(R.id.amount, "setBackgroundResource", getResources().getColor(R.color.materialGreen));
                }

                views.setTextViewText(R.id.amount, ""+amount);

                /*
                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(
                        getString(R.string.symbol_intent_keyword), data.getString(data.getColumnIndex(QuoteColumns.SYMBOL)));
                views.setOnClickFillInIntent(R.id.customListParent, fillInIntent);
                */

                return views;

            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data != null && data.moveToPosition(position)) {
                    return data.getLong(0);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }

}
