package com.example.carmaintenance.presentation.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carmaintenance.R;
import com.example.carmaintenance.data.Car;

public class EditSpecsActivity extends AppCompatActivity {
    private Car car;
    private ImageView carImageView;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageButton deleteImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_specs);
        EditText currentMileage = findViewById(R.id.current_mileage);
        carImageView = findViewById(R.id.car_image);
        deleteImageButton = findViewById(R.id.delete_image_button);
        car = (Car) getIntent().getSerializableExtra("car");
        if (car == null) {
            car = new Car();
        }
        // Проверяем, есть ли изображение

        if (car.getImageUriString() != null && !car.getImageUriString().isEmpty()) {
            try {
                imageUri = Uri.parse(car.getImageUriString());
                carImageView.setImageURI(imageUri);
                deleteImageButton.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e("EditSpecs", "Error loading image", e);
            }
        }
        else {
            carImageView.setImageResource(R.drawable.default_car);
            deleteImageButton.setVisibility(View.GONE);
        }

        // Обработчик кнопки удаления изображения
        deleteImageButton.setOnClickListener(v -> {
            carImageView.setImageResource(R.drawable.default_car);
            car.setImageUriString(null);
            car.setImagePath(null);
            imageUri = null;
            deleteImageButton.setVisibility(View.GONE);
        });
        // Инициализация UI элементов
        carImageView = findViewById(R.id.car_image);
        Button selectImageButton = findViewById(R.id.select_image_button);
        EditText carName = findViewById(R.id.car_name);
        EditText year = findViewById(R.id.year);
        EditText licensePlate = findViewById(R.id.license_plate);
        Spinner engineType = findViewById(R.id.engine_type);
        Spinner driveType = findViewById(R.id.drive_type);
        Spinner transmissionType = findViewById(R.id.transmission_type);
        EditText dailyMileage = findViewById(R.id.daily_mileage);
        Button saveButton = findViewById(R.id.save_button);

        // Заполняем поля текущими значениями
        if (car.getName() != null) carName.setText(car.getName());
        if (car.getLicensePlate() != null) licensePlate.setText(car.getLicensePlate());
        if (car.getYear() > 0) year.setText(String.valueOf(car.getYear()));
        if (car.getDailyMileage() > 0) dailyMileage.setText(String.valueOf(car.getDailyMileage()));
        if (car.getCurrentMileage() > 0) currentMileage.setText(String.valueOf(car.getCurrentMileage()));

        // Загружаем изображение, если оно есть
        if (car.getImagePath() != null && !car.getImagePath().isEmpty()) {
            try {
                carImageView.setImageURI(Uri.parse(car.getImagePath()));
            } catch (Exception e) {
                Log.e("ImageLoad", "Error loading image", e);
            }
        }

        // Настройка спиннеров
        ArrayAdapter<CharSequence> engineAdapter = ArrayAdapter.createFromResource(this,
                R.array.engine_types, android.R.layout.simple_spinner_item);
        engineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        engineType.setAdapter(engineAdapter);
        if (car.getEngineType() != null) {
            int spinnerPosition = engineAdapter.getPosition(car.getEngineType());
            engineType.setSelection(spinnerPosition);
        }

        ArrayAdapter<CharSequence> driveAdapter = ArrayAdapter.createFromResource(this,
                R.array.drive_types, android.R.layout.simple_spinner_item);
        driveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driveType.setAdapter(driveAdapter);
        if (car.getDriveType() != null) {
            int spinnerPosition = driveAdapter.getPosition(car.getDriveType());
            driveType.setSelection(spinnerPosition);
        }

        ArrayAdapter<CharSequence> transmissionAdapter = ArrayAdapter.createFromResource(this,
                R.array.transmission_types, android.R.layout.simple_spinner_item);
        transmissionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transmissionType.setAdapter(transmissionAdapter);
        if (car.getTransmissionType() != null) {
            int spinnerPosition = transmissionAdapter.getPosition(car.getTransmissionType());
            transmissionType.setSelection(spinnerPosition);
        }

        // Инициализация image picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            carImageView.setImageURI(selectedImageUri);
                            car.setImagePath(selectedImageUri.toString());
                        }
                    }
                });

        // Обработчик кнопки выбора изображения
        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });


        // Обработчик кнопки сохранения
        saveButton.setOnClickListener(v -> {
            try {
                // Сохраняем изменения
                car.setName(carName.getText().toString());
                car.setYear(Integer.parseInt(year.getText().toString()));
                car.setLicensePlate(licensePlate.getText().toString());
                car.setEngineType(engineType.getSelectedItem().toString());
                car.setDriveType(driveType.getSelectedItem().toString());
                car.setTransmissionType(transmissionType.getSelectedItem().toString());
                car.setDailyMileage(Double.parseDouble(dailyMileage.getText().toString()));
                car.setCurrentMileage(Integer.parseInt(currentMileage.getText().toString()));

                // Возвращаем результат
                Intent resultIntent = new Intent();
                resultIntent.putExtra("car", car);
                setResult(RESULT_OK, resultIntent);
                // В saveButton.setOnClickListener перед finish():
                if (imageUri != null) {
                    car.setImageUriString(imageUri.toString());
                } else {
                    car.setImageUriString(null);
                }
                // Если изображение удалено, сохраняем это
                if (imageUri == null) {
                    car.setImageUriString(null);
                    car.setImagePath(null);
                }
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Проверьте правильность введенных данных", Toast.LENGTH_SHORT).show();
                Log.e("SAVE_ERROR", "Error saving car", e);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            // Получаем постоянные права на доступ к файлу
            getContentResolver().takePersistableUriPermission(imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
            carImageView.setImageURI(imageUri);
            car.setImageUriString(imageUri.toString());
            deleteImageButton.setVisibility(View.VISIBLE);
        }
    }
}