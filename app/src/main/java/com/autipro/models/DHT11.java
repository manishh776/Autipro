package com.autipro.models;

public class DHT11 {
    String Hum, Temp;

    public DHT11(){

    }

    public DHT11(String hum, String temp) {
        Hum = hum;
        Temp = temp;
    }

    public void setHum(String hum) {
        Hum = hum;
    }

    public void setTemp(String temp) {
        Temp = temp;
    }

    public String getHum() {
        return Hum;
    }

    public String getTemp() {
        return Temp;
    }
}

