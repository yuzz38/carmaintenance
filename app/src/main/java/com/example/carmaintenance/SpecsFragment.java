// SpecsFragment.java
package com.example.carmaintenance;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SpecsFragment extends Fragment {
    private static final String ARG_CAR = "car";
    private Car car;
    private ActivityResultLauncher<Intent> editSpecsLauncher;

    public static SpecsFragment newInstance(Car car) {
        SpecsFragment fragment = new SpecsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CAR, car);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            car = (Car) getArguments().getSerializable(ARG_CAR);
        }
        editSpecsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // 1. Получаем обновленные данные из MainActivity
                        car = ((MainActivity) requireActivity()).getCar();

                        updateUI();
                    }
                }
        );
    }
    private void updateUI() {
        if (getView() == null) return;

        TextView carName = getView().findViewById(R.id.car_name);
        TextView year = getView().findViewById(R.id.year);
        TextView engineType = getView().findViewById(R.id.engine_type);
        TextView licensePlate = getView().findViewById(R.id.license_plate);
        TextView driveType = getView().findViewById(R.id.drive_type);
        TextView transmissionType = getView().findViewById(R.id.transmission_type);
        TextView dailyMileage = getView().findViewById(R.id.daily_mileage);
        TextView currentMileage = getView().findViewById(R.id.current_mileage);

        if (car.getName() != null && !car.getName().isEmpty()) {
            carName.setText(car.getName());
            year.setText(String.valueOf(car.getYear()));
            engineType.setText(car.getEngineType());
            licensePlate.setText(car.getLicensePlate());
            driveType.setText(car.getDriveType());
            transmissionType.setText(car.getTransmissionType());
            dailyMileage.setText(String.format("%.1f км/день", car.getDailyMileage()));
            currentMileage.setText(String.format("%d км", car.getCurrentMileage()));
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_specs, container, false);

        TextView carName = root.findViewById(R.id.car_name);
        TextView year = root.findViewById(R.id.year);
        TextView engineType = root.findViewById(R.id.engine_type);
        TextView driveType = root.findViewById(R.id.drive_type);
        TextView transmissionType = root.findViewById(R.id.transmission_type);
        TextView dailyMileage = root.findViewById(R.id.daily_mileage);
        TextView currentMileage = root.findViewById(R.id.current_mileage);
        Button editButton = root.findViewById(R.id.edit_button);

        if (car.getName() != null && !car.getName().isEmpty()) {
            carName.setText(car.getName());
            year.setText(String.valueOf(car.getYear()));
            engineType.setText(car.getEngineType());
            driveType.setText(car.getDriveType());
            transmissionType.setText(car.getTransmissionType());
            dailyMileage.setText(String.format("%.1f км/день", car.getDailyMileage()));
            currentMileage.setText(String.format("%d км", car.getCurrentMileage()));
        }

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditSpecsActivity.class);
            intent.putExtra("car", car);
            startActivityForResult(intent, 1);
        });
        Button deleteButton = root.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
        return root;
    }
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Удаление автомобиля")
                .setMessage("Вы уверены, что хотите удалить данные об автомобиле?")
                .setPositiveButton("Да", (dialog, which) -> deleteCar())
                .setNegativeButton("Нет", null)
                .show();
    }
    private void deleteCar() {
        // Создаем новый пустой автомобиль
        Car emptyCar = new Car();

        // Сохраняем пустой автомобиль
        ((MainActivity) requireActivity()).saveCarData(emptyCar);

        // Обновляем текущий автомобиль
        car = emptyCar;

        // Обновляем UI
        updateUI();

        // Возвращаемся на главный экран
        // Переходим на главный экран
        BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_home);



        Toast.makeText(getContext(), "Автомобиль удален", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onResume() {
        super.onResume();
        // Обновляем данные при каждом возвращении на фрагмент
        car = ((MainActivity) requireActivity()).getCar();
        updateUI();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Car updatedCar = (Car) data.getSerializableExtra("car");
            if (updatedCar != null) {
                ((MainActivity) requireActivity()).saveCarData(updatedCar);

                // Полностью пересоздаем фрагмент
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
            }
        }
    }
}