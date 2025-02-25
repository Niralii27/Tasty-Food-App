package com.nirali.tasty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<OrderItem> orderList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_order, container, false);

        recyclerView = view.findViewById(R.id.recycler2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList);
        recyclerView.setAdapter(orderAdapter);

        fetchOrdersFromFirebase();
        return view;
    }

    private void fetchOrdersFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Orders");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear(); // Clear existing data
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    try {
                        // Safely retrieve order data, check for nulls
                        OrderItem order = orderSnapshot.getValue(OrderItem.class);
                        if (order != null) {
                            // Add checks for missing or invalid data
                            if (order.getOrderID() == null || order.getQuantities() == null) {
                                Log.e("AdminOrderFragment", "Missing data for order: " + orderSnapshot.getKey());
                                continue; // Skip invalid order
                            }

                            Log.d("AdminOrderFragment", "Order ID: " + order.getOrderID());
                            Log.d("AdminOrderFragment", "Amount: " + order.getTotalAmount());
                            Log.d("AdminOrderFragment", "Quantities: " + order.getQuantities());
                            Log.d("AdminOrderFragment", "Order loaded: " + order.getOrderID());
                            orderList.add(order); // Add valid order to the list
                        }
                    } catch (Exception e) {
                        Log.e("AdminOrderFragment", "Error parsing order data: " + e.getMessage());
                    }
                }
                orderAdapter.notifyDataSetChanged(); // Update the RecyclerView
                Log.d("AdminOrderFragment", "RecyclerView updated. Items: " + orderList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load orders.", Toast.LENGTH_SHORT).show();
            }
        });




}

    public static class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
        private final List<OrderItem> orderList;

        public OrderAdapter(List<OrderItem> orderList) {
            this.orderList = orderList;
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adminorder_item, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            OrderItem order = orderList.get(position);

            holder.orderId.setText("Order ID: " + order.getOrderID());
            holder.orderDate.setText("Order Date: " + order.getOrderDate());
            holder.userId.setText("User ID: " + order.getUserID());
            holder.productName.setText("Items: " + (order.getItemNames() != null ? order.getItemNames().toString() : "Not Available"));
            holder.deliveryStatus.setText("Status: " + order.getStatus());
            holder.payment.setText("Payment: "+ order.getPaymentMethod());
            holder.quantity.setText("Quantity: " +order.getQuantities());
            holder.amount.setText("Total Amount: " +order.getTotalAmount());
            holder.address.setText("Address: " +order.getAddress());
            // Assuming we only want the first item in the list for the product image
            List<String> itemNames = order.getItemNames(); // This is a List<String>
            if (itemNames != null && !itemNames.isEmpty()) {
                fetchProductImage(itemNames.get(0), holder.productImage); // Fetch image for the first item
            } else {
                // Handle case where item list is empty or null
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.order)  // Default error image
                        .into(holder.productImage);
            }

            Log.d("AdminOrderAdapter", "Bound order ID: " + order.getOrderID());

            holder.btn_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, AdminOrderDetailsActivity.class);

                    // Pass order details as extras
                    intent.putExtra("orderId", order.getOrderID());
                    intent.putExtra("orderDate", order.getOrderDate());
                    intent.putExtra("userId", order.getUserID());
                    intent.putExtra("itemNames", new ArrayList<>(order.getItemNames())); // Assuming it's a List<String>
                    intent.putExtra("deliveryStatus", order.getStatus());
                    intent.putExtra("quantities", new ArrayList<>(order.getQuantities())); // Assuming it's a List<Integer>
                    intent.putExtra("amount", String.valueOf(order.getTotalAmount())); // Convert to string if needed
                    intent.putExtra("address", order.getAddress());
                    intent.putExtra("payment", order.getPaymentMethod());

                    context.startActivity(intent);
                }
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
                        // Get the first product from the query result
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            String imageUrl = productSnapshot.child("imageUrl").getValue(String.class);
                            if (imageUrl != null) {
                                // Load the product image using Glide
                                Glide.with(productImageView.getContext())
                                        .load(imageUrl)
                                        .placeholder(R.drawable.profile)
                                        .error(R.drawable.order)
                                        .into(productImageView);
                            } else {
                                // Handle the case where image URL is missing or invalid
                                Glide.with(productImageView.getContext())
                                        .load(R.drawable.order)  // Default error image
                                        .into(productImageView);
                            }
                        }
                    } else {
                        // Handle the case where no product matches the item name
                        Log.e("AdminOrderFragment", "No product found for item: " + itemName);
                        Glide.with(productImageView.getContext())
                                .load(R.drawable.order)  // Default error image
                                .into(productImageView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle any errors while fetching data
                    Log.e("AdminOrderFragment", "Error fetching product image: " + error.getMessage());
                }
            });
        }




        @Override
        public int getItemCount() {
            return orderList.size();
        }

        public static class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView orderId, orderDate, userId, productName, deliveryStatus,quantity,amount,address,payment;
            ImageView productImage;

            Button btn_view;

            public OrderViewHolder(@NonNull View itemView) {
                super(itemView);
                orderId = itemView.findViewById(R.id.orderId);
                orderDate = itemView.findViewById(R.id.order_date);
                userId = itemView.findViewById(R.id.userId);
                productName = itemView.findViewById(R.id.product_name);
                deliveryStatus = itemView.findViewById(R.id.delivery_status);
                productImage = itemView.findViewById(R.id.product_image);
                quantity = itemView.findViewById(R.id.quantity);
                amount = itemView.findViewById(R.id.amount);
                address = itemView.findViewById(R.id.address);
                payment = itemView.findViewById(R.id.payment);
                btn_view = itemView.findViewById(R.id.btn_view);
            }
        }
    }
}
