package com.example.carmaintenance.presentation.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carmaintenance.R;
import com.example.carmaintenance.data.Car;
import com.example.carmaintenance.domain.CalculateMaintenanceIntervalsUseCase;

public class EditIntervalsActivity extends AppCompatActivity {
    private Car car;
    private EditText oilIntervalInput;
    private EditText transmissionOilIntervalInput;
    private EditText brakePadsIntervalInput;
    private CalculateMaintenanceIntervalsUseCase intervalsUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_intervals);

        car = (Car) getIntent().getSerializableExtra("car");
        if (car == null) {
            finish();
            return;
        }

        intervalsUseCase = new CalculateMaintenanceIntervalsUseCase();

        oilIntervalInput = findViewById(R.id.oil_interval);
        transmissionOilIntervalInput = findViewById(R.id.transmission_oil_interval);
        brakePadsIntervalInput = findViewById(R.id.brake_pads_interval);
        Button saveButton = findViewById(R.id.save_button);

        // Устанавливаем текущие значения через use case
        oilIntervalInput.setText(String.valueOf(intervalsUseCase.getOilChangeInterval(car)));
        transmissionOilIntervalInput.setText(String.valueOf(intervalsUseCase.getTransmissionOilChangeInterval(car)));
        brakePadsIntervalInput.setText(String.valueOf(intervalsUseCase.getBrakePadsChangeInterval(car)));

        saveButton.setOnClickListener(v -> {
            try {
                int oilInterval = Integer.parseInt(oilIntervalInput.getText().toString());
                int transOilInterval = Integer.parseInt(transmissionOilIntervalInput.getText().toString());
                int brakePadsInterval = Integer.parseInt(brakePadsIntervalInput.getText().toString());

                if (oilInterval <= 0 || transOilInterval <= 0 || brakePadsInterval <= 0) {
                    Toast.makeText(this, "Интервалы должны быть больше 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                car.setCustomOilChangeInterval(oilInterval);
                car.setCustomTransmissionOilChangeInterval(transOilInterval);
                car.setCustomBrakePadsChangeInterval(brakePadsInterval);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("car", car);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Пожалуйста, введите корректные числа", Toast.LENGTH_SHORT).show();
            }
        });
    }
}