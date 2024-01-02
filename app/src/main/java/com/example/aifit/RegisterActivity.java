package com.example.aifit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth Auth;
    private EditText usEmail, usPass;
    private Button regButton, redirecButton;

    // Firebase Database
    FirebaseDatabase fData;
    DatabaseReference dataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        Auth = FirebaseAuth.getInstance();

        // Initialize UserInterface elements
        usEmail = findViewById(R.id.email);
        usPass = findViewById(R.id.registerpassword);
        regButton = findViewById(R.id.buttonregister);
        redirecButton = findViewById(R.id.AlreadyHaveAccount);

        // Set clicklistener for button
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize Database
                fData = FirebaseDatabase.getInstance();
                dataRef = fData.getReference("users");

                // Get the userinput
                String email = usEmail.getText().toString().trim();
                String password = usPass.getText().toString().trim();

                // Validate the userinput
                if (email.isEmpty()) {
                    usEmail.setError("Email cannot be empty");
                } else if (password.isEmpty()) {
                    usPass.setError("Password cannot be empty");
                } else {
                    // Create a new user using Authentication
                    Auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Signup successful
                                        Toast.makeText(RegisterActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();

                                        // Save user data in Database
                                        HelperClass helperClass = new HelperClass(email, password);
                                        dataRef.child(email.replace(".", "_")).setValue(helperClass);

                                        // Redirect to CreateProfile
                                        Intent intent = new Intent(RegisterActivity.this,Createprofile.class);
                                        intent.putExtra("email", email);
                                        intent.putExtra("password", password);
                                        startActivity(intent);
                                    } else {
                                        // If signup fails, shows error message
                                        Toast.makeText(RegisterActivity.this, "Signup failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        // Set clicklistener for redirect to login button
        redirecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirect to LoginActivity
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }
}
