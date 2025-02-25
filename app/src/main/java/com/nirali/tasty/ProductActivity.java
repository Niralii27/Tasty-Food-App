//package com.nirali.tasty;
//
//import static java.security.AccessController.getContext;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentContainer;
//import androidx.fragment.app.FragmentTransaction;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//
//public class ProductActivity extends AppCompatActivity {
//
//    RecyclerView recycler3;
//
//    ArrayList<Product> categoryList = new ArrayList<>();
//
//
//    DatabaseReference databaseReference;
//
//    String categoryId, userId;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_product);
//
//
//        recycler3 = findViewById(R.id.recycler3);
//        RecyclerView.LayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
//        recycler3.setLayoutManager(linearLayoutManager);
//
//        categoryId = getIntent().getStringExtra("categoryId");
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            userId = currentUser.getUid();  // Get the current user's UID
//            Log.d("ProductActivity", "Authenticated User ID: " + userId);
//        } else {
//            // If user is not authenticated, set a fallback userId
//            userId = getIntent().getStringExtra("user_id");
//            if (userId == null) {
//                userId = "DefaultUserId";  // Handle case if no userId is provided
//            }
//            Log.d("ProductActivity", "No authenticated user found, using user_id from Intent: " + userId);
//        }
//
//
//
//        databaseReference = FirebaseDatabase.getInstance().getReference("Product");
//
//
//
//        fetchCategories(categoryId);
//    }
//
//    private void fetchCategories(String categoryId) {
//        databaseReference.orderByChild("categoryId").equalTo(categoryId)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        categoryList.clear(); // Clear the list before adding new data
//
//                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                            Product product = dataSnapshot.getValue(Product.class);
//
//                            String productId = dataSnapshot.getKey();  // This is the Firebase auto-generated ID
//                            product.setProductID(productId);
//                            categoryList.add(product); // Add category to list
//                        }
//
//                        // Set adapter after data is fetched
//                        ProductActivity.MyAdapter adapter = new ProductActivity.MyAdapter();
//                        recycler3.setAdapter(adapter);
//
//
//                        adapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // Handle database errors here if needed
//                    }
//                });
//    }
//
//    class MyAdapter extends RecyclerView.Adapter<ProductActivity.MyAdapter.MyHolder> {
//        @NonNull
//        @Override
//        public ProductActivity.MyAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//            LayoutInflater inflater = getLayoutInflater();
//            View view = inflater.inflate(R.layout.product_item, parent, false);
//            ProductActivity.MyAdapter.MyHolder myHolder = new ProductActivity.MyAdapter.MyHolder(view);
//            return myHolder;
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull ProductActivity.MyAdapter.MyHolder myHolder, int position) {
//
//            Product model = categoryList.get(position);
////          myHolder.cat_id.setText(model.cat_id + "");
////          myHolder.cat_status.setText(model.cat_status + "");
//            myHolder.cat_name.setText(model.name);
//            myHolder.price.setText(model.price);
//            myHolder.description.setText(model.description);
//
////          myHolder.created_date.setText(model.created_date);
//            Glide.with(myHolder.itemView)
//                    .load(model.imageUrl)
//                    .into(myHolder.cat_image);
//
//
//            myHolder.relative_main.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(ProductActivity.this, ProductDetail.class);
//                    intent.putExtra("productID", model.getProductID()); // Use getProductID() to fetch the productID
//                    intent.putExtra("productName", model.getName());
//                    intent.putExtra("productPrice", model.getPrice());
//                    intent.putExtra("productDescription", model.getDescription());
//                    intent.putExtra("productImageUrl", model.getImageUrl());
//                    intent.putExtra("user_id", userId);
//
//                    startActivity(intent);
//                }
//            });
//
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return categoryList.size();
//        }
//
//
//        class MyHolder extends RecyclerView.ViewHolder {
//
//            TextView cat_id, cat_status, cat_name, created_date,price,description;
//            ImageView cat_image;
//            CardView relative_main;
//
//            public MyHolder(@NonNull View itemView) {
//                super(itemView);
////              cat_id = itemView.findViewById(R.id.cat_id);
//
//                cat_name = itemView.findViewById(R.id.name);
//                cat_image = itemView.findViewById(R.id.imageUrl);
//                price = itemView.findViewById(R.id.price);
//                description = itemView.findViewById(R.id.description);
//
////              cat_status = itemView.findViewById(R.id.cat_status);
////              created_date = itemView.findViewById(R.id.created_date);
//                relative_main = itemView.findViewById(R.id.relative_main);
//            }
//        }
//    }
//}


package com.nirali.tasty;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    RecyclerView recycler3;

    ArrayList<Product> categoryList = new ArrayList<>();


    DatabaseReference databaseReference;

    String categoryId, userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);


        recycler3 = findViewById(R.id.recycler3);
        RecyclerView.LayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recycler3.setLayoutManager(linearLayoutManager);

        categoryId = getIntent().getStringExtra("categoryId");
        userId = getIntent().getStringExtra("user_id");


        databaseReference = FirebaseDatabase.getInstance().getReference("Product");



        fetchCategories(categoryId);
    }

    private void fetchCategories(String categoryId) {
        databaseReference.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        categoryList.clear(); // Clear the list before adding new data

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Product product = dataSnapshot.getValue(Product.class);

                            if (product != null && product.isActive()) { // Check if the product is active
                                String productId = dataSnapshot.getKey();

                                    Log.d("ProductActivity", "Product ID from Firebase: " + productId);
                                    Log.d("ProductActivity", "Product Name: " + product.getName());// Firebase auto-generated ID
                                product.setProductID(productId);
                                categoryList.add(product); // Add only active products to the list
                            }

//                            String productId = dataSnapshot.getKey();  // This is the Firebase auto-generated ID
//                            product.setProductID(productId);
//                            categoryList.add(product); // Add category to list
                        }

                        // Set adapter after data is fetched
                        ProductActivity.MyAdapter adapter = new ProductActivity.MyAdapter();
                        recycler3.setAdapter(adapter);


                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database errors here if needed
                    }
                });
    }

    class MyAdapter extends RecyclerView.Adapter<ProductActivity.MyAdapter.MyHolder> {
        @NonNull
        @Override
        public ProductActivity.MyAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.product_item, parent, false);
            ProductActivity.MyAdapter.MyHolder myHolder = new ProductActivity.MyAdapter.MyHolder(view);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ProductActivity.MyAdapter.MyHolder myHolder, int position) {

            Product model = categoryList.get(position);
//          myHolder.cat_id.setText(model.cat_id + "");
//          myHolder.cat_status.setText(model.cat_status + "");
            myHolder.cat_name.setText(model.name);
            myHolder.price.setText(model.price);
            myHolder.description.setText(model.description);

//          myHolder.created_date.setText(model.created_date);
            Glide.with(myHolder.itemView)
                    .load(model.imageUrl)
                    .into(myHolder.cat_image);


            myHolder.relative_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ProductActivity.this, ProductDetail.class);

                    intent.putExtra("productID", model.getProductID()); // Use getProductID() to fetch the productID
                    intent.putExtra("productName", model.getName());
                    intent.putExtra("productPrice", model.getPrice());
                    intent.putExtra("productDescription", model.getDescription());
                    intent.putExtra("productImageUrl", model.getImageUrl());
                    intent.putExtra("user_id", userId);
                    Log.d("ProductActivity", "User ID: " + userId);

                    startActivity(intent);
                }
            });


        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }


        class MyHolder extends RecyclerView.ViewHolder {

            TextView cat_id, cat_status, cat_name, created_date,price,description;
            ImageView cat_image;
            CardView relative_main;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
//              cat_id = itemView.findViewById(R.id.cat_id);

                cat_name = itemView.findViewById(R.id.name);
                cat_image = itemView.findViewById(R.id.imageUrl);
                price = itemView.findViewById(R.id.price);
                description = itemView.findViewById(R.id.description);

//              cat_status = itemView.findViewById(R.id.cat_status);
//              created_date = itemView.findViewById(R.id.created_date);
                relative_main = itemView.findViewById(R.id.relative_main);
            }
        }
    }
}
