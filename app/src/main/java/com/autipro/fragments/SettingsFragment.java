package com.autipro.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.autipro.R;
import com.autipro.activities.LoginActivity;
import com.autipro.activities.MainActivity;
import com.autipro.helpers.Config;
import com.autipro.models.User;
import com.autipro.sqlite.KeyValueDb;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private EditText username, email, age, password;
    private Button buttonUpdate;
    private ProgressDialog progressDialog;
    private final String EMPTY_ERROR = "Can't be empty";
    private String user_id;
    private DatabaseReference userRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        age = view.findViewById(R.id.age);
        password = view.findViewById(R.id.password);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    saveDetails();
                }
            }
        });

        view.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        fetchUser();
    }

    private void logout() {
        KeyValueDb.set(getActivity(), Config.LOGIN_STATE, "0",1);

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void saveDetails() {
        progressDialog.show();
        String usernameData = username.getText().toString();
        String passwordData = password.getText().toString();
        String ageData = age.getText().toString();
        String emailData = email.getText().toString();
        userRef.child("username").setValue(usernameData);
        userRef.child("password").setValue(passwordData);
        userRef.child("age").setValue(ageData);
        userRef.child("email").setValue(emailData);

        KeyValueDb.set(getActivity(), Config.AGE,ageData,1);

        progressDialog.dismiss();
        showToast("profile updated");
        if(getActivity()!= null)
        ((MainActivity) getActivity()).viewPager.setCurrentItem(1);
    }

    private void showToast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean validate() {
        boolean allOkay = true;

        if(TextUtils.isEmpty(username.getText())){
            username.setError(EMPTY_ERROR);
            allOkay = false;
        }
        if(TextUtils.isEmpty(password.getText())){
            password.setError(EMPTY_ERROR);
            allOkay = false;
        }
        if(TextUtils.isEmpty(age.getText())){
            age.setError(EMPTY_ERROR);
            allOkay = false;
        }
        if(TextUtils.isEmpty(email.getText())){
            email.setError(EMPTY_ERROR);
            allOkay = false;
        }

        return allOkay;
    }

    private void fetchUser() {
        progressDialog.show();
         user_id = KeyValueDb.get(getActivity(), Config.ID,"");
        userRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_USERS).child(user_id);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                User user = dataSnapshot.getValue(User.class);
                if(user!=null){
                    username.setText(user.getUsername());
                    email.setText(user.getEmail());
                    age.setText(user.getAge() + "");
                    password.setText(user.getPassword());
                }else{
                    showToast("User is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }
}
