package gj.udacity.capstone.hisab.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.activity.MainActivity;
import gj.udacity.capstone.hisab.database.TransactionContract;

public class GCMListener extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    public static final int NOTIFICATION_ID = 1;
    public static final int NOTIFICATION_ID_REMINDER = 2;
    private static final int NOTIFICATION_REQUST = 3;

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

            String type = message.getString("type");
            String requestingUser = message.getString("req_user");
            int amount = message.getInt("amount");
            JSONArray jsonData = message.getJSONArray("transaction");
            String name = message.getString("name");

            Cursor cursor = getContentResolver().query(TransactionContract.Transaction.getNameNoUri(),
                    null, null, null, null, null);
            cursor.moveToFirst();
            if(cursor.getCount()>0) {
                do {
                    if (requestingUser.equalsIgnoreCase(cursor.getString(1))) {
                        name = cursor.getString(0);
                        break;
                    }
                } while (cursor.moveToNext());
            }

            Intent notificationIntent = new Intent(this, MainActivity.class);
            Bundle reminder = new Bundle();
            reminder.putString("Type", type);
            reminder.putString("User", requestingUser);
            reminder.putString("Name", name);
            reminder.putInt("Sum", amount);
            reminder.putString("data", jsonData.toString());
            notificationIntent.putExtras(reminder);

            PendingIntent contentIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            String messageTitle = name + getString(R.string.owe_me);
            String messageBody = name + getString(R.string.reminded_msg_1) + amount + getString(R.string.reminded_msg_2);
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(messageTitle)
                    .setStyle(new NotificationCompat.BigTextStyle().
                            bigText(messageBody))
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setNumber(++NOTIFICATION_NUM);

            mBuilder.setContentIntent(contentIntent);
            SharedPreferences userDetail =
                    getSharedPreferences(getString(R.string.user_shared_preef), Context.MODE_PRIVATE);
            Boolean sound = userDetail.getBoolean(getString(R.string.not_sound), true);
            Boolean vibrate = userDetail.getBoolean(getString(R.string.not_vibrate), true);

            if(sound && vibrate)
                mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
            else if(sound)
                mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS );
            else if(vibrate)
                mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
            else
                mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);


            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Notification error", e.toString());
        }
    }

    private void handleRequestResponse(JSONObject message) {
        try {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            String type = message.getString("type");
            String requestingUser = message.getString("req_user");
            JSONArray jsonData = message.getJSONArray("transaction");
            int amount = message.getInt("amount");
            String name = message.getString("name");

            Intent notificationIntent = new Intent(this, MainActivity.class);
            Bundle reminder = new Bundle();
            reminder.putString("Type", type);
            reminder.putString("User", requestingUser);
            reminder.putString("data", jsonData.toString());
            reminder.putInt("Sum", amount);
            reminder.putString("Name", name);
            notificationIntent.putExtras(reminder);

            PendingIntent contentIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            String messageTitle = getString(R.string.sync_result) + name;
            String messageBody = name + getString(R.string.sync_result_msg);
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(messageTitle)
                    .setStyle(new NotificationCompat.BigTextStyle().
                            bigText(messageBody))
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setNumber(++NOTIFICATION_NUM);

            mBuilder.setContentIntent(contentIntent);

            SharedPreferences userDetail =
                    getSharedPreferences(getString(R.string.user_shared_preef), Context.MODE_PRIVATE);
            Boolean sound = userDetail.getBoolean(getString(R.string.not_sound), true);
            Boolean vibrate = userDetail.getBoolean(getString(R.string.not_vibrate), true);

            if(sound && vibrate)
                mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
            else if(sound)
                mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS );
            else if(vibrate)
                mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
            else
                mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);


            mNotificationManager.notify(NOTIFICATION_REQUST, mBuilder.build());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Notification error", e.toString());
        }
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
            String name = message.getString("name");
            String senderName = message.getString("sender_name");


            // Intent to go to app
            Intent notificationIntent = null;
            Bundle arg = new Bundle();
            arg.putString(getString(R.string.gcm_rec_req_bunlde_type), type);
            arg.putString(getString(R.string.gcm_rec_req_bunlde_userid), requestingUser);
            notificationIntent = new Intent(this, MainActivity.class);
            Log.e("Request_received", "true");
            notificationIntent.putExtras(arg);
            PendingIntent contentIntent =
                    PendingIntent.getActivity(this, 2, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Create Notification Builder
            String messageTitle = senderName + getString(R.string.asking);
            String messageBody = senderName + getString(R.string.asking_msg);

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

            //mBuilder.setContentIntent(contentIntent);

            // Intent for actions.
            // When User wants to send the response with data
            Intent yesIntent = new Intent();
            Bundle yesArg = new Bundle();
            yesArg.putString(getString(R.string.gcm_rec_req_bunlde_userid), requestingUser);
            yesArg.putBoolean(getString(R.string.gcm_rec_req_bunlde_accept), true);
            yesArg.putString("Name",name);
            yesArg.putString("SenderName", senderName);
            yesIntent.setAction("gj.udacity.capstone.hisab.SEND_REQUEST_RESPONSE");
            yesIntent.putExtras(yesArg);

            PendingIntent yesPendingIntent =
                    PendingIntent.getBroadcast(this, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.addAction(R.drawable.ic_done_black_36dp, "Send", yesPendingIntent);

            // When User does not wants to sync

            Intent noIntent = new Intent();
            Bundle noArg = new Bundle();
            noArg.putString(getString(R.string.gcm_rec_req_bunlde_userid), requestingUser);
            noArg.putBoolean(getString(R.string.gcm_rec_req_bunlde_accept), false);
            noArg.putString(getString(R.string.gcm_rec_req_bunlde_username), name);

            noIntent.setAction("gj.udacity.capstone.hisab.SEND_REQUEST_RESPONSE");
            noIntent.putExtras(noArg);

            PendingIntent noPendingIntent =
                    PendingIntent.getBroadcast(this, 1, noIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.addAction(R.drawable.ic_clear_black_36dp, "Cancel", noPendingIntent);

            // Publish Notification
            SharedPreferences userDetail =
                    getSharedPreferences(getString(R.string.user_shared_preef), Context.MODE_PRIVATE);
            Boolean sound = userDetail.getBoolean(getString(R.string.not_sound), true);
            Boolean vibrate = userDetail.getBoolean(getString(R.string.not_vibrate), true);

            if(sound && vibrate)
                mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
            else if(sound)
                mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS );
            else if(vibrate)
                mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
            else
                mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);

            mNotificationManager.notify(NOTIFICATION_ID_REMINDER, mBuilder.build());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Notification error", e.toString());
        }
    }
}
