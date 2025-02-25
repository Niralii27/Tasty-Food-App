package com.nirali.tasty;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.slider.Slider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    RecyclerView recycler1,recycler2;

    EditText searchEditText;
    ImageView searchIcon;
    ViewPager2 viewPager2;


    ArrayList<Category> categoryList = new ArrayList<>();
    ArrayList<String> sliderImages = new ArrayList<>();
    ArrayList<Product> productList = new ArrayList<>();

    private Handler sliderHandler = new Handler(Looper.getMainLooper());

    private int currentPage = 0;

    String userId;

    DatabaseReference databaseReference;







    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);




        recycler1 = view.findViewById(R.id.recycler1);
        recycler2 = view.findViewById(R.id.recycler2);
        viewPager2 = view.findViewById(R.id.viewPager2); // Initialize ViewPager2
        searchEditText = view.findViewById(R.id.searchEditText);
        searchIcon = view.findViewById(R.id.searchIcon);



        setupSearch();



        recycler1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        recycler2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        RecyclerView.LayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
        recycler2.setLayoutManager(linearLayoutManager);


        databaseReference = FirebaseDatabase.getInstance().getReference("Category");



        // Fetch data from Firebase
        fetchCategories();
        fetchSliderImages();
        fetchTopRatedProducts(); // Fetch data for recycler2





        return view;
    }

    private void setupSearch() {
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchProducts(query);
                }
            }
        });
    }

    private void searchProducts(String query) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product");

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> searchResults = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null && product.getName() != null) {
                        // Check if the product name contains the query (case-insensitive)
                        if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                            searchResults.add(product);
                        }
                    }
                }

                // Update RecyclerView with search results
                updateProductRecycler(searchResults);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Search failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProductRecycler(List<Product> searchResults) {
        // Convert List to ArrayList
        ArrayList<Product> productArrayList = new ArrayList<>(searchResults);

        // Pass the ArrayList to the adapter
        ProductAdapter searchAdapter = new ProductAdapter(productArrayList);
        recycler2.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
    }


//    private void fetchCategories() {
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                categoryList.clear(); // Clear the list before adding new data
//
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Category category = dataSnapshot.getValue(Category.class);
//                    categoryList.add(category); // Add category to list
//                }
//
//                // Set adapter after data is fetched
//                MyAdapter adapter = new MyAdapter();
//                recycler1.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle database errors here if needed
//            }
//        });
//    }


    private void fetchCategories() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear(); // Clear the list before adding new data

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String categoryId = dataSnapshot.getKey();

                    Category category = dataSnapshot.getValue(Category.class);

                    // Check if the category is active before adding it to the list
//                    if (category != null && category.isActive()) { // Assuming 'isActive()' checks the status
//                        categoryList.add(category); // Add category to list if it's active
//                    }
                    if (category != null && category.isActive()) { // Check if category is active
                        category.setCategoryId(categoryId); // Set categoryId manually after fetching data
                        categoryList.add(category); // Add only active categories to the list
                    }
                }

                // Set adapter after data is fetched
                MyAdapter adapter = new MyAdapter();
                recycler1.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors here if needed
            }
        });
    }




    private void fetchSliderImages() {
        DatabaseReference sliderReference = FirebaseDatabase.getInstance().getReference("SliderImages");
        sliderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sliderImages.clear(); // Clear existing images

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    if (imageUrl != null) {
                        sliderImages.add(imageUrl); // Add valid URLs to the list
                    }
                }



                // Set adapter after images are fetched
                ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(sliderImages);
                viewPager2.setAdapter(sliderAdapter);

                startSliderAutoScroll();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors here if needed
            }
        });
    }

    private void startSliderAutoScroll() {
        sliderHandler.postDelayed(sliderRunnable, 1000);
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (sliderImages.size() > 0) {
                currentPage = (currentPage + 1) % sliderImages.size(); // Loop back to the first image after the last
                viewPager2.setCurrentItem(currentPage, true);
            }
            sliderHandler.postDelayed(this, 20000);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        startSliderAutoScroll();
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable); // Stop auto-scroll when the fragment is paused
    }


//    private void fetchTopRatedProducts() {
//        DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("Feedback");
//        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product");
//
//        feedbackRef.orderByChild("review").equalTo(5) // Fetch only feedbacks with review 5
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        ArrayList<String> topRatedItemIDs = new ArrayList<>();
//                        for (DataSnapshot feedbackSnapshot : snapshot.getChildren()) {
//                            String itemID = feedbackSnapshot.child("itemID").getValue(String.class);
//                            if (itemID != null) {
//                                topRatedItemIDs.add(itemID);
//                            }
//                        }
//
//                        // Fetch product details for the collected itemIDs
//                        fetchProducts(topRatedItemIDs, productRef);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // Handle database errors
//                    }
//                });
//    }
//
//    private void fetchProducts(ArrayList<String> itemIDs, DatabaseReference productRef) {
//        productList.clear(); // Clear the previous data
//        for (String itemID : itemIDs) {
//            productRef.child(itemID).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    Product product = snapshot.getValue(Product.class);
//                    if (product != null) {
//                        productList.add(product);
//                        updateRecycler2(); // Update the adapter whenever a product is added
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    // Handle database errors
//                }
//            });
//        }
//    }
//
//    private void updateRecycler2() {
//        ProductAdapter adapter = new ProductAdapter(productList);
//        recycler2.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//    }
//
private void fetchTopRatedProducts() {
    DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("Feedback");
    DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product");

    // Retrieve all feedback to calculate average rating
    feedbackRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            // Map to store product ratings and counts
            HashMap<String, ProductRating> productRatingsMap = new HashMap<>();

            // Iterate over feedbacks to accumulate ratings
            for (DataSnapshot feedbackSnapshot : snapshot.getChildren()) {
                String itemID = feedbackSnapshot.child("itemID").getValue(String.class);
                Integer rating = feedbackSnapshot.child("review").getValue(Integer.class);

                if (itemID != null && rating != null) {
                    ProductRating currentRating = productRatingsMap.getOrDefault(itemID, new ProductRating(0, 0));
                    currentRating.totalRating += rating;
                    currentRating.count += 1;

                    productRatingsMap.put(itemID, currentRating);
                }
            }

            // Filter products whose average rating is >= 4.5
            ArrayList<String> topRatedItemIDs = new ArrayList<>();
            for (Map.Entry<String, ProductRating> entry : productRatingsMap.entrySet()) {
                String itemID = entry.getKey();
                ProductRating rating = entry.getValue();

                // Calculate average rating
                double averageRating = (double) rating.totalRating / rating.count;

                // If average rating is >= 4.5, add product to the list
                if (averageRating >= 4.5) {
                    topRatedItemIDs.add(itemID);
                }
            }

            // Fetch product details for the filtered itemIDs
            fetchProducts(topRatedItemIDs, productRef);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            // Handle database errors
        }
    });
}

    private void fetchProducts(ArrayList<String> itemIDs, DatabaseReference productRef) {
        productList.clear(); // Clear previous data

        for (String itemID : itemIDs) {
            productRef.child(itemID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        // Calculate and set the average rating dynamically
                        double averageRating = calculateAverageRating(itemID, product);  // Pass the product object here
                        product.setAverageRating(averageRating); // Set calculated average rating
                        productList.add(product); // Add product to the list
                        updateRecycler2(); // Update RecyclerView with new product
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database errors
                }
            });
        }
    }

    // Method to calculate average rating
    private double calculateAverageRating(String productID, Product product) {
        DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("Feedback");
        final double[] totalRating = {0}; // To store the total rating
        final int[] count = {0}; // To store the number of feedbacks

        feedbackRef.orderByChild("itemID").equalTo(productID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot feedbackSnapshot : snapshot.getChildren()) {
                    Integer rating = feedbackSnapshot.child("review").getValue(Integer.class);
                    if (rating != null) {
                        totalRating[0] += rating;
                        count[0]++;
                    }
                }

                // Calculate average rating after collecting all feedbacks
                if (count[0] > 0) {
                    double averageRating = totalRating[0] / (double) count[0];
                    product.setAverageRating(averageRating); // Set the average rating for this product
                } else {
                    // If no ratings, you can set it to 0 or another default value
                    product.setAverageRating(0);
                }

                // Now update the RecyclerView
                updateRecycler2();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });

        // Temporarily return a default value while waiting for the async task to complete
        return 0; // Placeholder for the actual calculated value, which will be set asynchronously
    }

    private void updateRecycler2() {
        // Notify the RecyclerView to update the adapter when the products and ratings are set
        ProductAdapter adapter = new ProductAdapter(productList);
        recycler2.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    // ProductRating class to hold total rating and review count
    public static class ProductRating {
        public int totalRating;
        public int count;

        public ProductRating(int totalRating, int count) {
            this.totalRating = totalRating;
            this.count = count;
        }
    }

    //category Adapter
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.raw_layout, parent, false);
            MyHolder myHolder = new MyHolder(view);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder myHolder, int position) {

            Category model = categoryList.get(position);
//          myHolder.cat_id.setText(model.cat_id + "");
//          myHolder.cat_status.setText(model.cat_status + "");
            myHolder.cat_name.setText(model.name);

//          myHolder.created_date.setText(model.created_date);
            Glide.with(myHolder.itemView)
                    .load(model.iurl)
                    .into(myHolder.cat_image);



            myHolder.relative_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ProductActivity.class);
                    intent.putExtra("categoryId", model.getCategoryId()); // Pass the category ID

                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }


        class MyHolder extends RecyclerView.ViewHolder {

            TextView cat_id, cat_status, cat_name, created_date;
            ImageView cat_image;
            RelativeLayout relative_main;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
//              cat_id = itemView.findViewById(R.id.cat_id);
                cat_name = itemView.findViewById(R.id.name);
                cat_image = itemView.findViewById(R.id.iurl);
//              cat_status = itemView.findViewById(R.id.cat_status);
//              created_date = itemView.findViewById(R.id.created_date);
                relative_main = itemView.findViewById(R.id.relative_main);
            }
        }
    }


    public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder> {

        private ArrayList<String> sliderImages;

        public ImageSliderAdapter(ArrayList<String> sliderImages) {
            this.sliderImages = sliderImages;
        }

        @NonNull
        @Override
        public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, parent, false);
            return new SliderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
            Glide.with(holder.itemView)
                    .load(sliderImages.get(position))
                    .into(holder.sliderImage);
        }

        @Override
        public int getItemCount() {
            return sliderImages.size();
        }

        class SliderViewHolder extends RecyclerView.ViewHolder {
            ImageView sliderImage;

            public SliderViewHolder(@NonNull View itemView) {
                super(itemView);
                sliderImage = itemView.findViewById(R.id.sliderImage);
            }
        }
    }

    //product Adapter

    class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
        private ArrayList<Product> productList;

        public ProductAdapter(ArrayList<Product> productList) {
            this.productList = productList;
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_item, parent, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            Product product = productList.get(position);
            if (product != null) {
                holder.name.setText(product.getName() != null ? product.getName() : "No Name");
                holder.price.setText(product.getPrice() != null ? "₹" + product.getPrice() : "Price Unavailable");

                double rating = product.getAverageRating();
                holder.ratingText.setText(String.format("%.1f", rating));

                if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                    Glide.with(holder.itemView.getContext())
                            .load(product.getImageUrl())
                            .placeholder(R.drawable.order) // Placeholder image
                            .error(R.drawable.profile)      // Error image
                            .into(holder.imageUrl);
                } else {
                    holder.imageUrl.setImageResource(R.drawable.profile);
                }
            } else {
                Log.e("ProductAdapter", "Null product at position: " + position);
            }

            holder.relative_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ProductDetail.class);
                    intent.putExtra("productID", product.getProductID()); // Use getProductID() to fetch the productID
                    intent.putExtra("productName", product.getName());
                    intent.putExtra("productPrice", product.getPrice());
                    intent.putExtra("productDescription", product.getDescription());
                    intent.putExtra("productImageUrl", product.getImageUrl());
                    intent.putExtra("user_id", userId);
                    Log.d("ProductActivity", "User ID: " + userId);

                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView name, price,ratingText;
            ImageView imageUrl;

            RelativeLayout relative_main;

            public ProductViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                price = itemView.findViewById(R.id.price);
                imageUrl = itemView.findViewById(R.id.imageUrl);
                ratingText = itemView.findViewById(R.id.ratingText);
                relative_main = itemView.findViewById(R.id.relative_main);
            }
        }
    }

}