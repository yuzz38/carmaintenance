package com.example.carmaintenance.presentation.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carmaintenance.R;
import com.example.carmaintenance.data.Car;
import com.example.carmaintenance.data.Maintenance;
import com.example.carmaintenance.domain.CalculateNextMaintenanceUseCase;
import com.example.carmaintenance.domain.CarMaintenanceUseCase;
import com.example.carmaintenance.presentation.activities.AddMaintenanceActivity;
import com.example.carmaintenance.presentation.activities.EditIntervalsActivity;
import com.example.carmaintenance.presentation.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MaintenanceFragment extends Fragment {
    private static final String ARG_CAR = "car";
    private Car car;
    private MaintenanceAdapter adapter;
    private ActivityResultLauncher<Intent> addMaintenanceLauncher;

    // Views
    private TextView nextMaintenanceView;

    private TextView transmissionOilView;
    private TextView brakePadsView;
    private Button editIntervalsButton;
    public static MaintenanceFragment newInstance(Car car) {
        MaintenanceFragment fragment = new MaintenanceFragment();
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

        initActivityResultLauncher();
    }

    private void initActivityResultLauncher() {
        addMaintenanceLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        handleMaintenanceResult(result.getData());
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (car == null || car.getName() == null || car.getName().isEmpty()) {
            // Перенаправляем на главный экран
            ((MainActivity) requireActivity()).redirectToHome();
            return new View(requireContext()); // Возвращаем пустое View
        }
        View root = inflater.inflate(R.layout.fragment_maintenance, container, false);
        editIntervalsButton = root.findViewById(R.id.edit_intervals_button);
        editIntervalsButton.setOnClickListener(v -> openEditIntervalsActivity());
        initViews(root);
        setupRecyclerView(root);
        setupButtonListeners(root);
        updateMaintenanceInfo();

        return root;
    }
    private void openEditIntervalsActivity() {
        Intent intent = new Intent(getActivity(), EditIntervalsActivity.class);
        intent.putExtra("car", car);
        addMaintenanceLauncher.launch(intent);
    }
    private void handleMaintenanceResult(Intent data) {
        if (data == null) return;

        if (data.hasExtra("maintenance")) {
            Maintenance maintenance = (Maintenance) data.getSerializableExtra("maintenance");
            if (maintenance != null) {
                new CarMaintenanceUseCase().addMaintenance(car, maintenance);
            }
        } else if (data.hasExtra("car")) {
            car = (Car) data.getSerializableExtra("car");
        }

        ((MainActivity) requireActivity()).saveCarData(car);
        updateMaintenanceInfo();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }



    private void initViews(View root) {
        nextMaintenanceView = root.findViewById(R.id.next_maintenance);

        transmissionOilView = root.findViewById(R.id.transmission_oil);
        brakePadsView = root.findViewById(R.id.brake_pads);
    }


    private void setupRecyclerView(View root) {
        RecyclerView recyclerView = root.findViewById(R.id.maintenance_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Сортируем список ТО по дате (новые сверху)
        List<Maintenance> sortedList = car.getMaintenanceHistory();
        Collections.sort(sortedList, (m1, m2) -> m2.getDate().compareTo(m1.getDate()));

        adapter = new MaintenanceAdapter(sortedList);
        recyclerView.setAdapter(adapter);
    }

    private void setupButtonListeners(View root) {
        Button addMaintenanceButton = root.findViewById(R.id.add_maintenance_button);
        addMaintenanceButton.setOnClickListener(v -> {
            if (car.getCurrentMileage() > 0) {
                openAddMaintenanceActivity();
            } else {
                redirectToSpecsFragment();
            }
        });

        // Добавляем кнопку очистки списка
        Button clearHistoryButton = root.findViewById(R.id.clear_history_button);
        clearHistoryButton.setOnClickListener(v -> clearMaintenanceHistory());
    }
    private void clearMaintenanceHistory() {
        car.getMaintenanceHistory().clear();
        // Сбрасываем последние даты и пробеги ТО
        car.setLastOilChangeKm(0);
        car.setLastTransmissionOilChangeKm(0);
        car.setLastBrakePadsChangeKm(0);
        car.setLastOilChangeDate(0);
        car.setLastTransmissionOilChangeDate(0);
        car.setLastBrakePadsChangeDate(0);

        ((MainActivity) requireActivity()).saveCarData(car);
        adapter.notifyDataSetChanged();
        updateMaintenanceInfo();
    }





    private void updateMaintenanceInfo() {
        CalculateNextMaintenanceUseCase nextMaintenanceUseCase = new CalculateNextMaintenanceUseCase();
        CalculateNextMaintenanceUseCase.MaintenanceTask nextTask = nextMaintenanceUseCase.getNextMaintenance(car);

        if (nextTask.daysLeft == 0) {
            nextMaintenanceView.setTextColor(Color.RED);
            nextMaintenanceView.setText(nextTask.taskName);
        } else {
            nextMaintenanceView.setTextColor(Color.WHITE);
            nextMaintenanceView.setText(String.format("%s через %d дней (%d км)",
                    nextTask.taskName, nextTask.daysLeft, nextTask.kmLeft));
        }

        updateServiceInfo(transmissionOilView, "Масло в коробке",
                nextMaintenanceUseCase.getTransmissionOilKmLeft(car),
                nextMaintenanceUseCase.getDaysUntilTransmissionOilChange(car));

        updateServiceInfo(brakePadsView, "Тормозные колодки",
                nextMaintenanceUseCase.getBrakePadsKmLeft(car),
                nextMaintenanceUseCase.getDaysUntilBrakePadsChange(car));
    }

    private void updateServiceInfo(TextView textView, String serviceName,
                                   int kmLeft, int daysLeft) {
        String status = kmLeft <= 0 ? "ПОРА МЕНЯТЬ!" : "осталось";
        textView.setText(String.format("%s: %s %d км (%d дней)",
                serviceName, status, Math.abs(kmLeft), daysLeft));

        textView.setTextColor(kmLeft <= 0 ? Color.RED : Color.WHITE);
    }

    private void openAddMaintenanceActivity() {
        Intent intent = new Intent(getActivity(), AddMaintenanceActivity.class);
        intent.putExtra("currentKm", car.getCurrentMileage());
        addMaintenanceLauncher.launch(intent);
    }

    private void redirectToSpecsFragment() {
        ((MainActivity) requireActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, SpecsFragment.newInstance(car))
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            car = ((MainActivity) getActivity()).getCar();
            new CalculateNextMaintenanceUseCase().updateDailyData(car);
            updateMaintenanceInfo();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private static class MaintenanceAdapter extends RecyclerView.Adapter<MaintenanceAdapter.ViewHolder> {
        private final List<Maintenance> maintenanceList;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        MaintenanceAdapter(List<Maintenance> maintenanceList) {
            this.maintenanceList = maintenanceList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_maintenance, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Maintenance maintenance = maintenanceList.get(position);
            holder.date.setText(dateFormat.format(maintenance.getDate()));
            holder.mileage.setText(String.format("%d км", maintenance.getMileage()));

            StringBuilder services = new StringBuilder();
            if (maintenance.isOilChanged()) services.append("Масло двигателя, ");
            if (maintenance.isTransmissionOilChanged()) services.append("Масло коробки, ");
            if (maintenance.isBrakePadsChanged()) services.append("Тормозные колодки, ");

            if (services.length() > 0) {
                services.setLength(services.length() - 2);
                holder.services.setText(services.toString());
            } else {
                holder.services.setText("Работы не указаны");
            }

            if (maintenance.getNotes() != null && !maintenance.getNotes().isEmpty()) {
                holder.notes.setText(maintenance.getNotes());
                holder.notes.setVisibility(View.VISIBLE);
            } else {
                holder.notes.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return maintenanceList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView date;
            final TextView mileage;
            final TextView services;
            final TextView notes;

            ViewHolder(View view) {
                super(view);
                date = view.findViewById(R.id.date);
                mileage = view.findViewById(R.id.mileage);
                services = view.findViewById(R.id.services);
                notes = view.findViewById(R.id.notes);
            }
        }
    }
}