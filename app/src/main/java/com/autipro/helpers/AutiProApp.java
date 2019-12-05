package com.autipro.helpers;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.autipro.R;
import com.autipro.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;

public class AutiProApp extends Application {

    private final String TAG = "AutiProApp";
    private final String LOUDNESS = "loudness";
    private final String RUNNING = "running";
    private final String DROWNING = "drowning";

    @Override
    public void onCreate() {
        super.onCreate();
        subscribeToTopic(LOUDNESS);
        subscribeToTopic(RUNNING);
        subscribeToTopic(DROWNING);
    }

    private void subscribeToTopic(String topic){
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                    }
                });
    }
}
