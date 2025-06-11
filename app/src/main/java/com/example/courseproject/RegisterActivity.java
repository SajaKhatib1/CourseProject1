package com.example.courseproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Spinner userTypeSpinner;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        userTypeSpinner = findViewById(R.id.userTypeSpinner);
        registerButton = findViewById(R.id.registerButton);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"طالب", "معلم", "مسجل"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        registerButton.setOnClickListener(v -> attemptRegister());
        Button backToLoginButton = findViewById(R.id.backToLoginButton);
        backToLoginButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
       

    }

    private void attemptRegister() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        String selected = userTypeSpinner.getSelectedItem().toString();
        String userType = "";

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
        }

        // التحقق من الحقول
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        // التحقق من تطابق كلمة المرور
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "كلمة المرور وتأكيدها غير متطابقين", Toast.LENGTH_SHORT).show();
            return;
        }

        // إنشاء JSON
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", name);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("user_type", userType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // إرسال الطلب باستخدام Volley
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Constants.BASE_URL + "register.php",
                jsonBody,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");

                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();

                            if (success) {
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "خطأ في تحليل الرد", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        Toast.makeText(RegisterActivity.this, "فشل التسجيل: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        NetworkManager.getInstance(this).addToRequestQueue(request);
    }
}
