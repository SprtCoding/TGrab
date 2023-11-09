package com.sprtech.t_grab.Model;

public class BookModel {
    String name, destination, phone, passenger_uid, date;

    public BookModel() {
    }

    public BookModel(String name, String destination, String phone, String passenger_uid, String date) {
        this.name = name;
        this.destination = destination;
        this.phone = phone;
        this.passenger_uid = passenger_uid;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassenger_uid() {
        return passenger_uid;
    }

    public void setPassenger_uid(String passenger_uid) {
        this.passenger_uid = passenger_uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
