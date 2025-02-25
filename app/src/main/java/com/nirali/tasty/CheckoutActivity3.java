package com.nirali.tasty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CheckoutActivity3 extends AppCompatActivity {

    private DatabaseReference ordersRef;
    private TextView orderNumber;
    private String orderID;
    private String trackingNumber;
    Button btn_track_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkouut3);

        orderNumber = findViewById(R.id.order_number);
        btn_track_order = findViewById(R.id.btn_track_order);



        // Initialize Firebase reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ordersRef = database.getReference("Orders");

        // Retrieve data passed from CheckoutActivity2
        orderID = getIntent().getStringExtra("orderID");
        trackingNumber = getIntent().getStringExtra("trackingNumber");

        btn_track_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CheckoutActivity3", "Passing orderID: " + orderID);
                Log.d("CheckoutActivity3", "Passing trackingNumber: " + trackingNumber);
                Intent intent = new Intent(getApplicationContext(), TrackorderActivity.class);
                intent.putExtra("orderID", orderID); // Pass the orderID
                intent.putExtra("trackingNumber", trackingNumber);
                startActivity(intent);
            }
        });

        ArrayList<CartItem> cartItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("cartItems");
        float totalAmount = getIntent().getFloatExtra("totalAmount", 0.0f);
        String selectedAddress = getIntent().getStringExtra("selectedAddress");

        // Log data to debug missing intent data
        Log.d("CheckoutActivity3", "Received orderID: " + orderID);
        Log.d("CheckoutActivity3", "Received cartItems: " + cartItems);
        Log.d("CheckoutActivity3", "Received totalAmount: " + totalAmount);
        Log.d("CheckoutActivity3", "Received selectedAddress: " + selectedAddress);

        // Check if data is missing
        if (orderID == null || trackingNumber == null) {
            Toast.makeText(this, "Error displaying order details.", Toast.LENGTH_LONG).show();
            return;
        }

// Display order and tracking numbers to the user
        orderNumber.setText("Order Number: " + orderID + "\nTracking Number: " + trackingNumber);


    }


}
