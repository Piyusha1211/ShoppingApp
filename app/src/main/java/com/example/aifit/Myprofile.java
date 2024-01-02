package com.example.aifit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Myprofile extends AppCompatActivity {
    TextView pName, pEmail, pContact, pDoB;
    TextView tName;
    Button Profile;
    static final int EDIT_PROFILE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        initializeViews();

        // Shows user data
        showAllUserData();

        // Set up onClickListener for button
        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass user data to the EditProfile
                passUserDataToEdit();
            }
        });
    }

    private void initializeViews() {
        pName = findViewById(R.id.profileName);
        pEmail = findViewById(R.id.profileEmail);
        pContact = findViewById(R.id.profileContact);
        pDoB = findViewById(R.id.profileBirthdate);
        tName = findViewById(R.id.titleName);
        Profile = findViewById(R.id.editButton);
    }

    private void showAllUserData() {
        // Get user data from the intent
        Intent intent = getIntent();
        String nameUser = intent.getStringExtra("name");
        String emailUser = intent.getStringExtra("email");
        String contactUser = intent.getStringExtra("contact");
        String birthdateUser = intent.getStringExtra("birthdate");

        // Shows user data
        tName.setText(nameUser);
        pName.setText(nameUser);
        pEmail.setText(emailUser);
        pContact.setText(contactUser);
        pDoB.setText(birthdateUser);
    }

    private void passUserDataToEdit() {
        // Get the username from the profile
        String userUsername = pName.getText().toString().trim();

        // Query the database to get user details
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("profile");
        Query checkUserDatabase = reference.orderByChild("name").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get user details from the database
                    String nameFromDB = snapshot.child(userUsername).child("name").getValue(String.class);
                    String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);
                    String contactFromDB = snapshot.child(userUsername).child("contact").getValue(String.class);
                    String birthdateFromDB = snapshot.child(userUsername).child("birthdate").getValue(String.class);

                    // Pass user details to the EditProfile
                    Intent intent = new Intent(Myprofile.this, editProfile.class);
                    intent.putExtra("name", nameFromDB);
                    intent.putExtra("email", emailFromDB);
                    intent.putExtra("contact", contactFromDB);
                    intent.putExtra("birthdate", birthdateFromDB);

                    startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Manage edited data from EditProfile
            showAllUserData();
        }
    }

    @Override
    public void onBackPressed() {
        // Pass user data back to MainActivity when the back button is pressed
        Intent intent = new Intent(Myprofile.this, MainActivity.class);
        intent.putExtra("name", tName.getText().toString());
        startActivity(intent);
    }
}
