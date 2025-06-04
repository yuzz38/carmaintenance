package com.example.carmaintenance.domain;

import com.example.carmaintenance.data.Car;

import java.util.concurrent.TimeUnit;

public class CalculateNextMaintenanceUseCase {
    private final CalculateMaintenanceIntervalsUseCase intervalsUseCase;

    public CalculateNextMaintenanceUseCase() {
        this.intervalsUseCase = new CalculateMaintenanceIntervalsUseCase();
    }

    public void updateDailyData(Car car) {
        long now = System.currentTimeMillis();
        long daysPassed = TimeUnit.MILLISECONDS.toDays(now - car.getLastUpdateDate());

        if (daysPassed > 0) {
            car.setCurrentMileage(car.getCurrentMileage() + (int)(car.getDailyMileage() * daysPassed));
            car.setLastUpdateDate(now);
        }
    }

    public int getOilKmLeft(Car car) {
        updateDailyData(car);
        int interval = intervalsUseCase.getOilChangeInterval(car);
        return Math.max(0, interval - (car.getCurrentMileage() - car.getLastOilChangeKm()));
    }

    public int getTransmissionOilKmLeft(Car car) {
        updateDailyData(car);
        int interval = intervalsUseCase.getTransmissionOilChangeInterval(car);
        return Math.max(0, interval - (car.getCurrentMileage() - car.getLastTransmissionOilChangeKm()));
    }

    public int getBrakePadsKmLeft(Car car) {
        updateDailyData(car);
        return Math.max(0, intervalsUseCase.getBrakePadsChangeInterval(car) - (car.getCurrentMileage() - car.getLastBrakePadsChangeKm()));
    }

    public int getDaysUntilOilChange(Car car) {
        int kmLeft = getOilKmLeft(car);
        if (car.getDailyMileage() <= 0) return Integer.MAX_VALUE;
        return (int) Math.ceil(kmLeft / car.getDailyMileage());
    }

    public int getDaysUntilTransmissionOilChange(Car car) {
        int kmLeft = getTransmissionOilKmLeft(car);
        if (car.getDailyMileage() <= 0) return Integer.MAX_VALUE;
        return (int) Math.ceil(kmLeft / car.getDailyMileage());
    }

    public int getDaysUntilBrakePadsChange(Car car) {
        int kmLeft = getBrakePadsKmLeft(car);
        if (car.getDailyMileage() <= 0) return Integer.MAX_VALUE;
        return (int) Math.ceil(kmLeft / car.getDailyMileage());
    }

    public MaintenanceTask getNextMaintenance(Car car) {
        updateDailyData(car);

        int oilDays = getDaysUntilOilChange(car);
        int transOilDays = getDaysUntilTransmissionOilChange(car);
        int brakeDays = getDaysUntilBrakePadsChange(car);

        // Проверяем просроченные ТО
        if (getOilKmLeft(car) <= 0) {
            return new MaintenanceTask("СРОЧНО: Замена масла ДВС", 0, 0);
        }
        if (getTransmissionOilKmLeft(car) <= 0) {
            return new MaintenanceTask("СРОЧНО: Замена масла КПП", 0, 0);
        }
        if (getBrakePadsKmLeft(car) <= 0) {
            return new MaintenanceTask("СРОЧНО: Замена колодок", 0, 0);
        }

        // Определяем ближайшее ТО
        int minDays = Math.min(oilDays, Math.min(transOilDays, brakeDays));
        if (minDays == oilDays) {
            return new MaintenanceTask("Замена масла ДВС", oilDays, getOilKmLeft(car));
        } else if (minDays == transOilDays) {
            return new MaintenanceTask("Замена масла КПП", transOilDays, getTransmissionOilKmLeft(car));
        } else {
            return new MaintenanceTask("Замена колодок", brakeDays, getBrakePadsKmLeft(car));
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