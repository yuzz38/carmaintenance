package com.example.carmaintenance.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Car implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String licensePlate;
    private int year;
    private String engineType;
    private String driveType;
    private String transmissionType;
    private double dailyMileage;
    private String imagePath;
    private int customOilChangeInterval = -1;
    private int customTransmissionOilChangeInterval = -1;
    private int customBrakePadsChangeInterval = -1;
    private int currentMileage;
    private long lastUpdateDate;
    private int lastOilChangeKm;
    private int lastTransmissionOilChangeKm;
    private int lastBrakePadsChangeKm;
    private long lastOilChangeDate;
    private long lastTransmissionOilChangeDate;
    private long lastBrakePadsChangeDate;
    private List<Maintenance> maintenanceHistory;
    private String imageUriString;

    public Car() {
        maintenanceHistory = new ArrayList<>();
        lastUpdateDate = System.currentTimeMillis();
    }

    // Геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getEngineType() { return engineType; }
    public void setEngineType(String engineType) { this.engineType = engineType; }
    public String getDriveType() { return driveType; }
    public void setDriveType(String driveType) { this.driveType = driveType; }
    public String getTransmissionType() { return transmissionType; }
    public void setTransmissionType(String transmissionType) { this.transmissionType = transmissionType; }
    public double getDailyMileage() { return dailyMileage; }
    public void setDailyMileage(double dailyMileage) { this.dailyMileage = dailyMileage; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public int getCurrentMileage() { return currentMileage; }
    public void setCurrentMileage(int currentMileage) { this.currentMileage = currentMileage; }
    public long getLastUpdateDate() { return lastUpdateDate; }
    public void setLastUpdateDate(long lastUpdateDate) { this.lastUpdateDate = lastUpdateDate; }
    public int getLastOilChangeKm() { return lastOilChangeKm; }
    public void setLastOilChangeKm(int lastOilChangeKm) { this.lastOilChangeKm = lastOilChangeKm; }
    public int getLastTransmissionOilChangeKm() { return lastTransmissionOilChangeKm; }
    public void setLastTransmissionOilChangeKm(int lastTransmissionOilChangeKm) { this.lastTransmissionOilChangeKm = lastTransmissionOilChangeKm; }
    public int getLastBrakePadsChangeKm() { return lastBrakePadsChangeKm; }
    public void setLastBrakePadsChangeKm(int lastBrakePadsChangeKm) { this.lastBrakePadsChangeKm = lastBrakePadsChangeKm; }
    public long getLastOilChangeDate() { return lastOilChangeDate; }
    public void setLastOilChangeDate(long lastOilChangeDate) { this.lastOilChangeDate = lastOilChangeDate; }
    public long getLastTransmissionOilChangeDate() { return lastTransmissionOilChangeDate; }
    public void setLastTransmissionOilChangeDate(long lastTransmissionOilChangeDate) { this.lastTransmissionOilChangeDate = lastTransmissionOilChangeDate; }
    public long getLastBrakePadsChangeDate() { return lastBrakePadsChangeDate; }
    public void setLastBrakePadsChangeDate(long lastBrakePadsChangeDate) { this.lastBrakePadsChangeDate = lastBrakePadsChangeDate; }
    public List<Maintenance> getMaintenanceHistory() { return maintenanceHistory; }
    public void setMaintenanceHistory(List<Maintenance> maintenanceHistory) { this.maintenanceHistory = maintenanceHistory; }
    public int getCustomOilChangeInterval() { return customOilChangeInterval; }
    public void setCustomOilChangeInterval(int customOilChangeInterval) { this.customOilChangeInterval = customOilChangeInterval; }
    public int getCustomTransmissionOilChangeInterval() { return customTransmissionOilChangeInterval; }
    public void setCustomTransmissionOilChangeInterval(int customTransmissionOilChangeInterval) { this.customTransmissionOilChangeInterval = customTransmissionOilChangeInterval; }
    public int getCustomBrakePadsChangeInterval() { return customBrakePadsChangeInterval; }
    public void setCustomBrakePadsChangeInterval(int customBrakePadsChangeInterval) { this.customBrakePadsChangeInterval = customBrakePadsChangeInterval; }
    public String getImageUriString() { return imageUriString; }
    public void setImageUriString(String imageUriString) { this.imageUriString = imageUriString; }
}