package com.autipro.models;

public class User {
    private String username, password, email, token;
    private int age;

    public User(){

    }

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String email, String token, int age) {
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

    public int getAge() {
        return age;
    }
}
