package com.example.carmaintenance.presentation.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.carmaintenance.R;
import com.example.carmaintenance.data.Car;
import com.example.carmaintenance.domain.CalculateNextMaintenanceUseCase;
import com.example.carmaintenance.presentation.activities.EditSpecsActivity;
import com.example.carmaintenance.presentation.activities.MainActivity;

public class HomeFragment extends Fragment {
    private static final String ARG_CAR = "car";
    private Car car;
    private ActivityResultLauncher<Intent> editSpecsLauncher;

    public static HomeFragment newInstance(Car car) {
        HomeFragment fragment = new HomeFragment();
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
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Car updatedCar = (Car) result.getData().getSerializableExtra("car");
                        if (updatedCar != null) {
                            ((MainActivity) requireActivity()).saveCarData(updatedCar);
                            car = updatedCar;
                            // Принудительно обновляем UI
                            updateUI();
                        }
                    }
                });

    }
    private void updateUI() {
        View root = getView();
        if (root == null) return;

        TextView carName = root.findViewById(R.id.car_name);
        TextView licensePlate = root.findViewById(R.id.license_plate);

        RelativeLayout license_bg = root.findViewById(R.id.license_bg);
        TextView check_fines_link = root.findViewById(R.id.check_fines_link);
        ImageView carImage = root.findViewById(R.id.car_image);
        TextView nextMaintenance = root.findViewById(R.id.next_maintenance);
        Button addCarButton = root.findViewById(R.id.add_car_button);

        if (car == null || car.getName() == null || car.getName().isEmpty()) {
            // Показываем кнопку добавления
            addCarButton.setVisibility(View.VISIBLE);
            carName.setVisibility(View.GONE);
            check_fines_link.setVisibility(View.GONE);
            licensePlate.setVisibility(View.GONE);
            license_bg.setVisibility(View.GONE);
            carImage.setVisibility(View.GONE);
            nextMaintenance.setVisibility(View.GONE);
        } else {
            // Показываем данные авто
            addCarButton.setVisibility(View.GONE);
            carName.setVisibility(View.VISIBLE);
            check_fines_link.setVisibility(View.VISIBLE);
            license_bg.setVisibility(View.VISIBLE);
            licensePlate.setVisibility(View.VISIBLE);
            carImage.setVisibility(View.VISIBLE);
            nextMaintenance.setVisibility(View.VISIBLE);

            carName.setText(car.getName());
            licensePlate.setText(car.getLicensePlate());

            if (car.getImageUriString() != null && !car.getImageUriString().isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(car.getImageUriString());
                    getActivity().getContentResolver().takePersistableUriPermission(
                            imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    carImage.setImageURI(imageUri);
                } catch (Exception e) {
                    carImage.setImageResource(R.drawable.default_car);
                }
            } else {
                carImage.setImageResource(R.drawable.default_car);
            }

            // Обновляем информацию о ТО
            CalculateNextMaintenanceUseCase nextMaintenanceUseCase = new CalculateNextMaintenanceUseCase();
            CalculateNextMaintenanceUseCase.MaintenanceTask nextTask = nextMaintenanceUseCase.getNextMaintenance(car);

            if (nextTask.daysLeft == 0) {
                nextMaintenance.setTextColor(Color.RED);
                nextMaintenance.setText(nextTask.taskName);
            } else {
                nextMaintenance.setTextColor(Color.WHITE);
                nextMaintenance.setText(String.format("%s: %d км / %d дней",
                        nextTask.taskName, nextTask.kmLeft, nextTask.daysLeft));
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        TextView carName = root.findViewById(R.id.car_name);
        TextView licensePlate = root.findViewById(R.id.license_plate);
        ImageView carImage = root.findViewById(R.id.car_image);
        RelativeLayout license_bg = root.findViewById(R.id.license_bg);
        TextView check_fines_link = root.findViewById(R.id.check_fines_link);
        TextView nextMaintenance = root.findViewById(R.id.next_maintenance);
        Button addCarButton = root.findViewById(R.id.add_car_button);

        if (car == null || car.getName() == null || car.getName().isEmpty()) {
            addCarButton.setVisibility(View.VISIBLE);
            carName.setVisibility(View.GONE);
            check_fines_link.setVisibility(View.GONE);
            license_bg.setVisibility(View.GONE);
            licensePlate.setVisibility(View.GONE);
            carImage.setVisibility(View.GONE);
            nextMaintenance.setVisibility(View.GONE);

            addCarButton.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), EditSpecsActivity.class);
                intent.putExtra("car", new Car());
                editSpecsLauncher.launch(intent);
            });
        } else {
            addCarButton.setVisibility(View.GONE);
            carName.setVisibility(View.VISIBLE);
            licensePlate.setVisibility(View.VISIBLE);
            carImage.setVisibility(View.VISIBLE);
            nextMaintenance.setVisibility(View.VISIBLE);
            check_fines_link.setVisibility(View.VISIBLE);
            license_bg.setVisibility(View.VISIBLE);
            carName.setText(car.getName());
            licensePlate.setText(car.getLicensePlate());
            check_fines_link.setOnClickListener(v -> {
                // Открываем сайт в браузере
                String url = "https://rosfines.ru/sts-grz/";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            });
            if (car.getImageUriString() != null && !car.getImageUriString().isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(car.getImageUriString());
                    getActivity().getContentResolver().takePersistableUriPermission(
                            imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    carImage.setImageURI(imageUri);
                } catch (Exception e) {
                    Log.e("HomeFragment", "Ошибка загрузки изображения", e);
                    carImage.setImageResource(R.drawable.default_car);
                }
            } else {
                carImage.setImageResource(R.drawable.default_car);
            }

            CalculateNextMaintenanceUseCase nextMaintenanceUseCase = new CalculateNextMaintenanceUseCase();
            CalculateNextMaintenanceUseCase.MaintenanceTask nextTask = nextMaintenanceUseCase.getNextMaintenance(car);

            if (nextTask.daysLeft == 0) {
                nextMaintenance.setTextColor(Color.RED);
                nextMaintenance.setText(nextTask.taskName);
            } else {
                nextMaintenance.setTextColor(Color.WHITE);
                nextMaintenance.setText(String.format("%s: %d км / %d дней",
                        nextTask.taskName, nextTask.kmLeft, nextTask.daysLeft));
            }
        }

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Car updatedCar = (Car) data.getSerializableExtra("car");
            if (updatedCar != null) {
                ((MainActivity) requireActivity()).saveCarData(updatedCar);
                // Обновляем данные
                car = updatedCar;
                // Пересоздаем фрагмент для обновления UI
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        car = ((MainActivity) getActivity()).getCar();
        new CalculateNextMaintenanceUseCase().updateDailyData(car);
        if (getView() != null) {
            onCreateView(getLayoutInflater(), (ViewGroup) getView(), null);
        }
    }
}