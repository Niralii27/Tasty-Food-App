package com.nirali.tasty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.database.ServerValue;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class CategoryActivity extends AppCompatActivity {

    EditText editName,editImageUrl,edt_name,productprice,productimg,productdescription,sliderImageUrl;
    Switch switch2,switch3, sliderImageStatus;
    Button btn_Add,product_add,btnAddSliderImage;

    Spinner spinnerCategory;

    ArrayList<String> categoryList = new ArrayList<>();
    ArrayAdapter<String> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        editName = findViewById(R.id.editName);
        editImageUrl = findViewById(R.id.editImageUrl);
        edt_name = findViewById(R.id.edt_name);
        productimg = findViewById(R.id.productimg);
        productprice = findViewById(R.id.productprice);
        productdescription = findViewById(R.id.productdescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        product_add = findViewById(R.id.product_add);

        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);
        btn_Add = findViewById(R.id.btn_Add);

        sliderImageUrl = findViewById(R.id.sliderImageUrl);
        sliderImageStatus = findViewById(R.id.sliderImageStatus);
        btnAddSliderImage = findViewById(R.id.btnAddSliderImage);


        categoryAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Collections.singletonList(categoryList));
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        spinnerCategory.setAdapter(categoryAdapter);

        loadCategories();

        btn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertdata();
            }
        });

        // Add Product Listener
        product_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertProduct();
            }
        });
        btnAddSliderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSliderImage();
            }
        });

    }

    private void loadCategories() {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("Category");
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String categoryName = categorySnapshot.child("name").getValue(String.class);
                    categoryList.add(categoryName);
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CategoryActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private  void insertdata(){
        Map<String,Object> map = new HashMap<>();
        map.put("name", editName.getText().toString());
        map.put("iurl",editImageUrl.getText().toString());
        map.put("active", switch2.isChecked());
      //  map.put("createdAt", ServerValue.TIMESTAMP);
        map.put("createdAt", System.currentTimeMillis());


        map.put("createdAt", System.currentTimeMillis());

        FirebaseDatabase.getInstance().getReference().child("Category").push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CategoryActivity.this, "Data Inserted", Toast.LENGTH_SHORT).show();

                editName.setText("");
                editImageUrl.setText("");
                switch2.setChecked(false);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure( Exception e) {
                        Toast.makeText(CategoryActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void insertProduct() {
        String name = edt_name.getText().toString();
        String price = productprice.getText().toString();
        String imageUrl = productimg.getText().toString();
        String description = productdescription.getText().toString();
        String categoryName = spinnerCategory.getSelectedItem().toString();
        boolean isActive = switch3.isChecked();

        if (name.isEmpty() || price.isEmpty() || imageUrl.isEmpty() || description.isEmpty() || categoryName.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get reference to Category node in Firebase
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("Category");

        // Query Firebase to get the category ID based on the selected category name
        categoryRef.orderByChild("name").equalTo(categoryName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get the first category with the matching name
                    for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                        String categoryId = categorySnapshot.getKey(); // This is the category ID

                        // Create a product map with all product details
                        Map<String, Object> productMap = new HashMap<>();
                        productMap.put("name", name);
                        productMap.put("price", price);
                        productMap.put("imageUrl", imageUrl);
                        productMap.put("description", description);
                        productMap.put("category", categoryName);
                        productMap.put("categoryId", categoryId); // Add the category ID to the product map
                        productMap.put("active", isActive);
                        productMap.put("createdAt", System.currentTimeMillis());

                        // Insert product into Firebase
                        FirebaseDatabase.getInstance().getReference().child("Product").push()
                                .setValue(productMap)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(CategoryActivity.this, "Product Inserted", Toast.LENGTH_SHORT).show();
                                    edt_name.setText("");
                                    productprice.setText("");
                                    productimg.setText("");
                                    productdescription.setText("");
                                    switch3.setChecked(false);
                                    spinnerCategory.setSelection(0); // Reset spinner to first item
                                })
                                .addOnFailureListener(e -> Toast.makeText(CategoryActivity.this, "Error", Toast.LENGTH_SHORT).show());
                        break; // Break after finding the first matching category
                    }
                } else {
                    Toast.makeText(CategoryActivity.this, "Category not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CategoryActivity.this, "Error retrieving category ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertSliderImage() {
        String imageUrl = sliderImageUrl.getText().toString();
        boolean isImageActive = sliderImageStatus.isChecked();

        if (imageUrl.isEmpty() ) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data for the SliderImages table
        Map<String, Object> sliderImageMap = new HashMap<>();
        sliderImageMap.put("imageUrl", imageUrl);
        sliderImageMap.put("status", isImageActive);
        sliderImageMap.put("createdAt", System.currentTimeMillis());

        // Insert into Firebase
        FirebaseDatabase.getInstance().getReference().child("SliderImages").push()
                .setValue(sliderImageMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(CategoryActivity.this, "Slider Image Added", Toast.LENGTH_SHORT).show();
                    sliderImageUrl.setText("");
                    sliderImageStatus.setChecked(false);
                })
                .addOnFailureListener(e -> Toast.makeText(CategoryActivity.this, "Error Adding Slider Image", Toast.LENGTH_SHORT).show());
    }


}