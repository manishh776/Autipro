package com.autipro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.autipro.helpers.Config;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        textView = findViewById(R.id.textViewData);
        fetchData();
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
}
