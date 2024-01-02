package com.example.aifit;

import android.annotation.SuppressLint;
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

public class editProfile extends AppCompatActivity {
    EditText editName, editContact, editBirthdate;
    TextView editEmail;
    Button saveButton;
    String nameUser, emailUser, contactUser, birthdateUser;
    DatabaseReference reference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        reference = FirebaseDatabase.getInstance().getReference("profile");

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editContact = findViewById(R.id.editContact);
        editBirthdate = findViewById(R.id.editBirthdate);
        saveButton = findViewById(R.id.saveButton);

        showData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });
    }

    private void saveChanges() {
        if (isNameChanged() || isContactChanged() || isEmailChanged() || isBirthdateChanged()) {
            // Create an Intent to hold the edited data
            Intent resultIntent = new Intent();
            resultIntent.putExtra("editedName", nameUser);
            resultIntent.putExtra("editedEmail", emailUser);
            resultIntent.putExtra("editedContact", contactUser);
            resultIntent.putExtra("editedBirthdate", birthdateUser);

            // Set the result as OK and pass the Intent with edited data
            setResult(RESULT_OK, resultIntent);

            // Display a Toast indicating that changes are saved
            Toast.makeText(editProfile.this, "Changes Saved", Toast.LENGTH_SHORT).show();
        } else {
            // Display a Toast indicating that no changes are found
            Toast.makeText(editProfile.this, "No Changes Found", Toast.LENGTH_SHORT).show();
        }

        // Finish the activity
        finish();
    }

    private boolean isNameChanged() {
        String newName = editName.getText().toString().trim();
        if (!nameUser.equals(newName) && !newName.isEmpty()) {
            reference.child(nameUser).child("name").setValue(newName);
            nameUser = newName;
            return true;
        }
        return false;
    }

    private boolean isEmailChanged() {
        String newEmail = editEmail.getText().toString();
        if (!emailUser.equals(newEmail)) {
            reference.child(nameUser).child("email").setValue(newEmail);
            emailUser = newEmail;
            return true;
        }
        return false;
    }

    private boolean isContactChanged() {
        String newContact = editContact.getText().toString();
        if (!contactUser.equals(newContact)) {
            reference.child(nameUser).child("contact").setValue(newContact);
            contactUser = newContact;
            return true;
        }
        return false;
    }

    private boolean isBirthdateChanged() {
        String newBirthdate = editBirthdate.getText().toString();
        if (!birthdateUser.equals(newBirthdate)) {
            reference.child(nameUser).child("birthdate").setValue(newBirthdate);
            birthdateUser = newBirthdate;
            return true;
        }
        return false;
    }

    public void showData() {
        Intent intent = getIntent();
        nameUser = intent.getStringExtra("name");
        emailUser = intent.getStringExtra("email");
        contactUser = intent.getStringExtra("contact");
        birthdateUser = intent.getStringExtra("birthdate");

        editName.setText(nameUser);
        editEmail.setText(emailUser);
        editContact.setText(contactUser);
        editBirthdate.setText(birthdateUser);
    }
}
