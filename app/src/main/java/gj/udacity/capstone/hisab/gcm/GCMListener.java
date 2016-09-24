package gj.udacity.capstone.hisab.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.activity.MainActivity;

public class GCMListener extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    public static final int NOTIFICATION_ID = 1;
    public static final int NOTIFICATION_ID_REMINDER = 2;

    public static int NOTIFICATION_NUM = 0;

    // A reminder has been sent to you.
    public static final String NOTIFICATION_TYPE_REMINDER = "reminder";
    // Request for Sync is received
    public static final String NOTIFICATION_TYPE_REQUEST = "request";
    // Response of your request has received.
    public static final String NOTIFICATION_TYPE_REQUEST_RESPONSE = "request_response";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.e("GCM", data.toString());
        // Time to unparcel the bundle!

        if (!data.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(data.getString("msg"));
                String type = jsonObject.getString("type");
                if (type.equalsIgnoreCase(NOTIFICATION_TYPE_REQUEST_RESPONSE))
                    handleRequestResponse(jsonObject);
                else if (type.equalsIgnoreCase(NOTIFICATION_TYPE_REMINDER))
                    showReminder(jsonObject);
                else
                    handleReceivedRequest(jsonObject);
            } catch (JSONException e) {
            }
        }
    }

    private void showReminder(JSONObject message) {
        try {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Intent notificationIntent = new Intent(this, MainActivity.class);
            String messageTitle = message.getString("title");

            PendingIntent contentIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(messageTitle)
                    .setStyle(new NotificationCompat.BigTextStyle().
                            bigText(message.getString("body").replace("grp:", " ")))
                    .setContentText(message.getString("body").replace("grp:", " "))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setNumber(++NOTIFICATION_NUM);

            mBuilder.setContentIntent(contentIntent);
            mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Notification error", e.toString());
        }
    }

    private void handleRequestResponse(JSONObject jsonObject) {
        /*
        //getContentResolver().query()
        String url = Constant.url + "/requestResponse";
        JSONObject param = new JSONObject();
        /*
        try {
            param.put("UserID", FixedData.getUserID(getActivity()));
            param.put("Password", newPass.getText().toString());
            param.put("OldPassword", oldPass.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        *
        JsonObjectRequest change = new JsonObjectRequest(Request.Method.POST, url, param,
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

        ServerRequest.getInstance(getActivity()).getRequestQueue().add(change);
        */
    }

    /**
     * Put the message into a notification and post it.
     * This is just one simple example of what you might choose to do with a GCM message.
     *
     * @param message The alert message to be posted.
     */
    private void handleReceivedRequest(JSONObject message) {
        try {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            String type = message.getString("type");
            String requestingUser = message.getString("req_user");

            // Intent to go to app
            Intent notificationIntent = null;
            Bundle arg = new Bundle();
            arg.putString(getString(R.string.gcm_rec_req_bunlde_type), type);
            arg.putString(getString(R.string.gcm_rec_req_bunlde_userid),requestingUser);
            notificationIntent = new Intent(this, MainActivity.class);
            Log.e("Request_received", "true");
            notificationIntent.putExtras(arg);
            PendingIntent contentIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Create Notification Builder
            String messageTitle = requestingUser + " Asking";
            String messageBody = requestingUser + "has asked for Syncing Transaction With you.";

            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(messageTitle)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(messageBody))
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setNumber(++NOTIFICATION_NUM);

            mBuilder.setContentIntent(contentIntent);

            // Intent for actions.
            // When User wants to send the response with data
            Intent yesIntent = new Intent();
            Bundle yesArg = new Bundle();
            yesArg.putString(getString(R.string.gcm_rec_req_bunlde_userid), requestingUser);
            yesArg.putBoolean(getString(R.string.gcm_rec_req_bunlde_accept),true);
            yesIntent.setAction("gj.udacity.capstone.hisab.SEND_REQUEST_RESPONSE");
            yesIntent.putExtras(arg);

            PendingIntent yesPendingIntent =
                    PendingIntent.getBroadcast(this, 0, yesIntent, 0);
            mBuilder.addAction(R.drawable.ic_done_black_36dp,"Send",yesPendingIntent);

            // When User does not wants to sync
            Intent noIntent = new Intent();
            Bundle noArg = new Bundle();
            noArg.putString(getString(R.string.gcm_rec_req_bunlde_userid), requestingUser);
            noArg.putBoolean(getString(R.string.gcm_rec_req_bunlde_accept),false);
            noIntent.setAction("gj.udacity.capstone.hisab.SEND_REQUEST_RESPONSE");
            noIntent.putExtras(arg);

            PendingIntent noPendingIntent =
                    PendingIntent.getBroadcast(this, 0, noIntent, 0);
            mBuilder.addAction(R.drawable.ic_clear_black_36dp,"Cancel",noPendingIntent);

            // Publish Notification
            mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

            if (type.equalsIgnoreCase(NOTIFICATION_TYPE_REMINDER))
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            else
                mNotificationManager.notify(NOTIFICATION_ID_REMINDER, mBuilder.build());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Notification error", e.toString());
        }
    }
}
