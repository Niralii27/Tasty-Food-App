package com.nirali.tasty;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ContactUsActivity extends AppCompatActivity {

    EditText name,email,number,address,message;
    Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        number = findViewById(R.id.number);
        address = findViewById(R.id.address);
        message = findViewById(R.id.message);
        btn_submit = findViewById(R.id.btn_Submit);

        btn_submit.setOnClickListener(v -> saveDataToFirebase());

    }

    private void saveDataToFirebase() {
        String nameText = name.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String numberText = number.getText().toString().trim();
        String addressText = address.getText().toString().trim();
        String messageText = message.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(nameText) || TextUtils.isEmpty(emailText) ||
                TextUtils.isEmpty(numberText) || TextUtils.isEmpty(addressText) ||
                TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reference to Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference contactRef = database.getReference("Contacts");

        // Create a unique ID for each contact
        String contactId = contactRef.push().getKey();

        // Store data in a HashMap
        HashMap<String, String> contactData = new HashMap<>();
        contactData.put("name", nameText);
        contactData.put("email", emailText);
        contactData.put("number", numberText);
        contactData.put("address", addressText);
        contactData.put("message", messageText);

        // Insert data into Firebase
        if (contactId != null) {
            contactRef.child(contactId).setValue(contactData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "We’ve received your details. Our team will get back to you soon!", Toast.LENGTH_SHORT).show();
                        clearFields();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void clearFields() {
        name.setText("");
        email.setText("");
        number.setText("");
        address.setText("");
        message.setText("");
    }
}