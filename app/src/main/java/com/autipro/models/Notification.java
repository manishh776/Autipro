package com.autipro.models;

public class Notification {
  private String id, title, body, time;
  private boolean read = false;

    public Notification(){
    }

    public Notification(String id, String title, String body, String time, boolean read) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.time = time;
        this.read = read;
    }


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getTime() {
        return time;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read){
        this.read = read;
    }
}
