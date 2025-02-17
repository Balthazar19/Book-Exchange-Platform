package com.example;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.helpers.Constants;
import com.example.helpers.REST;
import com.example.model.Publication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ListView availableBooksListView;
    private ArrayAdapter<Publication> adapter;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        availableBooksListView = findViewById(R.id.availableBooks);
        fetchAllPublications();
    }

    private void fetchAllPublications() {
        executor.execute(() -> {
            try {
                String response = REST.sendGet(Constants.GET_ALL_PUBLICATIONS);  // Ensure you are calling the correct endpoint
                System.out.println("Response: " + response);  // Debug line to see response
                runOnUiThread(() -> parseAndDisplayPublications(response));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to load publications", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void parseAndDisplayPublications(String response) {
        if (!response.isEmpty() && !response.equals("Error")) {
            Gson gson = new Gson();
            List<Publication> publications = gson.fromJson(response, new TypeToken<List<Publication>>(){}.getType());

            System.out.println("Publications Size: " + publications.size());

            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, publications);
            availableBooksListView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No publications available", Toast.LENGTH_SHORT).show();
        }
    }
}
