package com.example.courseproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private AuthManager authManager;
    private TextView nameTextView, emailTextView, userTypeTextView;
    private Switch notificationsSwitch, darkModeSwitch;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        authManager = new AuthManager(this);

        // ربط العناصر بالواجهة
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        userTypeTextView = findViewById(R.id.userTypeTextView);
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        logoutButton = findViewById(R.id.logoutButton);

        // تحميل معلومات المستخدم
        nameTextView.setText("الاسم: " + authManager.getName());
        emailTextView.setText("البريد: " + authManager.getEmail());
        userTypeTextView.setText("النوع: " + authManager.getUserType());

        // SharedPreferences لتخزين حالة الوضع الليلي
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        boolean isDarkMode = preferences.getBoolean("dark_mode", false);
        darkModeSwitch.setChecked(isDarkMode);

        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        // عند تغيير وضع الليل
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();

            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            // إعادة تشغيل النشاط لتطبيق التغيير
            recreate();
        });

        // تسجيل الخروج
        logoutButton.setOnClickListener(v -> {
            authManager.logout();
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            finish();
        });
    }
}
