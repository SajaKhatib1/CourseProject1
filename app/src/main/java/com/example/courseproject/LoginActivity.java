package com.example.courseproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Spinner userTypeSpinner;
    private Button loginButton;
    private ProgressBar progressBar;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ✅ تفعيل الوضع الليلي إذا كان مفعّل
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ربط العناصر
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        userTypeSpinner = findViewById(R.id.userTypeSpinner);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        TextView registerLink = findViewById(R.id.registerLink);

        authManager = new AuthManager(this);

        // تعبئة السبينر بأنواع المستخدمين
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        // إذا كان المستخدم مسجل دخول مسبقًا
        if (authManager.isLoggedIn()) {
            navigateToHome();
            return;
        }

        // عند الضغط على تسجيل الدخول
        loginButton.setOnClickListener(v -> attemptLogin());

        // الانتقال إلى شاشة التسجيل
        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String selected = userTypeSpinner.getSelectedItem().toString();
        String userType;

        // تحويل النص العربي إلى النوع الصحيح
        switch (selected) {
            case "طالب":
                userType = "student";
                break;
            case "معلم":
                userType = "teacher";
                break;
            case "مسجل":
                userType = "registrar";
                break;
            default:
                userType = "";
        }

        // تحقق من الحقول
        if (email.isEmpty()) {
            emailEditText.setError("البريد مطلوب");
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("كلمة المرور مطلوبة");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        // تجهيز بيانات JSON
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("user_type", userType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // إرسال الطلب إلى الخادم
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Constants.BASE_URL + "login.php",
                jsonBody,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            // حفظ البيانات
                            JSONObject user = response.getJSONObject("user");
                            authManager.saveLoginData(
                                    user.getString("email"),
                                    user.getString("user_type"),
                                    user.getString("name"),
                                    user.getString("id")
                            );
                            navigateToHome();
                        } else {
                            String message = response.getString("message");
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "خطأ في تحليل الرد", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "فشل تسجيل الدخول: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        // إضافة الطلب إلى الطابور
        NetworkManager.getInstance(this).addToRequestQueue(request);
    }

    private void navigateToHome() {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }
}
