package com.example.carmaintenance;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeFragment extends Fragment {
    private static final String ARG_CAR = "car";
    private Car car;

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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        TextView carName = root.findViewById(R.id.car_name);
        TextView licensePlate = root.findViewById(R.id.license_plate);
        ImageView carImage = root.findViewById(R.id.car_image);
        TextView nextMaintenance = root.findViewById(R.id.next_maintenance);
        Button addCarButton = root.findViewById(R.id.add_car_button);

        if (car == null || car.getName() == null || car.getName().isEmpty()) {
            addCarButton.setVisibility(View.VISIBLE);
            carName.setVisibility(View.GONE);
            licensePlate.setVisibility(View.GONE);
            carImage.setVisibility(View.GONE);
            nextMaintenance.setVisibility(View.GONE);

            addCarButton.setOnClickListener(v -> {
                if (car == null) {
                    car = new Car();
                }
                Fragment specsFragment = SpecsFragment.newInstance(car);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, specsFragment)
                        .addToBackStack(null)
                        .commit();
            });
        } else {
            addCarButton.setVisibility(View.GONE);
            carName.setVisibility(View.VISIBLE);
            licensePlate.setVisibility(View.VISIBLE);
            carImage.setVisibility(View.VISIBLE);
            nextMaintenance.setVisibility(View.VISIBLE);

            carName.setText(car.getName());
            licensePlate.setText(car.getLicensePlate());

            // Загрузка изображения автомобиля
            if (car.getImageUriString() != null && !car.getImageUriString().isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(car.getImageUriString());
                    // Проверяем доступность URI
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

            Car.MaintenanceTask nextTask = car.getNextMaintenance();
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
    public void onResume() {
        super.onResume();
        car = ((MainActivity) getActivity()).getCar();
        car.updateDailyData();
        if (getView() != null) {
            onCreateView(getLayoutInflater(), (ViewGroup) getView(), null);
        }
    }
}