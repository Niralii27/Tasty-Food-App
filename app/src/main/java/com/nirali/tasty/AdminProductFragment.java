package com.nirali.tasty;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


public class AdminProductFragment extends Fragment {

    Button btn_category, btn_product, btn_slider;
    RecyclerView recycler1, recycler2;
    ViewPager2 viewPager2;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());

    ArrayList<Category> categoryList = new ArrayList<>();
    ArrayList<Product> productList = new ArrayList<>();


    ArrayList<String> sliderImages = new ArrayList<>();


    private int currentPage = 0;

    DatabaseReference databaseReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_product, container, false);

        btn_category = view.findViewById(R.id.btn_category);
        btn_product = view.findViewById(R.id.btn_product);
        btn_slider = view.findViewById(R.id.btn_slider);
        recycler1 = view.findViewById(R.id.recycler1);
        recycler2 = view.findViewById(R.id.recycler2);

        btn_slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CategoryActivity.class);
                startActivity(intent);
            }
        });

        btn_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CategoryActivity.class);
                startActivity(intent);
            }
        });

        btn_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CategoryActivity.class);
                startActivity(intent);
            }
        });

        viewPager2 = view.findViewById(R.id.viewPager2); // Initialize ViewPager2

        recycler1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recycler2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        databaseReference = FirebaseDatabase.getInstance().getReference("Category");

        fetchCategories();
        fetchSliderImages();
        fetchProduct();

        return view;
    }

    private void fetchCategories() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear(); // Clear the list before adding new data

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category != null) {
                        category.setCategoryId(dataSnapshot.getKey()); // Set the Firebase key as the category ID
                        categoryList.add(category); // Add category to the list
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


    private void fetchProduct() {
        DatabaseReference productReference = FirebaseDatabase.getInstance().getReference("Product");
        productReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        Log.d("ProductDetails", "Product ID: " + dataSnapshot.getKey()); // Log the Firebase key (productID)
                        product.setProductID(dataSnapshot.getKey()); // Set the productID from Firebase key
                        productList.add(product);
                    }
                }

                MyAdapter1 productAdapter = new MyAdapter1();
                recycler2.setAdapter(productAdapter);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors here if needed
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

    class MyAdapter extends RecyclerView.Adapter<AdminProductFragment.MyAdapter.MyHolder> {
        @NonNull
        @Override
        public AdminProductFragment.MyAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.raw_layout, parent, false);
            MyHolder myHolder = new MyHolder(view);

            return myHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull AdminProductFragment.MyAdapter.MyHolder myHolder, int position) {

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
                    Intent intent = new Intent(getContext(), admin_CategoryDetails.class);
                    // Pass the category details (e.g., ID, name, or any relevant data)
                    intent.putExtra("categoryId", model.getCategoryId()); // Replace with your method to get ID
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

    public class ImageSliderAdapter extends RecyclerView.Adapter<AdminProductFragment.ImageSliderAdapter.SliderViewHolder> {

        private ArrayList<String> sliderImages;

        public ImageSliderAdapter(ArrayList<String> sliderImages) {
            this.sliderImages = sliderImages;
        }

        @NonNull
        @Override
        public AdminProductFragment.ImageSliderAdapter.SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, parent, false);
            return new SliderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdminProductFragment.ImageSliderAdapter.SliderViewHolder holder, int position) {
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

    //Product Adapter
    class MyAdapter1 extends RecyclerView.Adapter<MyAdapter1.ProductViewHolder> {

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_layout1, parent, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            Product product = productList.get(position);

            // Set product name
            holder.productName.setText(product.getName());

            // Set product image using Glide
            Glide.with(holder.itemView.getContext())
                    .load(product.getImageUrl()) // Replace `getImageUrl()` with the actual method name in your `Product` class
                    .placeholder(R.drawable.order) // Optional placeholder
                    .error(R.drawable.order)             // Optional error image
                    .into(holder.productImage);

            holder.itemLayout.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), admin_product_details.class);
                intent.putExtra("productId", product.getProductID()); // Pass product ID to next activity
                view.getContext().startActivity(intent);
            });

            // Set click listener for the item
//            holder.itemLayout.setOnClickListener(view -> {
//                Intent intent = new Intent(view.getContext(), CategoryActivity.class);
//                intent.putExtra("productId", product.productID()); // Pass product ID to next activity
//                view.getContext().startActivity(intent);
//            });
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {

            TextView productName;
            ImageView productImage;
            RelativeLayout itemLayout;

            public ProductViewHolder(@NonNull View itemView) {
                super(itemView);
                productName = itemView.findViewById(R.id.name);  // Replace with your TextView ID
                productImage = itemView.findViewById(R.id.imageUrl); // Replace with your ImageView ID
                itemLayout = itemView.findViewById(R.id.relative_main);  // Replace with your main layout ID
            }
        }
    }


}