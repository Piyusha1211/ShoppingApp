package com.example.aifit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private FirebaseAuth mAuth;
    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private TextView signupRedirectText, forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        loginEmail = findViewById(R.id.inputEmail);
        loginPassword = findViewById(R.id.inputPassword);
        loginButton = findViewById(R.id.Login);
        signupRedirectText = findViewById(R.id.signUp);
        forgotPassword = findViewById(R.id.ForgotPassword);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateEmail() || !validatePassword()) {
                    // Handle validation failure
                    Toast.makeText(LoginActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                } else {
                    signInUser();
                }
            }
        });

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is already logged in, start MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPasswordDialog();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }

    private void signInUser() {
        String userEmail = loginEmail.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User is authenticated, check user profile
                            checkUserProfile(userEmail);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserProfile(String userEmail) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("profile");
        Query checkUserDatabase = reference.orderByChild("email").equalTo(userEmail);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User profile exists in the database
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();

                    String nameFromDB = userSnapshot.child("name").getValue(String.class);
                    String contactFromDB = userSnapshot.child("contact").getValue(String.class);
                    String emailFromDB = userSnapshot.child("email").getValue(String.class);
                    String birthDateFromDB = userSnapshot.child("birthdate").getValue(String.class);

                    // Redirect to MainActivity with user data
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("name", nameFromDB);
                    intent.putExtra("email", emailFromDB);
                    intent.putExtra("birthdate", birthDateFromDB);
                    intent.putExtra("contact", contactFromDB);
                    startActivity(intent);
                    finish();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
        EditText emailBox = dialogView.findViewById(R.id.emailBox);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = emailBox.getText().toString();
                if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    Toast.makeText(LoginActivity.this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(LoginActivity.this, "Unable to send email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.show();
    }

    private boolean validateEmail() {
        String val = loginEmail.getText().toString();
        if (val.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(val).matches()) {
            loginEmail.setError("Enter a valid email address");
            return false;
        } else {
            loginEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Password cannot be empty");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void reload() {
        // Implement your logic to handle the reload or go to the main activity
    }
}
