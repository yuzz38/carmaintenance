// MainActivity.java
package com.example.carmaintenance;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
            // Проверяем, есть ли данные об автомобиле
            boolean hasCarData = car != null && car.getName() != null && !car.getName().isEmpty();

            if (itemId == R.id.navigation_home) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment.newInstance(car))
                        .commit();
                return true;
            }
            else if (itemId == R.id.navigation_specs) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, SpecsFragment.newInstance(car))
                        .commit();
                return true;
            }
            else if (itemId == R.id.navigation_maintenance) {
                if (hasCarData) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, MaintenanceFragment.newInstance(car))
                            .commit();
                    return true;
                } else {
                    Toast.makeText(this, "Сначала добавьте автомобиль", Toast.LENGTH_SHORT).show();
                    navView.setSelectedItemId(R.id.navigation_home); // Возвращаем на главный экран
                    return false;
                }
            }

            return false;
        });

        if (savedInstanceState == null) {
            navView.setSelectedItemId(R.id.navigation_home);
        }
    }
    public void redirectToHome() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_home);
        Toast.makeText(this, "Сначала добавьте автомобиль", Toast.LENGTH_SHORT).show();
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