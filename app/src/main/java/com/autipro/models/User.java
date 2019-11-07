package com.autipro.models;

public class User {
    private String username = "", password = "", email = "", token = "";
    private String age ;
    private String loudVoice, running, drowning, tantrum;

    public User(){
    }

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String email, String token, String age,
                String loudVoice, String running, String drowning, String tantrum){
        this.username = username;
        this.password = password;
        this.email = email;
        this.token = token;
        this.age = age;
        this.loudVoice = loudVoice;
        this.running = running;
        this.drowning = drowning;
        this.tantrum = tantrum;
    }

    public User(String username, String password, String email, String token, String age) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.token = token;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getAge() {
        return age;
    }

    public String getLoudVoice() {
        return loudVoice;
    }

    public String getRunning() {
        return running;
    }

    public String getDrowning() {
        return drowning;
    }

    public String getTantrum() {
        return tantrum;
    }
}

