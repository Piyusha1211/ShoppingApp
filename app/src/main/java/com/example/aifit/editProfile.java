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

public class editProfile extends AppCompatActivity {
    EditText eName, eContact, eDoB;
    TextView eEmail;
    Button save;
    String nUser, eUser, cUser, DoBUser;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Database reference
        ref = FirebaseDatabase.getInstance().getReference("profile");

        // Initialize UserInterface elements
        eName = findViewById(R.id.editName);
        eEmail = findViewById(R.id.editEmail);
        eContact = findViewById(R.id.editContact);
        eDoB = findViewById(R.id.editBirthdate);
        save = findViewById(R.id.saveButton);

        // Show existing data in the UserInterface
        showData();

        // Set onClickListener for the button
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save changes when the button is clicked
                saveChanges();
            }
        });
    }

    // changes done by the user
    private void saveChanges() {
        if (isNameChanged() || isContactChanged() || isEmailChanged() || isBirthdateChanged()) {
            // If any data is altered, create an Intent to keep the edited data
            Intent resultIntent = new Intent();
            resultIntent.putExtra("editedName", nUser);
            resultIntent.putExtra("editedEmail", eUser);
            resultIntent.putExtra("editedContact", cUser);
            resultIntent.putExtra("editedBirthdate", DoBUser);

            // Set the result as OK and pass the Intent with edited data
            setResult(RESULT_OK, resultIntent);

            // Show a Toast indicating that changes are saved
            Toast.makeText(editProfile.this, "Changes Saved", Toast.LENGTH_SHORT).show();
        } else {
            // If no changes are found, show a Toast
            Toast.makeText(editProfile.this, "No Changes Found", Toast.LENGTH_SHORT).show();
        }

        // Finish the activity
        finish();
    }

    // Check if the name is changed
    private boolean isNameChanged() {
        String newName = eName.getText().toString().trim();
        if (!nUser.equals(newName) && !newName.isEmpty()) {
            // If changed, update the database and update the local variable
            ref.child(nUser).child("name").setValue(newName);
            nUser = newName;
            return true;
        }
        return false;
    }

    // Check if the email is changed
    private boolean isEmailChanged() {
        String newEmail = eEmail.getText().toString();
        if (!eUser.equals(newEmail)) {
            // If changed, update the database and update the local variable
            ref.child(nUser).child("email").setValue(newEmail);
            eUser = newEmail;
            return true;
        }
        return false;
    }

    // Check if the contact is changed
    private boolean isContactChanged() {
        String newContact = eContact.getText().toString();
        if (!cUser.equals(newContact)) {
            // If changed, update the database and update the local variable
            ref.child(nUser).child("contact").setValue(newContact);
            cUser = newContact;
            return true;
        }
        return false;
    }

    // Check if the birthdate is changed
    private boolean isBirthdateChanged() {
        String newBirthdate = eDoB.getText().toString();
        if (!eDoB.equals(newBirthdate)) {
            // If changed, update the database and update the local variable
            ref.child(nUser).child("birthdate").setValue(newBirthdate);
            DoBUser = newBirthdate;
            return true;
        }
        return false;
    }

    // Display existing data in the UserInterface
    private void showData() {
        Intent intent = getIntent();
        nUser = intent.getStringExtra("name");
        eUser = intent.getStringExtra("email");
        cUser = intent.getStringExtra("contact");
        DoBUser = intent.getStringExtra("birthdate");

        // Set the existing data in the UserInterface
        eName.setText(nUser);
        eEmail.setText(eUser);
        eContact.setText(cUser);
        eDoB.setText(DoBUser);
    }
}
