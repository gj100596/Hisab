package gj.udacity.capstone.hisab.gcm;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.database.TransactionContract;
import gj.udacity.capstone.hisab.util.Constant;
import gj.udacity.capstone.hisab.util.ServerRequest;


public class sendRequestResponseBroadcast extends BroadcastReceiver {

    private final int COLUMN_REASON_INDEX = 1;
    private final int COLUMN_DATE_INDEX = 2;
    private final int COLUMN_AMOUNT_INDEX = 3;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.e("Receiver","received");
        Bundle arg = intent.getExtras();
        String requestingUserNumber = arg.getString(context.getString(R.string.gcm_rec_req_bunlde_userid));
        String requestingUserName = arg.getString(context.getString(R.string.gcm_rec_req_bunlde_username));
        String senderName = arg.getString("SenderName");
        Boolean sendData = arg.getBoolean(context.getString(R.string.gcm_rec_req_bunlde_accept));

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(2);

        if(sendData) {
            String url = Constant.url + "/hisab/requestresponse";
            JSONObject param = new JSONObject();
            try {
                SharedPreferences userDetail = context.
                        getSharedPreferences(context.getString(R.string.user_shared_preef), Context.MODE_PRIVATE);
                param.put("SenderID", userDetail
                        .getString(context.getString(R.string.shared_pref_number), context.getString(R.string.default_usernumber)));
                param.put("TargetID", requestingUserNumber);
                param.put("Name", requestingUserName);
                JSONArray transaction = new JSONArray();
                Cursor particularTransaction = context.getContentResolver()
                        .query(TransactionContract.Transaction.buildUnSettleDetailURI(
                                requestingUserNumber),
                                null, null, null, null, null);
                int sum = 0;
                if(particularTransaction.getCount()>0) {
                     do{
                        JSONObject entry = new JSONObject();
                        entry.put("Reason", particularTransaction.getString(COLUMN_REASON_INDEX));
                        entry.put("Date", particularTransaction.getString(COLUMN_DATE_INDEX));
                        entry.put("Amount", particularTransaction.getInt(COLUMN_AMOUNT_INDEX));
                        sum += particularTransaction.getInt(COLUMN_AMOUNT_INDEX);
                        transaction.put(entry);
                    }while (particularTransaction.moveToNext());
                }
                particularTransaction.close();
                param.put("Transaction", transaction);
                param.put("Amount", sum);
                Log.e("Pram_log",param.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest responseRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
            );
            ServerRequest.getInstance(context).getRequestQueue().add(responseRequest);
        }
    }
}
