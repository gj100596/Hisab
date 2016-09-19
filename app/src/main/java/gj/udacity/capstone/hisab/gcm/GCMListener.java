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

    public static final String NOTIFICATION_TYPE_REMINDER = "Reminder";
    public static final String NOTIFICATION_TYPE_REQUEST = "request";

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
                if (jsonObject.getString("type").equalsIgnoreCase(NOTIFICATION_TYPE_REMINDER))
                    sendNotification(jsonObject);
                else
                    sendNotification(jsonObject);
            } catch (JSONException e) {
            }
        }
    }

    /**
     * Put the message into a notification and post it.
     * This is just one simple example of what you might choose to do with a GCM message.
     *
     * @param message The alert message to be posted.
     */
    private void sendNotification(JSONObject message) {
        try {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Log.e("dmda", message.toString());

            String type = message.getString("type");

            Intent notificationIntent = null;
            Bundle arg = new Bundle();
            arg.putString("type", type);
            String messageTitle = null;

            notificationIntent = new Intent(this, MainActivity.class);
            messageTitle = message.getString("title");
            Log.e("Notification_question", "true");
            notificationIntent.putExtras(arg);

            PendingIntent contentIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(messageTitle)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message.getString("body").replace("grp:", " ")))
                    .setContentText(message.getString("body").replace("grp:", " "))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setNumber(++NOTIFICATION_NUM);

            mBuilder.setContentIntent(contentIntent);
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
