package com.autipro.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.autipro.R;
import com.autipro.adapters.NotificationAdapter;
import com.autipro.helpers.Config;
import com.autipro.models.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerViewData;
    private TextView textViewNoData;
    private ProgressDialog progressDialog;
    private ArrayList<Notification> notificationArrayList;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");

        recyclerViewData = findViewById(R.id.recyclerViewData);
        recyclerViewData.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewData.setHasFixedSize(true);
        textViewNoData = findViewById(R.id.nodatatext);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        fetchNotifications();
    }

    private void fetchNotifications() {
        progressDialog.show();
        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_NOTIFICATIONS);
        notifRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationArrayList = new ArrayList<>();
                progressDialog.dismiss();
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Notification notification = data.getValue(Notification.class);
                    if(notification!=null){
                        notificationArrayList.add(notification);
                    }
                }
                if(notificationArrayList.isEmpty()){
                    recyclerViewData.setVisibility(View.GONE);
                    textViewNoData.setVisibility(View.VISIBLE);
                }else{
                    recyclerViewData.setVisibility(View.VISIBLE);
                    NotificationAdapter adapter = new NotificationAdapter(NotificationActivity.this, notificationArrayList);
                    recyclerViewData.setAdapter(adapter);
                    textViewNoData.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }
}
