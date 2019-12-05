package com.autipro.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class SensorData implements Serializable {
    private DHT11 dht11;
    private String HeartSens1, HeartSens2, MuscleSens, SoundDetector;
    private String Time, Vibration, WaterSens;

    public SensorData(){
    }

    public SensorData(DHT11 dht11, String heartSens1, String heartSens2, String muscleSens,
                      String soundDetector, String time, String vibration, String waterSens) {
        this.dht11 = dht11;
        HeartSens1 = heartSens1;
        HeartSens2 = heartSens2;
        MuscleSens = muscleSens;
        SoundDetector = soundDetector;
        Time = time;
        Vibration = vibration;
        WaterSens = waterSens;
    }

    public DHT11 getDht11() {
        return dht11;
    }

    public String getHeartSens1() {
        return HeartSens1;
    }

    public String getHeartSens2() {
        return HeartSens2;
    }

    public String getMuscleSens() {
        return MuscleSens;
    }

    public String getSoundDetector() {
        return SoundDetector;
    }

    public String getTime() {
        return Time;
    }

    public String getVibration() {
        return Vibration;
    }

    public String getWaterSens() {
        return WaterSens;
    }

}
