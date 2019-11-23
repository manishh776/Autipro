package com.autipro.firebase;

/**
 * Created by Manish on 11/22/2016.
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import com.autipro.R;
import com.autipro.helpers.Config;
import com.autipro.models.Notification;
import com.autipro.sqlite.KeyValueDb;
import com.autipro.video.push.SinchService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sinch.android.rtc.NotificationResult;
import com.sinch.android.rtc.SinchHelpers;
import com.sinch.android.rtc.calling.CallNotificationResult;
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
    private final String PREFERENCE_FILE = "com.sinch.android.rtc.sample.video.push.shared_preferences";
    SharedPreferences sharedPreferences;

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
            if(!data.containsKey("sinch")){
                //getting the json data
                String title = data.get("title");
                String body = data.get("body");
                Log.d(TAG, title +"DATA" + body);
                showNotification(title, body);
                saveNotification(title, body);
            }else{
                Log.d(TAG, "Contains sinch");
                handleSinch(data);
            }
        }
        catch (Exception e) {
            Log.e("Exception",e.getMessage());
        }
    }

    private void handleSinch(Map<String, String> data) {
        if (SinchHelpers.isSinchPushPayload(data)) {
            new ServiceConnection() {
                private Map payload;
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    Context context = getApplicationContext();
                    sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
                    if (payload != null) {
                        SinchService.SinchServiceInterface sinchService = (SinchService.SinchServiceInterface) service;
                        if (sinchService != null) {
                            NotificationResult result = sinchService.relayRemotePushNotificationPayload(payload);
                            // handle result, e.g. show a notification or similar
                            // here is example for notifying user about missed/canceled call:
                            if (result.isValid() && result.isCall()) {
                                CallNotificationResult callResult = result.getCallResult();
                                if (callResult != null && result.getDisplayName() != null) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(callResult.getRemoteUserId(), result.getDisplayName());
                                    editor.commit();
                                }
                                if (callResult.isCallCanceled()) {
                                    String displayName = result.getDisplayName();
                                    if (displayName == null) {
                                        displayName = sharedPreferences.getString(callResult.getRemoteUserId(),"n/a");
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        context.deleteSharedPreferences(PREFERENCE_FILE);
                                    }
                                }
                            }
                        }
                    }
                    payload = null;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {}

                public void relayMessageData(Map<String, String> data) {
                    payload = data;
                    getApplicationContext().bindService(new Intent(getApplicationContext(), SinchService.class), this, BIND_AUTO_CREATE);
                }
            }.relayMessageData(data);
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

