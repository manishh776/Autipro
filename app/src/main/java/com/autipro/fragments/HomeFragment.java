package com.autipro.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autipro.DataActivity;
import com.autipro.R;
import com.autipro.activities.NotificationActivity;
import com.autipro.helpers.Config;
import com.autipro.helpers.DataType;
import com.autipro.models.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout running, drowning, loudVoices, detectTantrum;
    private ImageView notificationTag;
    private TextView noofNotifications;
    private String TAG = HomeFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        running = view.findViewById(R.id.runningContainer);
        drowning = view.findViewById(R.id.drowningContainer);
        loudVoices = view.findViewById(R.id.loudVoicesContainer);
        notificationTag = view.findViewById(R.id.notificationTag);
        noofNotifications = view.findViewById(R.id.noOfNotifications);
        detectTantrum = view.findViewById(R.id.detectTantrum);

        running.setOnClickListener(this);
        drowning.setOnClickListener(this);
        loudVoices.setOnClickListener(this);
        notificationTag.setOnClickListener(this);
        detectTantrum.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        setNumberOfNotifications();
    }

    private void setNumberOfNotifications() {
        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_NOTIFICATIONS);
        notifRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Notification notification = data.getValue(Notification.class);
                    Log.d(TAG, "COUNT=" + count);
                    if(notification!=null && !notification.isRead()){
                        Log.d(TAG, "COUNT=" + count);
                        count++;
                    }
                }
                Log.d(TAG, "AFTERCOUNT=" + count);
                if(count == 0){
                    noofNotifications.setVisibility(View.GONE);
                }else{
                    noofNotifications.setVisibility(View.VISIBLE);
                    noofNotifications.setText(count+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), DataActivity.class);
        DataType.Sensor type = null;
        if(view == notificationTag){
            startActivity(new Intent(getActivity(), NotificationActivity.class));
            return;
        }
        else if(view == running){
            type = DataType.Sensor.RUNNING;
        } else if (view == drowning) {
            type = DataType.Sensor.DROWNING;
        } else if (view == loudVoices){
            type = DataType.Sensor.LOUD_VOICES;
        }else if(view == detectTantrum){
            type = DataType.Sensor.TANTRUM;
        }
        intent.putExtra("type", type);
        startActivity(intent);
    }
}
