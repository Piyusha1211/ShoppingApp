package com.example.aifit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.Calendar;

public class DetailActivity extends AppCompatActivity {
    TextView detailDesc,detailTitle,detailCost,detailCategory;
    ImageView detailImage;
    Button cart,ar, delete;
    DatabaseReference databaseReference;

    String key ="";
    String imageUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailDesc = findViewById(R.id.detailDesc);
        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        detailCost = findViewById(R.id.detailLang);
        detailCategory=findViewById(R.id.detailCategory);
        cart=findViewById(R.id.cart);
        ar=findViewById(R.id.ar);
        delete =findViewById(R.id.Delete);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            detailDesc.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));
            detailCost.setText(bundle.getString("Cost"));
            detailCategory.setText(bundle.getString("Category"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("cart");

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dataTitle = detailTitle.getText().toString();
                String dataCost = detailCost.getText().toString();
                String dataDescription = detailDesc.getText().toString();
                String dataCategory=detailCategory.getText().toString();
                String dataImage = bundle.getString("Image");

                // Create a unique key for the item in the cart
                String key = databaseReference.push().getKey();

                // Create a new cart item object
//                CartItem cartItem = new CartItem(dataTitle, dataDescription, dataImage, dataCost);
                DataClass dataItem = new DataClass(dataTitle, dataDescription, dataCost,dataCategory,dataImage);
                String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                // Add the cart item to the database reference
                databaseReference.child(currentDate).setValue(dataItem);
                Toast.makeText(DetailActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
            }
        });

        ar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, ARactivity.class);
                String categoryName = detailCategory.getText().toString();
                if (categoryName.equals("T-Shirts")) {
                    intent.putExtra("name", "T-Shirt");
                } else if (categoryName.equals("Pants")) {
                    intent.putExtra("name", "Pant");
                }
                else if (categoryName.equals("Shorts")) {
                    intent.putExtra("name", "Shorts");
                }
                else if(categoryName.equals("Caps")){
                    intent.putExtra("name", "Caps");
                }
                else if(categoryName.equals("Coats")){
                    intent.putExtra("name", "Coat");
                }
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Android Tutorials");
                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageReference = storage.getReferenceFromUrl(String.valueOf(imageUrl));
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue();
                        Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
            }
        });


    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DetailActivity.this, AllProducts.class);
        intent.putExtra("name", "Prathiksha Kini");
        startActivity(intent);
    }
}