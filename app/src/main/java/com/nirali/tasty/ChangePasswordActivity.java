package com.nirali.tasty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText edt_email,edt_email1,edt_email2;
    Button btn_forgot,btn_reset;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edt_email = findViewById(R.id.edt_email);
        edt_email1 = findViewById(R.id.edt_email1);
        edt_email2 = findViewById(R.id.edt_email2);
        btn_forgot = findViewById(R.id.btn_forgot);
        btn_reset = findViewById(R.id.btn_reset);

        btn_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        btn_reset.setOnClickListener(v -> {
            String currentPassword = edt_email.getText().toString().trim();
            String newPassword = edt_email1.getText().toString().trim();
            String confirmPassword = edt_email2.getText().toString().trim();

            // Input Validation
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Oops! Your passwords don't match. Please re-enter them.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if user is logged in
            if (user != null && user.getEmail() != null) {
                // Re-authenticate user with current password
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update the password
                        user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                // Optionally redirect user to another activity
                            } else {
                                Toast.makeText(this, "Error: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "The current password you entered is incorrect. Please try again." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


