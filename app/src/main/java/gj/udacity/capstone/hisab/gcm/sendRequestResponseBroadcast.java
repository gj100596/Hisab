package gj.udacity.capstone.hisab.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.util.Constant;


public class sendRequestResponseBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.e("Receiver","received");
        Bundle arg = intent.getExtras();
        String requestingUserName = arg.getString(context.getString(R.string.gcm_rec_req_bunlde_userid));
        Boolean sendData = arg.getBoolean(context.getString(R.string.gcm_rec_req_bunlde_accept));

        String url  = Constant.url + "/hisab/requestResponse";
        JSONObject param = new JSONObject();
        try {
            JSONArray mem = new JSONArray();
            param.put("GroupID",arg.getString("GroupID"));
            mem.put(arg.getString("UserID"));
            param.put("UID",mem);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
