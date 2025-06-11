package com.example.courseproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        authManager = new AuthManager(this);

        TextView welcomeText = findViewById(R.id.welcomeText);
        Button actionButton = findViewById(R.id.actionButton);
        Button manageUsersButton = findViewById(R.id.manageUsersButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button settingsButton = findViewById(R.id.settingsButton); // ✅ زر الإعدادات

        String name = authManager.getName();
        String userType = authManager.getUserType();

        welcomeText.setText("مرحبًا، " + name + " (" + userType + ")");

        switch (userType) {
            case "student":
                actionButton.setText("عرض جدولي");
                manageUsersButton.setVisibility(View.GONE);
                break;
            case "teacher":
                actionButton.setText("إدارة الصفوف");
                manageUsersButton.setVisibility(View.GONE);
                break;
            case "registrar":
                actionButton.setText("عرض الطلبات");
                manageUsersButton.setText("إدارة المستخدمين");
                manageUsersButton.setVisibility(View.VISIBLE);
                break;
            default:
                actionButton.setText("استكشاف");
                manageUsersButton.setVisibility(View.GONE);
        }

        logoutButton.setOnClickListener(v -> {
            authManager.logout();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });

        // ✅ فتح صفحة الإعدادات
        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        });
    }
}
