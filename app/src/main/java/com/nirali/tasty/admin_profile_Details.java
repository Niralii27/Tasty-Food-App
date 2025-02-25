package com.nirali.tasty;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class admin_profile_Details extends AppCompatActivity {

    ImageView categoryImage;
    EditText name,email,number,birthDate;
    Button btnChooseImage,btnUpdate,btnDelete;

    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile_details);

        String userId = getIntent().getStringExtra("userId"); // Assuming userId is passed from previous activity
        databaseReference = FirebaseDatabase.getInstance().getReference("Profile").child(userId);


        categoryImage = findViewById(R.id.categoryImage);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        number = findViewById(R.id.number);
        birthDate = findViewById(R.id.birthDate);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(v -> {
            databaseReference.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(admin_profile_Details.this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                } else {
                    Toast.makeText(admin_profile_Details.this, "Failed to delete profile: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });


        String userName = getIntent().getStringExtra("userName");
        String userEmail = getIntent().getStringExtra("userEmail");
        String usernumber = getIntent().getStringExtra("usernumber");
        String userBirthDate = getIntent().getStringExtra("userBirthDate");
        String profilePictureUrl = getIntent().getStringExtra("userProfilePicture");

        name.setText(userName);
        email.setText(userEmail);
        number.setText(usernumber);
        birthDate.setText(userBirthDate);
        Glide.with(this).load(profilePictureUrl).into(categoryImage);


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = name.getText().toString().trim();
                String updatedEmail = email.getText().toString().trim();
                String updatedNumber = number.getText().toString().trim();
                String updatedBirthDate = birthDate.getText().toString().trim();

                HashMap<String, Object> updates = new HashMap<>();
                updates.put("name", updatedName);
                updates.put("email", updatedEmail);
                updates.put("number", updatedNumber);
                updates.put("birthDate", updatedBirthDate);


                databaseReference.updateChildren(updates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update successful
                        Toast.makeText(admin_profile_Details.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Update failed
                        Toast.makeText(admin_profile_Details.this, "Failed to update profile: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}