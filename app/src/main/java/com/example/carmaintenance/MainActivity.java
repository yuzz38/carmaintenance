// MainActivity.java
package com.example.carmaintenance;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "CarPrefs";
    private static final String CAR_KEY = "car_data";
    private Car car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Загружаем данные автомобиля
        loadCarData();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                selectedFragment = HomeFragment.newInstance(car);
            } else if (itemId == R.id.navigation_specs) {
                selectedFragment = SpecsFragment.newInstance(car);
            } else if (itemId == R.id.navigation_maintenance) {
                selectedFragment = MaintenanceFragment.newInstance(car);
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }

            return false;
        });

        if (savedInstanceState == null) {
            navView.setSelectedItemId(R.id.navigation_home);
        }
    }

    public void saveCarData(Car updatedCar) {
        this.car = updatedCar;
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String carJson = gson.toJson(car);
        editor.putString(CAR_KEY, carJson);
        editor.apply();
    }

    private void loadCarData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String carJson = prefs.getString(CAR_KEY, null);

        if (carJson != null) {

            Gson gson = new Gson();
            car = gson.fromJson(carJson, Car.class);
        } else {

            car = new Car(); // Создаем пустой автомобиль
        }
    }
    public Car getCar() {
        return car;
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем данные автомобиля при каждом открытии приложения
        car.updateDailyData();
        saveCarData(car);
    }

}