package com.example;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.helpers.Constants;
import com.example.helpers.REST;
import com.example.model.Admin;
import com.example.model.Client;
import com.example.model.User;
import com.example.model.UserTypeAdapter;
import com.example.model.SqlDateTypeAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AdminActivity extends AppCompatActivity {

    private ListView userListView;
    private ArrayAdapter<String> adapter;
    private List<User> userList;
    private int selectedUserId = -1;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(User.class, new UserTypeAdapter())
            .registerTypeAdapter(Date.class, new SqlDateTypeAdapter())
            .create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);

        userListView = findViewById(R.id.userList);
        fetchAllUsers();

        Button createUserButton = findViewById(R.id.createUserButton);
        Button deleteUserButton = findViewById(R.id.deleteUserButton);
        Button updateUserButton = findViewById(R.id.updateUserButton);

        createUserButton.setOnClickListener(v -> createUser());
        deleteUserButton.setOnClickListener(v -> deleteUser());
        updateUserButton.setOnClickListener(v -> updateUser());

        userListView.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = userList.get(position);
            selectedUserId = selectedUser.getId();
            Toast.makeText(AdminActivity.this, "Selected User ID: " + selectedUserId, Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchAllUsers() {
        executor.execute(() -> {
            try {
                String response = REST.sendGet(Constants.BASE_URL + "allUsers");
                runOnUiThread(() -> displayUsers(response));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(AdminActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void displayUsers(String response) {
        Log.d("User List JSON", response);
        if (!response.isEmpty() && !response.equals("Error")) {
            userList = gson.fromJson(response, new TypeToken<List<User>>(){}.getType());

            List<String> userDisplayList = new ArrayList<>();
            for (User user : userList) {
                userDisplayList.add(user.getName() + " " + user.getSurname() + " (" + (user instanceof Admin ? "Admin" : "Client") + ")");
            }

            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userDisplayList);
            userListView.setAdapter(adapter);
            userListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        } else {
            Toast.makeText(this, "No users available", Toast.LENGTH_SHORT).show();
        }
    }

    private void createUser() {
        Intent intent = new Intent(AdminActivity.this, CreateUserActivity.class);
        startActivity(intent);
    }

    private void deleteUser() {
        if (selectedUserId == -1) {
            Toast.makeText(this, "Select a user to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            try {
                String response = REST.sendDelete(Constants.BASE_URL + "deleteUser/" + selectedUserId);
                runOnUiThread(() -> {
                    if ("Success".equals(response)) {
                        Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
                        fetchAllUsers();
                        selectedUserId = -1;
                    } else {
                        Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateUser() {
        if (selectedUserId == -1) {
            Toast.makeText(this, "Select a user to update", Toast.LENGTH_SHORT).show();
        }
    }
}
