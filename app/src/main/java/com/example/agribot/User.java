package com.example.agribot;

public class User {
    private String password;
    private String imei;
    private String owner;
    private String chipset;
    private String date;
    private String topic;

    public User() {
    }

    public User(String password, String imei, String owner, String chipset, String date, String topic) {
        this.password = password;
        this.chipset = chipset;
        this.imei = imei;
        this.owner = owner;
        this.date = date;
        this.topic = topic;
    }

    public String getPassword() {
        return password;
    }

    public String getChipset() {
        return chipset;
    }

    public String getImei() {
        return imei;
    }

    public String getOwner() {
        return owner;
    }

    public String getTopic() {
        return topic;
    }

    public String getDate() {
        return date;
    }
}
