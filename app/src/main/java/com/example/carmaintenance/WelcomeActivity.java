package com.example.carmaintenance;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 2000; // 2 секунды

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(() -> {
            // Переходим на MainActivity
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Закрываем WelcomeActivity чтобы нельзя было вернуться назад
        }, SPLASH_DELAY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Убираем анимацию перехода
        overridePendingTransition(0, 0);
    }
}