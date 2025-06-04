package com.example.carmaintenance.domain;

import com.example.carmaintenance.data.Car;

public class CalculateMaintenanceIntervalsUseCase {

    public int getOilChangeInterval(Car car) {
        return car.getCustomOilChangeInterval() > 0 ? car.getCustomOilChangeInterval() :
                "дизельный".equals(car.getEngineType()) ? 15000 : 5000;
    }

    public int getTransmissionOilChangeInterval(Car car) {
        if (car.getCustomTransmissionOilChangeInterval() > 0) {
            return car.getCustomTransmissionOilChangeInterval();
        }
        if (car.getTransmissionType() == null) return 50000;
        switch (car.getTransmissionType()) {
            case "автомат": return 60000;
            case "вариатор": return 50000;
            case "робот": return 40000;
            case "механика": return 70000;
            default: return 50000;
        }
    }

    public int getBrakePadsChangeInterval(Car car) {
        return car.getCustomBrakePadsChangeInterval() > 0 ? car.getCustomBrakePadsChangeInterval() : 30000;
    }

    // Добавим методы для получения текущих пользовательских интервалов
    public int getCurrentCustomOilInterval(Car car) {
        return car.getCustomOilChangeInterval() > 0 ?
                car.getCustomOilChangeInterval() :
                getOilChangeInterval(car);
    }

    public int getCurrentCustomTransmissionOilInterval(Car car) {
        return car.getCustomTransmissionOilChangeInterval() > 0 ?
                car.getCustomTransmissionOilChangeInterval() :
                getTransmissionOilChangeInterval(car);
    }

    public int getCurrentCustomBrakePadsInterval(Car car) {
        return car.getCustomBrakePadsChangeInterval() > 0 ?
                car.getCustomBrakePadsChangeInterval() :
                getBrakePadsChangeInterval(car);
    }
}