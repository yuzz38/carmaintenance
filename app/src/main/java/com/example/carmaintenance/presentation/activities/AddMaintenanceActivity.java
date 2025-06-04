package com.example.carmaintenance.presentation.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carmaintenance.R;
import com.example.carmaintenance.data.Maintenance;

import java.util.Date;

public class AddMaintenanceActivity extends AppCompatActivity {

    private EditText mileageInput;
    private EditText notesInput;
    private CheckBox oilChangeCheckbox;
    private CheckBox transmissionOilCheckbox;
    private CheckBox brakePadsCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_maintenance);

        // Получаем текущий пробег из Intent
        int currentMileage = getIntent().getIntExtra("currentKm", 0);

        // Инициализация UI элементов
        mileageInput = findViewById(R.id.mileage);
        notesInput = findViewById(R.id.notes);
        oilChangeCheckbox = findViewById(R.id.oil_change);
        transmissionOilCheckbox = findViewById(R.id.transmission_oil_change);
        brakePadsCheckbox = findViewById(R.id.brake_pads_change);
        Button saveButton = findViewById(R.id.save_button);

        // Устанавливаем текущий пробег
        mileageInput.setText(String.valueOf(currentMileage));

        // Обработчик кнопки сохранения
        saveButton.setOnClickListener(v -> {
            try {
                // Создаем новую запись о ТО
                Maintenance maintenance = createMaintenanceRecord();

                // Проверяем, что выбрана хотя бы одна услуга
                if (!maintenance.isOilChanged() && !maintenance.isTransmissionOilChanged() && !maintenance.isBrakePadsChanged()) {
                    Toast.makeText(this, "Выберите хотя бы один вид обслуживания", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Возвращаем результат
                returnMaintenanceResult(maintenance);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Проверьте правильность введенного пробега", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Maintenance createMaintenanceRecord() throws NumberFormatException {
        Maintenance maintenance = new Maintenance();
        maintenance.setDate(new Date());
        maintenance.setMileage(Integer.parseInt(mileageInput.getText().toString()));
        maintenance.setOilChanged(oilChangeCheckbox.isChecked());
        maintenance.setTransmissionOilChanged(transmissionOilCheckbox.isChecked());
        maintenance.setBrakePadsChanged(brakePadsCheckbox.isChecked());
        maintenance.setNotes(notesInput.getText().toString());
        return maintenance;
    }

    private void returnMaintenanceResult(Maintenance maintenance) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("maintenance", maintenance);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }


}