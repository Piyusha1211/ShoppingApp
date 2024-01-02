package com.example.aifit;

// Import the statements

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth Auth;
    private EditText Email, Password;
    private Button Button;
    private TextView signup, forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Authentication
        Auth = FirebaseAuth.getInstance();

        // Initialize UserInterface elements
        Email = findViewById(R.id.inEmail);
        Password = findViewById(R.id.inPassword);
        Button = findViewById(R.id.Login);
        signup = findViewById(R.id.signUp);
        forgot = findViewById(R.id.ForgotPassword);

        // Set clicklistener for button
        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate email and password
                if (!isValidEmail() || !isValidPassword()) {
                    // Check the failure
                    Toast.makeText(LoginActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                } else {
                    // Confirm to sign in the user
                    signInUser();
                }
            }
        });

        // Check whether the user is logged in or not
        FirebaseUser current = Auth.getCurrentUser();
        if (current != null) {
            // User is already logged in, start MainActivity
            startMainActivity();
            return;
        }

        // Set click listener for signup button
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirect to the registration activity
                startRegistrationActivity();
            }
        });

        // Set click listener for forgot password
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Display the forgot password dialog
                showForgotPasswordDialog();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in and update UI accordingly
        FirebaseUser currentUser = Auth.getCurrentUser();
        if (currentUser != null) {
            reloadUserDetails();
        }
    }

    private void reloadUserDetails() {

    }

    private void signInUser() {
        // Get user email and password
        String userEmail = Email.getText().toString().trim();
        String userPassword = Password.getText().toString().trim();

        // Attempt to sign in using Firebase Auth
        Auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User is authenticated, check user profile
                            checkUserProfile(userEmail);
                        } else {
                            // If sign in fails, display a message to the user
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserProfile(String userEmail) {
        // Check if the user profile exists in the database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("profile");
        Query checkUserDatabase = reference.orderByChild("email").equalTo(userEmail);
        ((Query) checkUserDatabase).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User profile exists in the database, extract user details
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    String nameFromDB = userSnapshot.child("name").getValue(String.class);
                    String contactFromDB = userSnapshot.child("contact").getValue(String.class);
                    String emailFromDB = userSnapshot.child("email").getValue(String.class);
                    String birthDateFromDB = userSnapshot.child("birthdate").getValue(String.class);

                    // Redirect to MainActivity with user data
                    redirectToMainActivity(nameFromDB, emailFromDB, birthDateFromDB, contactFromDB);
                } else {
                    // User profile does not exist in the database
                    Toast.makeText(LoginActivity.this, "User profile not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
                Toast.makeText(LoginActivity.this, "Database error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showForgotPasswordDialog() {
        // Build and display the forgot password dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
        EditText emailBox = dialogView.findViewById(R.id.emailBox);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Set click listener for reset button
        dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reset password using the provided email address
                resetPassword(emailBox.getText().toString());
                dialog.dismiss();
            }
        });

        // Set click listener for cancel button
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        // Set background for the dialog window
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        // Display the dialog
        dialog.show();
    }

    // Validation methods

    private boolean isValidEmail() {
        // Validate email format
        String email = Email.getText().toString();
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Enter a valid email address");
            return false;
        } else {
            Email.setError(null);
            return true;
        }
    }

    private boolean isValidPassword() {
        // Validate password presence
        String password = Password.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Password.setError("Password cannot be empty");
            return false;
        } else {
            Password.setError(null);
            return true;
        }
    }

    private void startMainActivity() {
        // Start the main activity
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void startRegistrationActivity() {
        // Redirect to the registration activity
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private void redirectToMainActivity(String name, String email, String birthDate, String contact) {
        // Redirect to MainActivity with user data
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("birthdate", birthDate);
        intent.putExtra("contact", contact);
        startActivity(intent);
        finish();
    }

    private void resetPassword(String userEmail) {
        // Reset password using Firebase Auth
        Auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Unable to send email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Finish the entire application
        finishAffinity();

    }
}
