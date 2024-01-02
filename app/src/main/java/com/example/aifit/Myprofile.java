package com.example.aifit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Myprofile extends AppCompatActivity {
    TextView profileName, profileEmail, profileContact, profileBirthdate;
    TextView titleName;
    String nameFromDB, emailFromDB, birthdateFromDB, contactFromDB;
    Button editProfile;
    static final int EDIT_PROFILE_REQUEST_CODE = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileContact = findViewById(R.id.profileContact);
        profileBirthdate = findViewById(R.id.profileBirthdate);
        titleName = findViewById(R.id.titleName);
        editProfile = findViewById(R.id.editButton);

        showAllUserData();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passUserData();
            }
        });
    }

    public void showAllUserData() {
        Intent intent = getIntent();
        String nameUser = intent.getStringExtra("name");
        String emailUser = intent.getStringExtra("email");
        String contactUser = intent.getStringExtra("contact");
        String birthdateUser = intent.getStringExtra("birthdate");

        titleName.setText(nameUser);
        profileName.setText(nameUser);
        profileEmail.setText(emailUser);
        profileContact.setText(contactUser);
        profileBirthdate.setText(birthdateUser);
    }

    public void passUserData() {
        String userUsername = profileName.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("profile");
        Query checkUserDatabase = reference.orderByChild("name").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    nameFromDB = snapshot.child(userUsername).child("name").getValue(String.class);
                    emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);
                    contactFromDB = snapshot.child(userUsername).child("contact").getValue(String.class);
                    birthdateFromDB = snapshot.child(userUsername).child("birthdate").getValue(String.class);

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
            // Handle edited data from EditProfile activity
            showAllUserData();
        }
    }

    public void passUserData1() {
        Intent intent = getIntent();
        nameFromDB = intent.getStringExtra("name");
    }

    @Override
    public void onBackPressed() {
        passUserData1();
        Intent intent = new Intent(Myprofile.this, MainActivity.class);
        intent.putExtra("name", nameFromDB);
        startActivity(intent);
    }
}
