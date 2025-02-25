package com.nirali.tasty;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AdminFeedbackFragment extends Fragment {

    private RecyclerView recyclerView;
    private FeedbackAdapter adapter;
    private ArrayList<FeedbackItem> feedbackList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_feedback, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        feedbackList = new ArrayList<>();
        adapter = new FeedbackAdapter(feedbackList);
        recyclerView.setAdapter(adapter);

        fetchFeedback();


        return view;
    }

    private void fetchFeedback() {
        DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("Feedback");
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product");

        feedbackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot feedbackSnapshot) {
                feedbackList.clear();
                for (DataSnapshot feedbackData : feedbackSnapshot.getChildren()) {
                    // Fetch itemID and convert dynamically
                    Object itemIDObj = feedbackData.child("itemID").getValue();
                    String itemID = itemIDObj != null ? itemIDObj.toString() : null;

                    // Fetch userID and review dynamically
                    String userID = feedbackData.child("userID").getValue(String.class);
                    Object reviewObj = feedbackData.child("review").getValue();
                    String review = reviewObj != null ? reviewObj.toString() : null;

                    // Skip if critical fields are missing
                    if (itemID == null || userID == null || review == null) {
                        Log.e("FeedbackError", "Missing data for feedback entry: " + feedbackData.getKey());
                        continue;
                    }

                    // Fetch product details
                    productRef.child(itemID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot productSnapshot) {
                            String itemName = productSnapshot.child("name").getValue(String.class);
                            String itemImage = productSnapshot.child("imageUrl").getValue(String.class);

                            if (itemName == null || itemImage == null) {
                                Log.e("ProductError", "Product not found or missing data for itemID: " + itemID);
                                return;
                            }

                            // Add feedback item to the list
                            feedbackList.add(new FeedbackItem(itemID, itemName, itemImage, userID, review));
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("FirebaseError", "Failed to fetch product data for itemID: " + itemID, error.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch feedback data", error.toException());
            }
        });
    }




    public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

        private ArrayList<FeedbackItem> feedbackList;

        public FeedbackAdapter(ArrayList<FeedbackItem> feedbackList) {
            this.feedbackList = feedbackList;
        }

        @NonNull
        @Override
        public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_admin_feedback, parent, false);
            return new FeedbackViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
            FeedbackItem item = feedbackList.get(position);
            holder.itemname.setText("Product Name: " + item.getItemName());
            holder.username.setText("UserID: " + item.getUsername());
            holder.review.setText("Rating: " + item.getReview());
            Glide.with(holder.itemView.getContext()).load(item.getItemImage()).into(holder.img1);
        }

        @Override
        public int getItemCount() {
            return feedbackList.size();
        }

        public class FeedbackViewHolder extends RecyclerView.ViewHolder {
            TextView itemname, username, review;
            ImageView img1;

            public FeedbackViewHolder(@NonNull View itemView) {
                super(itemView);
                itemname = itemView.findViewById(R.id.itemname);
                username = itemView.findViewById(R.id.username);
                review = itemView.findViewById(R.id.review);
                img1 = itemView.findViewById(R.id.img1);
            }
        }
    }
}