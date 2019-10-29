package com.autipro.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.autipro.R;
import com.autipro.helpers.Config;
import com.autipro.models.User;
import com.autipro.sqlite.KeyValueDb;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username, password;
    private Button buttonLogin;
    private ProgressDialog progressDialog;
    private String usernametxt, passwordtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
    }

    private void initViews() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        buttonLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == buttonLogin){
            if(validate()){
                loginUser();
            }
        }
    }

    private void loginUser() {
        progressDialog.show();
        usernametxt = username.getText().toString();
        passwordtxt = password.getText().toString();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_USERS);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    if(user!=null){
                        if(user.getUsername().equals(usernametxt) && user.getPassword().equals(passwordtxt)){
                            saveAndMovetoMainActivity(ds.getKey());
                            Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                Toast.makeText(LoginActivity.this, "No records found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    private void saveAndMovetoMainActivity(String key) {
        KeyValueDb.set(this, Config.USERNAME, usernametxt,1);
        KeyValueDb.set(this, Config.ID, key,1);
        KeyValueDb.set(this, Config.LOGIN_STATE,"1",1);
        updateToken(key);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void updateToken(String key) {
        String token = KeyValueDb.get(this, Config.TOKEN,"");
        DatabaseReference ds = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_USERS).child(key);
        ds.child("token").setValue(token);
    }

    private boolean validate() {
        boolean allOkay = true;
        if(TextUtils.isEmpty(username.getText())){
            username.setError("Cannot be empty");
            allOkay = false;
        }

        if(TextUtils.isEmpty(password.getText())){
            password.setError("Cannot be empty");
            allOkay = false;
        }
        return allOkay;
    }

}
