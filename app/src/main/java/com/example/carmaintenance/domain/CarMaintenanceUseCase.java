package com.example.carmaintenance.domain;

import com.example.carmaintenance.data.Car;
import com.example.carmaintenance.data.Maintenance;

public class CarMaintenanceUseCase {
    public void addMaintenance(Car car, Maintenance maintenance) {
        car.getMaintenanceHistory().add(maintenance);
        car.setCurrentMileage(maintenance.getMileage());
        car.setLastUpdateDate(System.currentTimeMillis());

        if (maintenance.isOilChanged()) {
            car.setLastOilChangeKm(maintenance.getMileage());
            car.setLastOilChangeDate(maintenance.getDate().getTime());
        }
        if (maintenance.isTransmissionOilChanged()) {
            car.setLastTransmissionOilChangeKm(maintenance.getMileage());
            car.setLastTransmissionOilChangeDate(maintenance.getDate().getTime());
        }
        if (maintenance.isBrakePadsChanged()) {
            car.setLastBrakePadsChangeKm(maintenance.getMileage());
            car.setLastBrakePadsChangeDate(maintenance.getDate().getTime());
        }
    }
}