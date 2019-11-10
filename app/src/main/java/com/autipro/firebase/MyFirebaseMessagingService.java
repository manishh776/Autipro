package com.autipro.firebase;

/**
 * Created by Manish on 11/22/2016.
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.autipro.R;
import com.autipro.helpers.Config;
import com.autipro.models.Notification;
import com.autipro.sqlite.KeyValueDb;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private int notification_id = 0, message_id = 0;
    private String CHANNEL_ID = "borrow";
    String TYPE;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        KeyValueDb.set(getApplicationContext(), Config.TOKEN,s,1);
        Log.d("Refreshed Token", s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("onMessageReceived","Check");
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                Log.d("Try","Just got into try");
                parseNotificationData(remoteMessage.getData());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

    }


    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void parseNotificationData(Map<String, String> data) {
        //optionally we can display the json into log
        try {
            //getting the json data
            String title = data.get("title");
            String body = data.get("body");
            Log.d(TAG, title +"DATA" + body);
            showNotification(title, body);
            saveNotification(title, body);
        }
        catch (Exception e) {
            Log.e("Exception",e.getMessage());
        }
    }

    private void saveNotification(String title, String body) {
        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_NOTIFICATIONS);
        String id = notifRef.push().getKey();
        Notification notify = new Notification(id,title, body, getTime(), false);
        notifRef.child(id).setValue(notify);
    }

    private void showNotification(String title, String message) {
        Log.d(TAG, "showNotifications");
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setShowWhen(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notification_id++, mBuilder.build());
    }

    public String getTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return df.format(c.getTime());
    }
}

