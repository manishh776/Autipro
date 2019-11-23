package com.autipro.video.push;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.autipro.R;
import com.sinch.android.rtc.PushTokenRegistrationCallback;
import com.sinch.android.rtc.SinchError;

public class LoginActivity extends BaseActivity implements SinchService.StartFailedListener, PushTokenRegistrationCallback {

    private Button mLoginButton;
    private EditText mLoginName;
    private ProgressDialog mSpinner;
    private boolean mPushTokenIsRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mLoginName = findViewById(R.id.loginName);
        mLoginButton = findViewById(R.id.loginButton);
        mLoginButton.setEnabled(false);
        mLoginButton.setOnClickListener(v -> loginClicked());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // for simplicity assume that we're not registered yet
        mPushTokenIsRegistered = false;
    }

    @Override
    protected void onServiceConnected() {
        mLoginButton.setEnabled(true);
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    protected void onPause() {
        dismissSpinner();
        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        dismissSpinner();
    }

    @Override
    public void onStarted() {
        nextActivityIfReady();
    }

    private void loginClicked() {
        String username = mLoginName.getText().toString();
        getSinchServiceInterface().setUsername(username);

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }

        if (!username.equals(getSinchServiceInterface().getUsername())) {
            getSinchServiceInterface().stopClient();
        }

        if (!mPushTokenIsRegistered) {
            getSinchServiceInterface().registerPushToken(this);
        }

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient();
            showSpinner();
        } else {
            nextActivityIfReady();
        }
    }

    private void openPlaceCallActivity() {
        Intent mainActivity = new Intent(this, PlaceCallActivity.class);
        startActivity(mainActivity);
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }

    private void dismissSpinner() {
        if (mSpinner != null) {
            mSpinner.dismiss();
            mSpinner = null;
        }
    }

    @Override
    public void tokenRegistered() {
        dismissSpinner();
        mPushTokenIsRegistered = true;
        nextActivityIfReady();
    }

    @Override
    public void tokenRegistrationFailed(SinchError sinchError) {
        dismissSpinner();
        mPushTokenIsRegistered = false;
        Toast.makeText(this, "Push token registration failed - incoming calls can't be received!", Toast.LENGTH_LONG).show();
    }

    private void nextActivityIfReady() {
        if (mPushTokenIsRegistered && getSinchServiceInterface().isStarted()) {
            openPlaceCallActivity();
        }
    }
}
