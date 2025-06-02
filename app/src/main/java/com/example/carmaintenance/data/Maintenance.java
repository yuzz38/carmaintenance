package com.example.carmaintenance.data;

import java.io.Serializable;
import java.util.Date;

public class Maintenance implements Serializable {
    private Date date;
    private int mileage;
    private boolean oilChanged;
    private boolean transmissionOilChanged;
    private boolean brakePadsChanged;
    private String notes;

    public Maintenance() {
    }

    // Геттеры и сеттеры
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public boolean isOilChanged() {
        return oilChanged;
    }

    public void setOilChanged(boolean oilChanged) {
        this.oilChanged = oilChanged;
    }

    public boolean isTransmissionOilChanged() {
        return transmissionOilChanged;
    }

    public void setTransmissionOilChanged(boolean transmissionOilChanged) {
        this.transmissionOilChanged = transmissionOilChanged;
    }

    public boolean isBrakePadsChanged() {
        return brakePadsChanged;
    }

    public void setBrakePadsChanged(boolean brakePadsChanged) {
        this.brakePadsChanged = brakePadsChanged;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}