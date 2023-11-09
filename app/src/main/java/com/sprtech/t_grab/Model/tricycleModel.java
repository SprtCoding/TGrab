package com.sprtech.t_grab.Model;

public class tricycleModel {
    String name;
    String location;
    Double lat;
    Double lon;
    String who;
    String phone_number;
    String uid;

    public tricycleModel() {
    }

    public tricycleModel(String name, String location, Double lat, Double lon, String who, String phone_number, String uid) {
        this.name = name;
        this.location = location;
        this.lat = lat;
        this.lon = lon;
        this.who = who;
        this.phone_number = phone_number;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
