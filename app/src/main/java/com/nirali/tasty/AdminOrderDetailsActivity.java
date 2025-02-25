package com.nirali.tasty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminOrderDetailsActivity extends AppCompatActivity {

        Button btn_update, btn_delete;

        String receivedOrderId; // Declare globally

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin_order_details);




            // Initialize views
            btn_delete = findViewById(R.id.btn_delete);
            btn_update = findViewById(R.id.btn_update);
            EditText orderDate = findViewById(R.id.order_date);
            EditText orderId = findViewById(R.id.orderId);
            EditText userId = findViewById(R.id.userId);
            EditText productName = findViewById(R.id.product_name);
            EditText quantity = findViewById(R.id.quantity);
            EditText amount = findViewById(R.id.amount);
            EditText address = findViewById(R.id.address);
            EditText payment = findViewById(R.id.payment);
            Spinner deliveryStatusSpinner = findViewById(R.id.delivery_status_spinner);
            ImageView productImage = findViewById(R.id.product_image);



            btn_delete.setOnClickListener(v -> {
                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Orders").child(receivedOrderId);

                orderRef.removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Order Deleted Successfully", Toast.LENGTH_SHORT).show();
                            Log.d("AdminOrderDetails", "Order deleted successfully.");

                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to delete the order", Toast.LENGTH_SHORT).show();
                            Log.e("AdminOrderDetails", "Error deleting order: " + e.getMessage());
                        });
            });




            // Retrieve data from intent
            Intent intent = getIntent();
            receivedOrderId = intent.getStringExtra("orderId"); // Initialize receivedOrderId
            String receivedOrderDate = intent.getStringExtra("orderDate");
            String receivedUserId = intent.getStringExtra("userId");
            List<String> receivedItemNames = intent.getStringArrayListExtra("itemNames");
            String receivedDeliveryStatus = intent.getStringExtra("deliveryStatus");
            List<Integer> receivedQuantities = intent.getIntegerArrayListExtra("quantities");
            String receivedAmount = intent.getStringExtra("amount");
            String receivedAddress = intent.getStringExtra("address");
            String receivedPayment = intent.getStringExtra("payment");

            // Populate the UI
            orderDate.setText("Date: " + receivedOrderDate);
            orderId.setText("Order ID: " + receivedOrderId);
            userId.setText("User ID: " + receivedUserId);
            productName.setText("Product Names: " + (receivedItemNames != null ? receivedItemNames.toString() : "No Items"));
            quantity.setText("Quantity: " + receivedQuantities);
            amount.setText("Amount: ₹" + receivedAmount);
            address.setText("Address: " + receivedAddress);
            payment.setText("Payment Method: " + receivedPayment);





            // Set delivery status spinner selection
            if (receivedDeliveryStatus != null) {
                String[] options = getResources().getStringArray(R.array.delivery_status_options);
                int index = java.util.Arrays.asList(options).indexOf(receivedDeliveryStatus);
                if (index >= 0) {
                    deliveryStatusSpinner.setSelection(index);
                }
            }

            // Set image (if applicable)
            if (receivedItemNames != null && !receivedItemNames.isEmpty()) {
                fetchProductImage(receivedItemNames.get(0), productImage);
            }





            // Handle the "Update" button click
            btn_update.setOnClickListener(v -> {
                // Retrieve updated data from UI
//                String updatedOrderDate = orderDate.getText().toString();
//                String updatedUserId = userId.getText().toString();
//                String updatedItemNames = productName.getText().toString();
//                String updatedQuantity = quantity.getText().toString();
//                String updatedAmount = amount.getText().toString();
//                String updatedAddress = address.getText().toString();
//                String updatedPayment = payment.getText().toString();
                String updatedDeliveryStatus = deliveryStatusSpinner.getSelectedItem().toString();  // Get selected delivery status

                // Create a map of the updated order data
                Map<String, Object> updatedOrderData = new HashMap<>();
//                updatedOrderData.put("orderDate", updatedOrderDate);
//                updatedOrderData.put("userId", updatedUserId);
//                updatedOrderData.put("itemNames", updatedItemNames);  // This could be a list of items if needed
//                updatedOrderData.put("quantities", updatedQuantity); // Ensure correct type here (String/Integer)
//                updatedOrderData.put("amount", updatedAmount);
//                updatedOrderData.put("address", updatedAddress);
//                updatedOrderData.put("payment", updatedPayment);
                updatedOrderData.put("status", updatedDeliveryStatus);

                // Reference to the "Orders" node in Firebase, using the order ID
                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Orders").child(receivedOrderId);

                // Update the data in Firebase
                orderRef.updateChildren(updatedOrderData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Status Updated Successfully", Toast.LENGTH_SHORT).show();
                            // Notify user of success
                            Log.d("AdminOrderDetails", "Order updated successfully.");
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                            Toast.makeText(this, "An error to update Status", Toast.LENGTH_SHORT).show();
                            Log.e("AdminOrderDetails", "Error updating order: " + e.getMessage());
                        });
            });


        }





        private void fetchProductImage(String itemName, final ImageView productImageView) {
            // Reference to the "Products" node in Firebase
            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("Product");

            // Query the Products node for a product that matches the item name
            productsRef.orderByChild("name").equalTo(itemName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            String imageUrl = productSnapshot.child("imageUrl").getValue(String.class);
                            if (imageUrl != null) {
                                Glide.with(productImageView.getContext())
                                        .load(imageUrl)
                                        .placeholder(R.drawable.profile)
                                        .error(R.drawable.order)
                                        .into(productImageView);
                            } else {
                                Glide.with(productImageView.getContext())
                                        .load(R.drawable.order)  // Default error image
                                        .into(productImageView);
                            }
                        }
                    } else {
                        Log.e("AdminOrderDetails", "No product found for item: " + itemName);
                        Glide.with(productImageView.getContext())
                                .load(R.drawable.order)  // Default error image
                                .into(productImageView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("AdminOrderDetails", "Error fetching product image: " + error.getMessage());
                }
            });
        }






}
