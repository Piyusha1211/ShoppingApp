package com.example.aifit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Createprofile extends AppCompatActivity {
    // Declare UI elements
    EditText nam, cont, bd;
    TextView email, pass;
    Button Profile;

    FirebaseDatabase data;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createprofile);

        // Initialize UserInterface elements
        nam = findViewById(R.id.sname);
        email = findViewById(R.id.semail);
        bd = findViewById(R.id.birthdate);
        cont = findViewById(R.id.contact);
        pass = findViewById(R.id.pass);
        Profile = findViewById(R.id.createProfile);

        // Extract data from the previous activity
        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("email");
        String userPassword = intent.getStringExtra("password");

        // Set email and password
        email.setText(userEmail);
        pass.setText(userPassword);

        // Set clicklistener for profile button
        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize Database
                data = FirebaseDatabase.getInstance();
                ref = data.getReference("profile");

                // Get user input
                String userName = nam.getText().toString();
                String userContact = cont.getText().toString();
                String userBirthdate = bd.getText().toString();

                // Make HelperC object with userdetails
                HelperC helperClass = new HelperC(userName, userEmail, userContact, userBirthdate, userPassword);

                // Save userprofile in Database
                ref.child(userName.replace(".", "_")).setValue(helperClass);

                // Show success message
                Toast.makeText(Createprofile.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();

                // Goes to LoginActivity
                Intent loginIntent = new Intent(Createprofile.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }
}
