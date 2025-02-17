package com.example;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.helpers.REST;
import com.example.helpers.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializer;

import java.sql.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {

    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> Date.valueOf(json.getAsString()))
            .create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void login_submit(View view) {
        TextView login = findViewById(R.id.login_input);
        TextView psw = findViewById(R.id.password_input);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("login", login.getText().toString());
        jsonObject.addProperty("psw", psw.getText().toString());

        String info = gson.toJson(jsonObject);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = REST.sendPost(Constants.VALIDATE_USER, info);
                System.out.println("Raw server response: " + response);
                handler.post(() -> {
                    try {
                        if (!response.equals("Error") && !response.isEmpty()) {
                            JsonObject responseObject = gson.fromJson(response, JsonObject.class);
                            if (responseObject.has("type") && responseObject.has("id")) {
                                String userType = responseObject.get("type").getAsString();
                                int userId = responseObject.get("id").getAsInt();

                                if ("Client".equals(userType)) {
                                    navigateToMainActivity(userId);
                                } else if ("Admin".equals(userType)) {
                                    navigateToAdminActivity(userId);
                                } else {
                                    Toast.makeText(this, "Unknown user type", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Invalid JSON response", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Invalid login credentials", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "An error occurred during login", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void navigateToMainActivity(int userId) {
        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    private void navigateToAdminActivity(int userId) {
        Intent intent = new Intent(Login.this, AdminActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }
}
