package com.autipro.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autipro.R;
import com.autipro.helpers.Config;
import com.autipro.helpers.DataType;
import com.autipro.models.SensorData;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private Context context;
    private ArrayList<SensorData> sensorDataArrayList;
    private DataType.Sensor type;

    public DataAdapter(Context context, ArrayList<SensorData> sensorDataArrayList, DataType.Sensor type){
        this.context = context;
        this.sensorDataArrayList = sensorDataArrayList;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.data_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SensorData sensorData = sensorDataArrayList.get(position);
            String data = null;
            if(type == DataType.Sensor.RUNNING){
                data = sensorData.getVibration();
            } else if(type == DataType.Sensor.DROWNING){
                data = sensorData.getWaterSens();
            } else if(type == DataType.Sensor.LOUD_VOICES){
                data = String.valueOf(sensorData.getSoundDetector());
            }
            holder.textViewData.setText(data);
            holder.textViewTime.setText(sensorData.getTime());
    }

    @Override
    public int getItemCount() {
        return sensorDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewData, textViewTime;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewData = itemView.findViewById(R.id.data);
            textViewTime = itemView.findViewById(R.id.time);
        }
    }
}
