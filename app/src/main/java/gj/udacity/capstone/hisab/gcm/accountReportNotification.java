package gj.udacity.capstone.hisab.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class accountReportNotification extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.e("Receiver","received");
        Bundle arg = intent.getExtras();

        String url  = "/group/adduser";
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
