package com.autipro.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.autipro.helpers.Config;
import com.autipro.sqlite.KeyValueDb;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int login_state = Integer.parseInt(KeyValueDb.get(this, Config.LOGIN_STATE,"0"));
        if(login_state == 0)
        startActivity(new Intent(this, LoginActivity.class));
        else
            startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
