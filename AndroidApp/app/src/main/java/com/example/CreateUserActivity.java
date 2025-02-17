package com.example;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helpers.Constants;
import com.example.helpers.REST;
import com.google.gson.JsonObject;

import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CreateUserActivity extends AppCompatActivity {

    private EditText editTextBirthDate;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        executor = Executors.newSingleThreadExecutor();

        EditText editTextLogin = findViewById(R.id.editTextLogin);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        EditText editTextName = findViewById(R.id.editTextName);
        EditText editTextSurname = findViewById(R.id.editTextSurname);
        EditText editTextAddress = findViewById(R.id.editTextAddress);
        editTextBirthDate = findViewById(R.id.editTextBirthDate);
        EditText editTextPhoneNum = findViewById(R.id.editTextPhoneNum);

        Switch switchUserType = findViewById(R.id.switchUserType);
        Button buttonCreateUser = findViewById(R.id.buttonCreateUser);

        switchUserType.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editTextAddress.setVisibility(View.GONE);
                editTextBirthDate.setVisibility(View.GONE);
                editTextPhoneNum.setVisibility(View.VISIBLE);
            } else {
                editTextAddress.setVisibility(View.VISIBLE);
                editTextBirthDate.setVisibility(View.VISIBLE);
                editTextPhoneNum.setVisibility(View.GONE);
            }
        });

        editTextBirthDate.setOnClickListener(v -> showDatePickerDialog());

        buttonCreateUser.setOnClickListener(v -> {
            String login = editTextLogin.getText().toString();
            String password = editTextPassword.getText().toString();
            String name = editTextName.getText().toString();
            String surname = editTextSurname.getText().toString();
            String address = editTextAddress.getText().toString();
            String birthDate = editTextBirthDate.getText().toString();
            String phoneNum = editTextPhoneNum.getText().toString();

            if (switchUserType.isChecked()) {
                createAdmin(login, password, name, surname, phoneNum);
            } else {
                createClient(login, password, name, surname, address, birthDate);
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            String selectedDate = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            editTextBirthDate.setText(selectedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void createAdmin(String login, String password, String name, String surname, String phoneNum) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("login", login);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("surname", surname);
        jsonObject.addProperty("phoneNum", phoneNum);

        String payload = jsonObject.toString();
        executor.execute(() -> {
            try {
                String response = REST.sendPost(Constants.BASE_URL + "createAdmin", payload);
                runOnUiThread(() -> {
                    if (!response.equals("Error")) {
                        Toast.makeText(this, "Admin created successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to create Admin", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to create Admin", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void createClient(String login, String password, String name, String surname, String address, String birthDate) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("login", login);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("surname", surname);
        jsonObject.addProperty("address", address);
        jsonObject.addProperty("birthDate", birthDate);

        String payload = jsonObject.toString();
        executor.execute(() -> {
            try {
                String response = REST.sendPost(Constants.BASE_URL + "createClient", payload);
                runOnUiThread(() -> {
                    if (!response.equals("Error")) {
                        Toast.makeText(this, "Client created successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to create Client", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to create Client", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
