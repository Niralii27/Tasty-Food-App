package com.nirali.tasty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class admin_product_details extends AppCompatActivity {

    private TextView name1,description1,price1;

    private ImageView categoryImage;
    private Switch switch2;
    private String categoryId;

    private Button btnUpdate,btnDelete;
    private DatabaseReference categoryReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_details);

        name1 = findViewById(R.id.name);
        description1 = findViewById(R.id.description);
        price1 = findViewById(R.id.price);
        categoryImage = findViewById(R.id.categoryImage);
        switch2 = findViewById(R.id.switch2);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryReference.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(admin_product_details.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    } else {
                        Toast.makeText(admin_product_details.this, "Failed to delete Product: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Get the passed categoryId
        categoryId = getIntent().getStringExtra("productId");


        if (categoryId == null) {
            // Handle error if productId is null
            Log.e("ProductDetails", "No product ID passed to the activity");
            Toast.makeText(this, "Invalid product ID", Toast.LENGTH_SHORT).show();
            finish(); // Optionally finish the activity if no valid ID
            return;
        }

        // Reference to Firebase
        categoryReference = FirebaseDatabase.getInstance().getReference("Product").child(categoryId);

        // Fetch category details from Firebase
        fetchCategoryDetails();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCategoryStatus();
            }
        });
    }

    private void fetchCategoryDetails() {
        categoryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("CategoryDetails", "Snapshot: " + snapshot.toString());

                    // Fetch fields
                    String name = snapshot.child("name").getValue(String.class);
                    Boolean status = snapshot.child("active").getValue(Boolean.class);  // Ensure it's a Boolean in Firebase
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String price = snapshot.child("price").getValue(String.class);


                    // Log values for debugging
                    Log.d("CategoryDetails", "Name: " + name);
                    Log.d("CategoryDetails", "Status: " + status);
                    Log.d("CategoryDetails", "Image URL: " + imageUrl);

                    // Update UI
                    name1.setText(name);
                    description1.setText(description);
                    price1.setText(price);


                    // Set the status of the switch
                    if (status != null) {
                        switch2.setChecked(status);  // This will update the switch based on the Boolean value
                    }

                    // Load the image using Glide
                    Glide.with(admin_product_details.this)
                            .load(imageUrl)
//                            .placeholder(R.drawable.order)
                            .into(categoryImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });
    }

    private void updateCategoryStatus() {
        // Get the current status from the switch
        boolean updatedStatus = switch2.isChecked();

        // Update the status in Firebase
        categoryReference.child("active").setValue(updatedStatus)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Show toast if update was successful
                        Toast.makeText(admin_product_details.this, "Item updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle failure
                        Toast.makeText(admin_product_details.this, "Failed to update item", Toast.LENGTH_SHORT).show();
                    }
                });
        // Optionally, log the update
        Log.d("CategoryDetails", "Category status updated to: " + updatedStatus);

        // Optionally, show a success message or handle the UI after update
    }
}