package com.autipro.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.autipro.R;
import com.autipro.adapters.ViewpagerAdapter;
import com.autipro.helpers.Config;
import com.autipro.sqlite.KeyValueDb;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private TextView textView;
    private ViewpagerAdapter viewpagerAdapter;
    public ViewPager viewPager;
    private ImageView settings, live, statistics, contactUs;
    private final int SETTINGS = 0, LIVE = 1, STATS = 2, CONTACT_US = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        viewPager = findViewById(R.id.viewPager);
        viewpagerAdapter = new ViewpagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewpagerAdapter);
        int age = Integer.parseInt(KeyValueDb.get(this, Config.AGE,"0"));
        if(age == 0)
        viewPager.setCurrentItem(0);
        else
            viewPager.setCurrentItem(1);
       // fetchData();

        settings = findViewById(R.id.settings);
        live = findViewById(R.id.live);
        statistics = findViewById(R.id.stats);
        contactUs = findViewById(R.id.contactUs);

        settings.setOnClickListener(this);
        live.setOnClickListener(this);
        statistics.setOnClickListener(this);
        contactUs.setOnClickListener(this);

    }

    private void fetchData() {
        progressDialog.show();
        DatabaseReference ds = FirebaseDatabase.getInstance().getReference();
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                StringBuilder s = new StringBuilder();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.getValue().getClass() == String.class) {
                        String data = ds.getValue(String.class);
                        if (data != null) {
                            s.append(data).append("\n");
                            Log.d(TAG, data);
                        }
                    }
                }
                textView.setText(s.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == settings){
            viewPager.setCurrentItem(SETTINGS);
        }
        else if(view == live){
            viewPager.setCurrentItem(LIVE);
        }else if(view == statistics){
            viewPager.setCurrentItem(STATS);
        }else if(view == contactUs){
            viewPager.setCurrentItem(CONTACT_US);
        }
    }
}
