package com.autipro.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
    private String limit;
    private int age;
    private String TAG = "DataAdapter";

    public DataAdapter(Context context, ArrayList<SensorData> sensorDataArrayList,
                       DataType.Sensor type, String limit, int age){
        this.context = context;
        this.sensorDataArrayList = sensorDataArrayList;
        this.type = type;
        this.limit = limit;
        this.age = age;

        Log.d(TAG, "SIZE" + sensorDataArrayList.size());
        for(SensorData sensorData : sensorDataArrayList){
            Log.d(TAG, "Loop" + sensorData.getHeartSens1() + "HeartRate" + sensorData);
        }
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
            Log.d(TAG, sensorData.getHeartSens1() + "HeartRate" + position);
            String data = null;
            int bg_color = 0;
            if(type == DataType.Sensor.RUNNING){
                data = sensorData.getVibration();
                if(data.equals("Not Runing")){
                    data = "Not Running";
                    bg_color = context.getResources().getColor(R.color.green);
                }else{
                    data = "Running";
                    bg_color = context.getResources().getColor(R.color.red);
                }
            } else if(type == DataType.Sensor.DROWNING){
                data = sensorData.getWaterSens();
                if(data!= null && data.equals("Not Drowning")){
                    data = "Not Drowning";
                    bg_color = context.getResources().getColor(R.color.green);
                }else{
                    data = "Drowning";
                    bg_color = context.getResources().getColor(R.color.red);
                }
            } else if(type == DataType.Sensor.LOUD_VOICES){
                data = sensorData.getSoundDetector();
                bg_color = context.getResources().getColor(R.color.green);
                if(data != null) {
                    double loudness = Double.parseDouble(data);
                    bg_color = context.getResources().getColor(loudness > 90 ? R.color.red : R.color.green);
                }else{
                    data = "";
                }
            }else if(type == DataType.Sensor.TANTRUM){
                if(!sensorData.getHeartSens1().equals("Not Sensing")
                && sensorData.getDht11()!=null
                        && sensorData.getMuscleSens()!=null
                ) {
                    int heartRate = Integer.parseInt(sensorData.getHeartSens1());
                    int temp = Integer.parseInt(sensorData.getDht11().getTemp());
                    int hum = Integer.parseInt(sensorData.getDht11().getHum());
                    int muscleSens = Integer.parseInt(sensorData.getMuscleSens());
                    Log.d(TAG, heartRate + "HEARTRATE" + isHeartRateNormal(age, heartRate) + " " + isTemperatureNormal(age,
                            temp));
                    Log.d(TAG, "AGE" + age);
                    if (!isHeartRateNormal(age, heartRate)
                            && !isTemperatureNormal(age,
                            temp) && hum > 80 && sensorData.getVibration().equals("Not Runing") && muscleSens > 200) {
                        data = "Tantrum Detected  \n" + "HeartRate = " + heartRate + "\n" +
                                "Temperature = " + temp + "\n" + "Humidity = " + hum + "\n" +
                                "Muscle = " + muscleSens;
                        bg_color = context.getResources().getColor(R.color.red);
                        Log.d(TAG, heartRate + "HEARTRATE" + isHeartRateNormal(age, heartRate) + " " + isTemperatureNormal(age,
                                temp));
                    }else{
                        data = "No Tantrum Detected  \n" + "HeartRate = " + heartRate + "\n" +
                                "Temperature = " + temp + "\n" + "Humidity = " + hum + "\n" +
                                "Muscle = " + muscleSens;
                        bg_color = context.getResources().getColor(R.color.green);
                    }
                }else{
                    int temp = sensorData.getDht11()!=null ? Integer.parseInt(sensorData.getDht11().getTemp()) : 0;
                    int hum = sensorData.getDht11()!=null ? Integer.parseInt(sensorData.getDht11().getHum()): 0;
                    int muscleSens = sensorData.getMuscleSens()!=null? Integer.parseInt(sensorData.getMuscleSens()) : 0;
                    data = "No Tantrum Detected \n" + "HeartRate = +" + sensorData.getHeartSens1()+"\n"+
                            "Temperature = "+temp+"\n" + "Humidity = "+hum + "\n" +
                            "Muscle = " + muscleSens;
                    bg_color = context.getResources().getColor(R.color.green);
                }
            }
            holder.textViewData.setText(data);
            holder.container.setBackgroundColor(bg_color);
            holder.textViewTime.setText(sensorData.getTime());
    }


    boolean isHeartRateNormal(int age, int heartRate){
        if(age >= 1 && age <= 3){
            return (heartRate>=75 && heartRate <=130);
        }
        else if(age > 3 && age <=5){
            return (heartRate>=75 && heartRate <=120);
        }
        else if(age>=5 && age < 11){
            return (heartRate>=70 && heartRate <=110);
        }
        else if(age>=11 && age < 13){
            return (heartRate>=70 && heartRate<=110);
        }else if(age>=13 && age < 18){
            return (heartRate>=65 && heartRate<=105);
        }else {
            return (heartRate>=50 && heartRate<=90);
        }
    }

    boolean isTemperatureNormal(int age, int temp){
        if(age>=3 && age<=10){
            return (temp>=35.9 && temp<=36.7);
        }
        else if(age>=11 && age <=65){
            return (temp>=35.2 && temp<=36.9);
        }
        else {
            return (temp>=35.6 && temp<=36.3);
        }
    }

    @Override
    public int getItemCount() {
        return sensorDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewData, textViewTime;
        LinearLayout container;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewData = itemView.findViewById(R.id.data);
            textViewTime = itemView.findViewById(R.id.time);
            container = itemView.findViewById(R.id.container);
        }
    }
}
