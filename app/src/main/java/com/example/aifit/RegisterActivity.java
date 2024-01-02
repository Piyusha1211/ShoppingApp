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
    private FirebaseAuth auth;
    private EditText Email,Password;
    private Button button;
    private Button Redirect;

    FirebaseDatabase db;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth=FirebaseAuth.getInstance();
        Email=findViewById(R.id.email);
        Password=findViewById(R.id.registerpassword);
        button=findViewById(R.id.buttonregister);
        Redirect=findViewById(R.id.AlreadyHaveAccount);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = FirebaseDatabase.getInstance();
                reference = db.getReference("users");
                String user = Email.getText().toString();
                String pass = Password.getText().toString();

                if (user.isEmpty()) {
                    Email.setError("Email cannot be empty");
                } else if (pass.isEmpty()) {
                    Password.setError("Password cannot be empty");
                } else {
                    auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
                                HelperClass hc = new HelperClass(user, pass);
                                reference.child(user.replace(".", "_")).setValue(hc);

                                Intent i = new Intent(RegisterActivity.this, Createprofile.class);
                                i.putExtra("email", user);
                                i.putExtra("password", pass);
                                startActivity(i);
                            } else {
                                Toast.makeText(RegisterActivity.this, "Signup failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        Redirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
    }
}