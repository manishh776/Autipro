package com.autipro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.autipro.adapters.DataAdapter;
import com.autipro.helpers.Config;
import com.autipro.helpers.DataType;
import com.autipro.models.SensorData;
import com.autipro.models.User;
import com.autipro.sqlite.KeyValueDb;
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
    private String loudnessLimit;
    private String runningLimit, drowningLimit;
    private TextView charts;


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
        charts = findViewById(R.id.charts);

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
        charts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DataActivity.this, LineChartActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", sensorDataArrayList);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        });
        fetchUserData();
    }

    private void fetchUserData() {
        progressDialog.show();
        String user_id = KeyValueDb.get(this, Config.ID,"");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_USERS).child(user_id);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user!=null){
                    loudnessLimit = user.getLoudVoice();
                    runningLimit = user.getRunning();
                    drowningLimit = user.getDrowning();
                    fetchData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchData() {
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
                    String limit = "";
                    if(type == DataType.Sensor.LOUD_VOICES){
                        limit = loudnessLimit;
                    }else if(type == DataType.Sensor.RUNNING){
                        limit = runningLimit;
                    }else if(type == DataType.Sensor.DROWNING){
                        limit = drowningLimit;
                    }
                    dataAdapter = new DataAdapter(DataActivity.this, sensorDataArrayList, type, limit);
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
