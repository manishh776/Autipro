package com.autipro.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.autipro.R;
import com.autipro.adapters.ViewpagerAdapter;
import com.autipro.helpers.Config;
import com.autipro.sqlite.KeyValueDb;
import com.autipro.video.push.BaseActivity;
import com.autipro.video.push.PlaceCallActivity;
import com.autipro.video.push.SinchService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.SinchError;

public class MainActivity extends BaseActivity implements View.OnClickListener,  SinchService.StartFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ACCESS_PERMISSION_REQUEST = 123;
    private ProgressDialog progressDialog;
    private TextView textView;
    private ViewpagerAdapter viewpagerAdapter;
    public ViewPager viewPager;
    private ImageView settings, live, statistics, contactUs;
    private final int SETTINGS = 0, LIVE = 1, STATS = 2, CONTACT_US = 3;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getRequiredPermissions();
        }
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

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "onServiceConnected");
        getSinchServiceInterface().setStartListener(this);
        if (!getSinchServiceInterface().isStarted()) {
            sinchLogin();
            progressDialog.show();
            Log.d(TAG, "openPlaceCallActivity not started");
        }
    }

    private void sinchLogin() {
        username = KeyValueDb.get(this, Config.USERNAME,"");
        Log.d(TAG, "username" + username);
        getSinchServiceInterface().setUsername(username);

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }
        if (!username.equals(getSinchServiceInterface().getUsername())) {
            getSinchServiceInterface().stopClient();
        }
        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient();
        }
    }

    @Override
    public void onClick(View view) {
        if(view == settings){
            viewPager.setCurrentItem(SETTINGS);
        } else if(view == live){
            openPlaceCallActivity();
        }else if(view == statistics){
            viewPager.setCurrentItem(STATS);
        }else if(view == contactUs){
            viewPager.setCurrentItem(CONTACT_US);
        }
    }

    private void openPlaceCallActivity() {
        Log.d(TAG, "openPlaceCallActivity");
        if (!getSinchServiceInterface().isStarted()) {
            sinchLogin();
            progressDialog.show();
            Log.d(TAG, "openPlaceCallActivity not started");
        } else {
            Log.d(TAG, "openPlaceCallActivity started");
            startActivity(new Intent(this, PlaceCallActivity.class));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getRequiredPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

        ) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.MODIFY_AUDIO_SETTINGS,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CAMERA
                    },
                    ACCESS_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == ACCESS_PERMISSION_REQUEST) {
            if (grantResults.length == 5 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[4] == PackageManager.PERMISSION_GRANTED
            ) {
                //Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }else{
                    Log.e(TAG,"in permissions map is null");
                }
            } else {
                Toast.makeText(getApplicationContext(), " permission denied", Toast.LENGTH_SHORT).show();
            }
        }


    @Override
    public void onStartFailed(SinchError error) {
        Log.d(TAG, "onStartFailed" + error.getMessage());
    }

    @Override
    public void onStarted() {
        Log.d(TAG, "onStarted");
        progressDialog.dismiss();
    }
}
