package com.autipro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.autipro.adapters.DataAdapter;
import com.autipro.helpers.Config;
import com.autipro.helpers.DataType;
import com.autipro.models.SensorData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DataActivity extends AppCompatActivity {

    private TextView textViewTitle;
    private RecyclerView recyclerViewData;
    private DataAdapter dataAdapter;
    private ProgressDialog progressDialog;
    private ArrayList<SensorData> sensorDataArrayList;
    private TextView nodatatext;
    private DataType.Sensor type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");
        recyclerViewData = findViewById(R.id.recyclerViewData);
        recyclerViewData.setLayoutManager(new LinearLayoutManager(this));
        textViewTitle = findViewById(R.id.title);
        nodatatext = findViewById(R.id.nodatatext);

        type = (DataType.Sensor) getIntent().getSerializableExtra("type");
        if (type != null)
            textViewTitle.setText(type.toString());
        else
            Toast.makeText(this, "type is null", Toast.LENGTH_SHORT).show();

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fetchData();
    }

    private void fetchData() {
        progressDialog.show();
        DatabaseReference sensorRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_SENSORS_DATA);
        sensorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sensorDataArrayList = new ArrayList<>();
                progressDialog.dismiss();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    SensorData sensorData = data.getValue(SensorData.class);
                    if (sensorData != null)
                        sensorDataArrayList.add(sensorData);

                }
                if(sensorDataArrayList.isEmpty()){
                    recyclerViewData.setVisibility(View.GONE);
                    nodatatext.setVisibility(View.VISIBLE);
                }else{
                    recyclerViewData.setVisibility(View.VISIBLE);
                    nodatatext.setVisibility(View.GONE);
                    dataAdapter = new DataAdapter(DataActivity.this, sensorDataArrayList, type);
                    recyclerViewData.setAdapter(dataAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }
}
