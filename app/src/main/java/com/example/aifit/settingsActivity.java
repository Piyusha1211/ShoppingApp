package com.example.aifit;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class settingsActivity extends AppCompatActivity {
    RecyclerView recView;
    List<DataClass> List;
    SearchView sView;
    MyAdapter adap;
    ValueEventListener eList;
    String nameDB;
    DatabaseReference dataRef;
    Query dataQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Get the category from the Intent
        String category = getIntent().getStringExtra("category");

        // Initialize UserInterface components
        recView = findViewById(R.id.recyclerView);
        sView = findViewById(R.id.search);
        sView.clearFocus();

        // Set up the RecyclerView with a GridLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(settingsActivity.this, 1);
        recView.setLayoutManager(gridLayoutManager);

        // Set up AlertDialog for progress
        AlertDialog.Builder builder = new AlertDialog.Builder(settingsActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.activity_progress);
        AlertDialog dialog = builder.create();

        // Initialize data list and adapter
        List = new ArrayList<>();
        adap = new MyAdapter(settingsActivity.this, List);
        recView.setAdapter(adap);

        // Initialize DatabaseReference and Query
        dataRef = FirebaseDatabase.getInstance().getReference("upload");
        dataQ = FirebaseDatabase.getInstance().getReference("upload")
                .orderByChild("dataCategory")
                .equalTo(category); // Filter the results based on the category

        // Display progress dialog
        dialog.show();

        // Attach ValueEventListener to get data from the database
        eList = dataQ.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    dataClass.setKey(itemSnapshot.getKey());
                    List.add(dataClass);
                }
                adap.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        // Set up SearchView listener for removing data
        sView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
    }

    // Method to remove data based on search text
    public void searchList(String text) {
        ArrayList<DataClass> searchList = new ArrayList<>();
        for (DataClass dataClass : List) {
            if (dataClass.getDataTitle().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(dataClass);
            }
        }
        adap.searchDataList(searchList);
    }

    // Method to pass user data
    public void passUserData() {
        Intent intent = getIntent();
        nameDB = intent.getStringExtra("name");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        passUserData();
        Intent intent = new Intent(settingsActivity.this, MainActivity.class);
        intent.putExtra("name", nameDB);
        startActivity(intent);
    }
}
