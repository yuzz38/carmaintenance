package com.example.carmaintenance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Car implements Serializable {
    private static final long serialVersionUID = 1L;

    // Основные данные авто
    private String name;
    private String licensePlate;
    private int year;
    private String engineType; // бензиновый, дизельный
    private String driveType; // передний, задний, полный
    private String transmissionType; // автомат, механика, вариатор, робот
    private double dailyMileage; // средний пробег в день (км)
    private String imagePath;

    // Текущий пробег и дата последнего обновления
    private int currentMileage;
    private long lastUpdateDate;

    // Данные о последних ТО
    private int lastOilChangeKm;
    private int lastTransmissionOilChangeKm;
    private int lastBrakePadsChangeKm;
    private long lastOilChangeDate;
    private long lastTransmissionOilChangeDate;
    private long lastBrakePadsChangeDate;

    private List<Maintenance> maintenanceHistory;
    private String imageUriString; // Будем хранить строковое представление URI

    public String getImageUriString() {
        return imageUriString;
    }

    public void setImageUriString(String imageUriString) {
        this.imageUriString = imageUriString;
    }
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

    // Методы расчета интервалов
    public int getOilChangeInterval() {
        return "дизельный".equals(engineType) ? 15000 : 5000;
    }

    public int getTransmissionOilChangeInterval() {
        if (transmissionType == null) return 50000;
        switch (transmissionType) {
            case "автомат": return 60000;
            case "вариатор": return 50000;
            case "робот": return 40000;
            case "механика": return 70000;
            default: return 50000;
        }
    }

    public int getBrakePadsChangeInterval() {
        return 30000;
    }

    // Обновление данных с учетом прошедшего времени
    public void updateDailyData() {
        long now = System.currentTimeMillis();
        long daysPassed = TimeUnit.MILLISECONDS.toDays(now - lastUpdateDate);

        if (daysPassed > 0) {
            currentMileage += (int)(dailyMileage * daysPassed);
            lastUpdateDate = now;
        }
    }

    // Расчет оставшихся километров
    public int getOilKmLeft() {
        updateDailyData();
        int interval = getOilChangeInterval();
        return Math.max(0, interval - (currentMileage - lastOilChangeKm));
    }

    public int getTransmissionOilKmLeft() {
        updateDailyData();
        int interval = getTransmissionOilChangeInterval();
        return Math.max(0, interval - (currentMileage - lastTransmissionOilChangeKm));
    }

    public int getBrakePadsKmLeft() {
        updateDailyData();
        return Math.max(0, getBrakePadsChangeInterval() - (currentMileage - lastBrakePadsChangeKm));
    }

    // Расчет оставшихся дней
    public int getDaysUntilOilChange() {
        int kmLeft = getOilKmLeft();
        if (dailyMileage <= 0) return Integer.MAX_VALUE;
        return (int) Math.ceil(kmLeft / dailyMileage);
    }

    public int getDaysUntilTransmissionOilChange() {
        int kmLeft = getTransmissionOilKmLeft();
        if (dailyMileage <= 0) return Integer.MAX_VALUE;
        return (int) Math.ceil(kmLeft / dailyMileage);
    }

    public int getDaysUntilBrakePadsChange() {
        int kmLeft = getBrakePadsKmLeft();
        if (dailyMileage <= 0) return Integer.MAX_VALUE;
        return (int) Math.ceil(kmLeft / dailyMileage);
    }

    // Получение информации о следующем ТО
    public MaintenanceTask getNextMaintenance() {
        updateDailyData();

        int oilDays = getDaysUntilOilChange();
        int transOilDays = getDaysUntilTransmissionOilChange();
        int brakeDays = getDaysUntilBrakePadsChange();

        // Проверяем просроченные ТО
        if (getOilKmLeft() <= 0) {
            return new MaintenanceTask("СРОЧНО: Замена масла", 0, 0);
        }
        if (getTransmissionOilKmLeft() <= 0) {
            return new MaintenanceTask("СРОЧНО: Замена масла КПП", 0, 0);
        }
        if (getBrakePadsKmLeft() <= 0) {
            return new MaintenanceTask("СРОЧНО: Замена колодок", 0, 0);
        }

        // Определяем ближайшее ТО
        int minDays = Math.min(oilDays, Math.min(transOilDays, brakeDays));
        if (minDays == oilDays) {
            return new MaintenanceTask("Замена масла", oilDays, getOilKmLeft());
        } else if (minDays == transOilDays) {
            return new MaintenanceTask("Замена масла КПП", transOilDays, getTransmissionOilKmLeft());
        } else {
            return new MaintenanceTask("Замена колодок", brakeDays, getBrakePadsKmLeft());
        }
    }

    // Добавление записи о ТО
    public void addMaintenance(Maintenance maintenance) {
        maintenanceHistory.add(maintenance);
        currentMileage = maintenance.getMileage();
        lastUpdateDate = System.currentTimeMillis();

        if (maintenance.isOilChanged()) {
            lastOilChangeKm = maintenance.getMileage();
            lastOilChangeDate = maintenance.getDate().getTime();
        }
        if (maintenance.isTransmissionOilChanged()) {
            lastTransmissionOilChangeKm = maintenance.getMileage();
            lastTransmissionOilChangeDate = maintenance.getDate().getTime();
        }
        if (maintenance.isBrakePadsChanged()) {
            lastBrakePadsChangeKm = maintenance.getMileage();
            lastBrakePadsChangeDate = maintenance.getDate().getTime();
        }
    }

    public static class MaintenanceTask {
        public final String taskName;
        public final int daysLeft;
        public final int kmLeft;

        public MaintenanceTask(String taskName, int daysLeft, int kmLeft) {
            this.taskName = taskName;
            this.daysLeft = daysLeft;
            this.kmLeft = kmLeft;
        }
    }
}