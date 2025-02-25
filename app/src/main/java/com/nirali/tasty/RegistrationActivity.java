package com.nirali.tasty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashSet;
import java.util.Set;


public class RegistrationActivity extends AppCompatActivity {

    Button btn_sign_up;
    EditText edt_name,edt_email,edt_password;
    TextView txt_login1;
    FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            // If the user is logged in, go to MainActivity
//            Intent intent = new Intent(RegistrationActivity.this, MainActivity2.class);
//            startActivity(intent);
//            finish(); // Close the registration activity
//            return;
//        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail(); // Get the logged-in user's email

            // Check if the user is the admin based on email
            if (email != null && email.equals("admin@gmail.com")) {
                // Redirect to AdminHomeActivity
                Intent intent = new Intent(RegistrationActivity.this, AdminHomeActivity.class);
                startActivity(intent);
                finish(); // Close the registration activity
            } else {
                // Redirect to MainActivity for normal users
                Intent intent = new Intent(RegistrationActivity.this, MainActivity2.class);
                startActivity(intent);
                finish(); // Close the registration activity
            }
        }


////        // Check if the user is already logged in
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            navigateToHome();
//            return; // Exit the registration activity as the user is logged in
//        }

        mAuth = FirebaseAuth.getInstance();
        btn_sign_up = findViewById(R.id.btn_sign_up);
        edt_email = findViewById(R.id.edt_email);
        edt_name = findViewById(R.id.edt_name);
        edt_password = findViewById(R.id.edt_password);
        txt_login1 = findViewById(R.id.txt_login1);

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email,password,name;
                email = String.valueOf(edt_email.getText());
                password = String.valueOf(edt_password.getText());
                name = String.valueOf(edt_name.getText());

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(RegistrationActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegistrationActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegistrationActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(RegistrationActivity.this, "Successfully registered.", Toast.LENGTH_SHORT).show();

                                    // Get the current user
                                    FirebaseUser user = mAuth.getCurrentUser();



                                    // Update the user profile with the name
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();

                                    if (user != null) {
                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // Name updated, redirect to Login or MainActivity
                                                            Intent intent = new Intent(RegistrationActivity.this, Login.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegistrationActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });





            }
        });

        txt_login1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this,Login.class);
                startActivity(intent);
            }
        });
    }

//    private void navigateToHome() {
//        Intent intent = new Intent(RegistrationActivity.this, MainActivity2.class); // Replace with your home activity
//        startActivity(intent);
//        finish(); // Finish this activity to prevent returning to it
//    }



//private void navigateToHome() {
//    FirebaseUser currentUser = mAuth.getCurrentUser(); // Get the current logged-in user
//    if (currentUser != null) {
//        String userId = currentUser.getUid(); // Get the userId
//
//        Intent intent = new Intent(RegistrationActivity.this, MainActivity2.class);
//        intent.putExtra("userId", userId); // Pass the userId to MainActivity2
//        startActivity(intent);
//        finish(); // Finish this activity to prevent returning to it
//    }
//}


}