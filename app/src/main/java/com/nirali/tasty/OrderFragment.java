package com.nirali.tasty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<OrderItem> orderList;

    LinearLayout linear1,linear2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        recyclerView = view.findViewById(R.id.recycler1);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        linear1 = view.findViewById(R.id.linear1);
        linear2 = view.findViewById(R.id.linear2);

        linear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new CartFragment()).commit();
            }
        });

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(getContext(), orderList);
        recyclerView.setAdapter(orderAdapter);

        fetchOrdersFromFirebase();

        return view;
    }

    private void fetchOrdersFromFirebase() {
        // Check if the user is logged in
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Log.e("OrderFragment", "No user is logged in.");
            return; // Exit if no user is logged in
        }

        // Get the logged-in user's ID
        String currentUserID = auth.getCurrentUser().getUid();

        // Reference to the 'orders' table
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Orders");

        // Query to filter orders by userID
        databaseReference.orderByChild("userID").equalTo(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderList.clear();
                        for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                            Log.d("OrderFragment", "Raw order data: " + orderSnapshot.getValue());
                            try {
                                OrderItem order = orderSnapshot.getValue(OrderItem.class);
                                if (order != null) {
                                    // Log all fields for debugging
                                    Log.d("OrderFragment", "Parsed Order: " +
                                            "Address: " + order.getAddress() +
                                            ", ItemIDs: " + order.getItemIDs() +
                                            ", ItemNames: " + order.getItemNames() +
                                            ", OrderDate: " + order.getOrderDate() +
                                            ", OrderID: " + order.getOrderID() +
                                            ", PaymentMethod: " + order.getPaymentMethod() +
                                            ", Quantities: " + order.getQuantities() +
                                            ", Status: " + order.getStatus() +
                                            ", TotalAmount: " + order.getTotalAmount() +
                                            ", TrackingNumber: " + order.getTrackingNumber() +
                                            ", UserID: " + order.getUserID());
                                    orderList.add(order);
                                } else {
                                    Log.e("OrderFragment", "OrderItem is null");
                                }
                            } catch (Exception e) {
                                Log.e("OrderFragment", "Error parsing order: " + e.getMessage());
                            }
                        }
                        orderAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load orders.", Toast.LENGTH_SHORT).show();
                    }
                });


}

    public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
        private Context context;
        private List<OrderItem> orderList;

        public OrderAdapter(Context context, List<OrderItem> orderList) {
            this.context = context;
            this.orderList = orderList;
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            OrderItem order = orderList.get(position);

            // Set order details
            holder.orderId.setText("Order ID: " + order.getOrderID());
            holder.orderDate.setText("Order Date: " + order.getOrderDate());
            holder.userId.setText("User ID: " + order.getUserID());

            // Display the first item's name and fetch its image
            if (order.getItemNames() != null && !order.getItemNames().isEmpty()) {
                String itemName = order.getItemNames().get(0); // Display the first item's name
                holder.productName.setText("Item: " + itemName);

                // Fetch product image from the database
                DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product");
                productRef.orderByChild("name").equalTo(itemName)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                                        String productImageUrl = productSnapshot.child("imageUrl").getValue(String.class);

                                        // Load the image using Glide
                                        Glide.with(context)
                                                .load(productImageUrl)
                                                .placeholder(R.drawable.profile) // Placeholder image
                                                .error(R.drawable.order)         // Error image
                                                .into(holder.productImage);
                                    }
                                } else {
                                    holder.productImage.setImageResource(R.drawable.order); // Fallback image
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                holder.productImage.setImageResource(R.drawable.order); // Fallback image
                            }
                        });
            } else {
                holder.productName.setText("Item: Not Available");
                holder.productImage.setImageResource(R.drawable.order); // Fallback image
            }

            holder.deliveryStatus.setText("Status: " + order.getStatus());

            // Add click listener to open TrackOrderActivity
            holder.linear1.setOnClickListener(v -> {
                Log.d("OrderAdapter", "Linear1 clicked! Opening TrackOrderActivity for Order ID: " + order.getOrderID());

                // Intent to navigate to TrackOrderActivity
                Intent intent = new Intent(context, TrackorderActivity.class);
                intent.putExtra("orderID", order.getOrderID());
                context.startActivity(intent);
            });

            holder.btn_feedback.setOnClickListener(v -> {
                 if (holder.currentRating == 0) {
                     Toast.makeText(context, "Please select a star rating first!", Toast.LENGTH_SHORT).show();
                 } else {
                     saveReviewToFirebase(order.getUserID(), order.getItemIDs().get(0), holder.currentRating);
            }
        });
    }
        @Override
        public int getItemCount() {
            return orderList.size();
        }

        private void saveReviewToFirebase(String userID, String itemID, int review) {
            DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("Feedback");

            String feedbackID = feedbackRef.push().getKey(); // Generate unique key
            if (feedbackID == null) return;

            Feedback feedback = new Feedback(userID, itemID, review); // Create Feedback object
            feedbackRef.child(feedbackID).setValue(feedback)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to submit review.", Toast.LENGTH_SHORT).show();
                        }
                    });
    }

        public class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView orderId, orderDate, userId, productName, deliveryStatus;
            ImageView productImage,star1,star2,star3,star4,star5;
            Button btn_feedback;
            int currentRating = 0;
            LinearLayout linear1;


            public OrderViewHolder(@NonNull View itemView) {
                super(itemView);
                orderId = itemView.findViewById(R.id.orderId);
                orderDate = itemView.findViewById(R.id.order_date);
                userId = itemView.findViewById(R.id.userId);
                productName = itemView.findViewById(R.id.product_name);
                deliveryStatus = itemView.findViewById(R.id.delivery_status);
                productImage = itemView.findViewById(R.id.product_image);
                star1 = itemView.findViewById(R.id.star1);
                star2 = itemView.findViewById(R.id.star2);
                star3 = itemView.findViewById(R.id.star3);
                star4 = itemView.findViewById(R.id.star4);
                star5 = itemView.findViewById(R.id.star5);
                btn_feedback = itemView.findViewById(R.id.btn_feedback);
                linear1 = itemView.findViewById(R.id.linear1); // Initialize the linear layout

                setupStarClickListeners();


            }
            private void setupStarClickListeners() {
                View.OnClickListener starClickListener = view -> {
                    int selectedStar = Integer.parseInt(view.getTag().toString());
                    currentRating = selectedStar; // Store the selected rating
                    updateStarUI(selectedStar); // Update UI
                };

                // Assign tags and click listeners
                star1.setTag(1);
                star2.setTag(2);
                star3.setTag(3);
                star4.setTag(4);
                star5.setTag(5);

                star1.setOnClickListener(starClickListener);
                star2.setOnClickListener(starClickListener);
                star3.setOnClickListener(starClickListener);
                star4.setOnClickListener(starClickListener);
                star5.setOnClickListener(starClickListener);
            }

            private void updateStarUI(int rating) {
                // Reset all stars to unselected
                star1.setImageResource(R.drawable.star);
                star2.setImageResource(R.drawable.star);
                star3.setImageResource(R.drawable.star);
                star4.setImageResource(R.drawable.star);
                star5.setImageResource(R.drawable.star);

                // Highlight stars up to the selected rating
                if (rating <= 0) star1.setImageResource(R.drawable.star1);
                if (rating <= 1) star2.setImageResource(R.drawable.star1);
                if (rating <= 2) star3.setImageResource(R.drawable.star1);
                if (rating <= 3) star4.setImageResource(R.drawable.star1);
                if (rating <= 4) star5.setImageResource(R.drawable.star1);
            }
        }
    }

}
