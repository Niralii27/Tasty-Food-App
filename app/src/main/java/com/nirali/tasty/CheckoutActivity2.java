package com.nirali.tasty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CheckoutActivity2 extends AppCompatActivity {

    private String selectedPaymentMethod;

    RadioButton radio_paypal, radio_credit_card, radio_apple_pay, radio_google_pay, radio_webmoney;
    Button btn_add_payment;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout2);

        btn_add_payment = findViewById(R.id.btn_add_payment);
        radio_paypal = findViewById(R.id.radio_paypal);
        radio_credit_card = findViewById(R.id.radio_credit_card);
        radio_apple_pay = findViewById(R.id.radio_apple_pay);
        radio_google_pay = findViewById(R.id.radio_google_pay);
        radio_webmoney = findViewById(R.id.radio_webmoney);

        // Retrieve cartItems, item IDs, names, quantities, total amount, and address
        ArrayList<String> itemIDs = getIntent().getStringArrayListExtra("itemIDs");
        ArrayList<String> itemNames = getIntent().getStringArrayListExtra("itemNames");
        ArrayList<Integer> quantities = getIntent().getIntegerArrayListExtra("quantities");
        float totalAmount = getIntent().getFloatExtra("totalAmount", 0);
        String address = getIntent().getStringExtra("address");

        Log.d("CheckoutActivity2", "Received itemIDs: " + itemIDs);
        Log.d("CheckoutActivity2", "Received itemNames: " + itemNames);
        Log.d("CheckoutActivity2", "Received quantities: " + quantities);
        Log.d("CheckoutActivity2", "Received totalAmount: " + totalAmount);
        Log.d("CheckoutActivity2", "Received address: " + address);

        radio_paypal.setOnClickListener(v -> selectedPaymentMethod = "PayPal Card");
        radio_credit_card.setOnClickListener(v -> selectedPaymentMethod = "Credit Card");
        radio_apple_pay.setOnClickListener(v -> selectedPaymentMethod = "Apple Pay");
        radio_google_pay.setOnClickListener(v -> selectedPaymentMethod = "Google Pay");
        radio_webmoney.setOnClickListener(v -> selectedPaymentMethod = "Cash on delivery");

        databaseReference = FirebaseDatabase.getInstance().getReference("Orders");

        btn_add_payment.setOnClickListener(v -> {
            if (selectedPaymentMethod == null) {
                Toast.makeText(CheckoutActivity2.this, "Please select a payment method.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (itemIDs == null || itemNames == null || quantities == null || address == null) {
                Toast.makeText(CheckoutActivity2.this, "Missing required order details. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(this, "User not logged in. Please log in.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                finish();
                return;
            }

            String userId = currentUser.getUid(); // Get UID of logged-in user
            String orderID = "ORD" + System.currentTimeMillis();
            String trackingNumber = "TRK" + System.currentTimeMillis();  // Generate unique tracking number

            Map<String, Object> orderData = new HashMap<>();
            orderData.put("orderID", orderID);
            orderData.put("userID", userId); // Include the UID
            orderData.put("itemIDs", itemIDs);
            orderData.put("itemNames", itemNames);
            orderData.put("quantities", quantities);
            orderData.put("totalAmount", totalAmount);
            orderData.put("address", address);
            orderData.put("paymentMethod", selectedPaymentMethod);
            orderData.put("orderDate", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
            orderData.put("status", "prepared");
            orderData.put("trackingNumber", trackingNumber);  // Add tracking number to the order

            databaseReference.child(orderID).setValue(orderData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(CheckoutActivity2.this, "Order placed successfully. Tracking Number: " + trackingNumber, Toast.LENGTH_SHORT).show();

                        // Start CheckoutActivity3 and pass data via Intent
                        Intent intent = new Intent(CheckoutActivity2.this, CheckoutActivity3.class);
                        intent.putExtra("orderID", orderID);  // Passing orderID
                        intent.putExtra("trackingNumber", trackingNumber);  // Passing trackingNumber

                        intent.putExtra("totalAmount", totalAmount);
                        intent.putExtra("selectedAddress", address);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CheckoutActivity2", "Error saving order: " + e.getMessage());
                        Toast.makeText(CheckoutActivity2.this, "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show();
                    });
        });

    }
}
