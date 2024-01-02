package com.example.aifit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SwitchCompat Mode;
    SharedPreferences shPref;
    SharedPreferences.Editor edit;
    ImageSlider imgSl;
    String nameDB, emailDB, birthdateDB, contactDB;
    NavigationView navView;
    private DrawerLayout drawLay;
    Toolbar tb;
    CardView shirt, coat, cap, frock, pant, shorts;

    SessionManager sess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();

        setupImageSlider();

        setupNightModeSwitch();

        setCardClickListeners();

        setupToolbar();

        setNavigationDrawer();

        getUserDetails();

        handleSavedInstanceState(savedInstanceState);
    }

    private void initializeUI() {
        imgSl = findViewById(R.id.ImageSlider);
        Mode = findViewById(R.id.switchMode);
        shPref = getSharedPreferences("MODE", MODE_PRIVATE);

        shirt = findViewById(R.id.shirtCard);
        coat = findViewById(R.id.coatCard);
        cap = findViewById(R.id.capCard);
        frock = findViewById(R.id.frockCard);
        pant = findViewById(R.id.pantCard);
        shorts = findViewById(R.id.shortCard);
    }

    private void setupImageSlider() {
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.mobileecommerce, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.onlineshopping, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.coat, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.shirt, ScaleTypes.FIT));
        imgSl.setImageList(slideModels, ScaleTypes.FIT);
    }

    private void setupNightModeSwitch() {
        boolean nightMode = shPref.getBoolean("nightMode", false);

        Mode.setChecked(nightMode);

        Mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleNightMode();
            }
        });
    }

    private void toggleNightMode() {
        if (Mode.isChecked()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            edit = shPref.edit();
            edit.putBoolean("nightMode", true);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            edit = shPref.edit();
            edit.putBoolean("nightMode", false);
        }
        edit.apply();
    }

    private void setCardClickListeners() {
        // Onclicking the dashboard cards
        shirt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettingsActivity("T-Shirts");
            }
        });

        coat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettingsActivity("Coats");
            }
        });

        cap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettingsActivity("Caps");
            }
        });

        frock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettingsActivity("Frocks");
            }
        });

        pant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettingsActivity("Pants");
            }
        });

        shorts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettingsActivity("Shorts");
            }
        });
    }

    private void openSettingsActivity(String category) {
        Intent intent = new Intent(MainActivity.this, settingsActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    private void setupToolbar() {
        tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
    }

    private void setNavigationDrawer() {
        drawLay = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawLay, tb, R.string.open_nav, R.string.close_nav);

        drawLay.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void getUserDetails() {
        Intent intent = getIntent();
        String userUsername = intent.getStringExtra("name");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("profile");
        Query checkUserDatabase = reference.orderByChild("name").equalTo(userUsername);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    nameDB = snapshot.child(userUsername).child("name").getValue(String.class);
                    emailDB = snapshot.child(userUsername).child("email").getValue(String.class);
                    contactDB = snapshot.child(userUsername).child("contact").getValue(String.class);
                    birthdateDB = snapshot.child(userUsername).child("birthdate").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }

    private void handleSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            navView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                // Handle the action for the Home menu item
                // Update Userinterface based on the requirements
                break;
            case R.id.nav_about:
                openProfileActivity();
                break;
            case R.id.nav_settings:
                openAboutActivity();
                break;
            case R.id.nav_share:
                shareApp();
                break;
            case R.id.nav_cart:
                openViewCartActivity();
                break;
            case R.id.nav_logout:
                handleLogout();
                break;
        }
        drawLay.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openProfileActivity() {
        Intent intent = new Intent(this, Myprofile.class);
        intent.putExtra("name", nameDB);
        intent.putExtra("email", emailDB);
        intent.putExtra("contact", contactDB);
        intent.putExtra("birthdate", birthdateDB);
        startActivity(intent);
    }

    private void openAboutActivity() {
        Intent intent = new Intent(this, About.class);
        intent.putExtra("name", nameDB);
        startActivity(intent);
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this amazing app!");
        startActivity(Intent.createChooser(shareIntent, "Share using"));
    }

    private void openViewCartActivity() {
        Intent intent = new Intent(this, ViewCart.class);
        intent.putExtra("name", nameDB);
        startActivity(intent);
    }

    private void handleLogout() {
        Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();

        // Clear session

        sess.setLoggedIn(false);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawLay.isDrawerOpen(GravityCompat.START)) {
            drawLay.closeDrawer(GravityCompat.START);
        } else {
            // Exit the app
            finishAffinity();
        }
    }
}
