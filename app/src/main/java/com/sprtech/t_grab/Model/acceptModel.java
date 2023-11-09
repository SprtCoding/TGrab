package com.sprtech.t_grab.Model;

public class acceptModel {
    String message, driverNAME, driverUID;

    public acceptModel() {
    }

    public acceptModel(String message, String driverNAME, String driverUID) {
        this.message = message;
        this.driverNAME = driverNAME;
        this.driverUID = driverUID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDriverNAME() {
        return driverNAME;
    }

    public void setDriverNAME(String driverNAME) {
        this.driverNAME = driverNAME;
    }

    public String getDriverUID() {
        return driverUID;
    }

    public void setDriverUID(String driverUID) {
        this.driverUID = driverUID;
    }
}
