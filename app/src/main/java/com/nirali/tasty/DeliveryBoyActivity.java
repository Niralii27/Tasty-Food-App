package com.nirali.tasty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DeliveryBoyActivity extends AppCompatActivity {

    private EditText deliveryName, deliveryNumber, deliveryImage;
    ImageView  categoryImage;
    private Switch statusSwitch;
    private Button btnInsert,btnUpdate;
    private DatabaseReference databaseReference;
    private String deliveryBoyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_boy);

        deliveryName = findViewById(R.id.DeliveryName);
        deliveryNumber = findViewById(R.id.deliverynumber);
        deliveryImage = findViewById(R.id.DeliveryImage);
        statusSwitch = findViewById(R.id.switch2);
        btnInsert = findViewById(R.id.btnInsert);
        btnUpdate = findViewById(R.id.btnUpdate);
        categoryImage = findViewById(R.id.categoryImage);

        databaseReference = FirebaseDatabase.getInstance().getReference("Delivery_Boy");

        // Get data from Intent
        Intent intent = getIntent();
        deliveryBoyId = intent.getStringExtra("id"); // Retrieve deliveryBoyId

        String name = intent.getStringExtra("name");
        String number = intent.getStringExtra("number");
        boolean status = intent.getBooleanExtra("status", false);
        String img = intent.getStringExtra("img");


        String imagePath = intent.getStringExtra("imagePath");

        if (deliveryBoyId != null) {
            deliveryName.setText(name);
            deliveryNumber.setText(number);
            deliveryImage.setText(imagePath);
            statusSwitch.setChecked(status);

            Glide.with(this)
                    .load(imagePath)
                    .placeholder(R.drawable.order)
                    .into(categoryImage);

            btnInsert.setVisibility(View.GONE); // Hide Insert button for updates
        } else {
            btnUpdate.setVisibility(View.GONE); // Hide Update button for new inserts
        }
        // Insert button click listener
        btnInsert.setOnClickListener(view -> insertDeliveryBoyData());

        // Update button click listener
        btnUpdate.setOnClickListener(view -> updateDeliveryBoyData());


        // Insert button click listener
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertDeliveryBoyData();
            }
        });


    }

    private void insertDeliveryBoyData() {
        // Get values from input fields
        String name = deliveryName.getText().toString().trim();
        String number = deliveryNumber.getText().toString().trim();
        String imagePath = deliveryImage.getText().toString().trim();
        boolean status = statusSwitch.isChecked();

        // Validation
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter the delivery boy's name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "Please enter the delivery boy's number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(imagePath)) {
            Toast.makeText(this, "Please enter the image path", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a unique ID for the delivery boy
        String id = databaseReference.push().getKey();

        // Create a delivery boy object
        DeliveryBoy deliveryBoy = new DeliveryBoy(id, name, number, imagePath, status);

        // Save to Firebase
        assert id != null;
        databaseReference.child(id).setValue(deliveryBoy).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(DeliveryBoyActivity.this, "Data inserted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DeliveryBoyActivity.this, "Failed to insert data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDeliveryBoyData() {
        String name = deliveryName.getText().toString().trim();
        String number = deliveryNumber.getText().toString().trim();
        String imagePath = deliveryImage.getText().toString().trim();
        boolean status = statusSwitch.isChecked();

        if (validateInputs(name, number, imagePath)) {
            HashMap<String, Object> updateMap = new HashMap<>();
            updateMap.put("name", name);
            updateMap.put("number", number);
            updateMap.put("imagePath", imagePath);
            updateMap.put("status", status);

            databaseReference.child(deliveryBoyId).updateChildren(updateMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private boolean validateInputs(String name, String number, String imagePath) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter the delivery boy's name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "Please enter the delivery boy's number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(imagePath)) {
            Toast.makeText(this, "Please enter the image path", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}