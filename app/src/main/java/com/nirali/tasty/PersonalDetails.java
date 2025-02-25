package com.nirali.tasty;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;

public class PersonalDetails extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 101;

    EditText txt1, txt2, txt3, txt4; // EditText for Name, Email, Number, and BirthDate
    TextView txt_name;
    ImageView profile_img;
    Button btn_update_profile;

    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference databaseReference;

    Bitmap selectedBitmap = null; // To store the selected image as a Bitmap

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        // Initialize UI elements
        txt1 = findViewById(R.id.txt1); // User's name
        txt2 = findViewById(R.id.txt2); // User's email
        txt3 = findViewById(R.id.txt3); // User's number
        txt4 = findViewById(R.id.txt4); // User's birthDate
        txt_name = findViewById(R.id.txt_name);
        profile_img = findViewById(R.id.profile_img);
        btn_update_profile = findViewById(R.id.btn_update_profile);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Profile");

        // Load user data
        if (user != null) {
            loadUserData();
        } else {
            Log.d("PersonalDetails", "No user is currently logged in.");
        }

        // Open gallery when clicking on txt_name
        txt_name.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                requestStoragePermission();
            }
        });

        // Handle Update Profile button click
        btn_update_profile.setOnClickListener(v -> {
            if (user != null) {
                // Data fields
                String name = txt1.getText().toString();
                String email = txt2.getText().toString();
                String number = txt3.getText().toString();
                String birthDate = txt4.getText().toString();

                // HashMap to store data
                HashMap<String, Object> profileData = new HashMap<>();

                // Check if profile exists in the database
                databaseReference.child(user.getUid()).get().addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        // First-time save: Add all fields
                        profileData.put("name", name);
                        profileData.put("email", email);
                        profileData.put("number", number);
                        profileData.put("birthDate", birthDate);

                        if (selectedBitmap != null) {
                            // Compress and save the image path
                            String imagePath = saveImageToInternalStorage(selectedBitmap);
                            profileData.put("profilePicture", imagePath);
                        } else {
                            profileData.put("profilePicture", ""); // Default empty profile picture
                        }

                        // Save profile data
                        databaseReference.child(user.getUid()).setValue(profileData)
                                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show());
                    } else {
                        // Profile exists: Update only number and/or profilePicture if changed
                        boolean isUpdated = false;

                        // Check and update profile picture
                        if (selectedBitmap != null) {
                            String imagePath = saveImageToInternalStorage(selectedBitmap);
                            profileData.put("profilePicture", imagePath);
                            isUpdated = true;
                        }

                        // Check and update mobile number
                        String currentNumber = snapshot.child("number").getValue(String.class);
                        if (currentNumber == null || !currentNumber.equals(number)) {
                            profileData.put("number", number);
                            isUpdated = true;
                        }

                        // Update only if there are changes
                        if (isUpdated) {
                            databaseReference.child(user.getUid()).updateChildren(profileData)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(this, "No changes detected!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(e -> Toast.makeText(this, "Error retrieving profile data", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "No user logged in.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // For Android 13 and above
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_PERMISSION_CODE);
        } else { // For Android 12 and below
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                profile_img.setImageBitmap(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadUserData() {
        // Fetch user profile data from the "Profile" table in Firebase Realtime Database
        databaseReference.child(user.getUid()).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String name = snapshot.child("name").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String number = snapshot.child("number").getValue(String.class);
                String birthDate = snapshot.child("birthDate").getValue(String.class);
                String profilePicture = snapshot.child("profilePicture").getValue(String.class);

                txt1.setText(name);
                txt2.setText(email);
                txt3.setText(number);
                txt4.setText(birthDate);

                if (profilePicture != null && !profilePicture.isEmpty()) {
                    // Load the image from local storage
                    File imgFile = new  File(profilePicture);
                    if(imgFile.exists()){
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        profile_img.setImageBitmap(bitmap);
                    }
                }
            } else {
                // If no custom profile data is found, fall back to Firebase Authentication details
                txt1.setText(user.getDisplayName());
                txt2.setText(user.getEmail());
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load profile data", Toast.LENGTH_SHORT).show());
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        File directory = getFilesDir(); // Get the internal storage directory
        File file = new File(directory, "profile_" + System.currentTimeMillis() + ".jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos); // Compress and save the image
            return file.getAbsolutePath(); // Return the file path
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
