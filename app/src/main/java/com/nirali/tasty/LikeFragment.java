package com.nirali.tasty;

import static android.content.Intent.getIntent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LikeFragment extends Fragment {

    private RecyclerView recycler4;
    private List<Product> likedProducts;
    private ProductAdapter adapter;
    ImageView img_Cart,emptyStateImage;
    String productName; // Name of the product
    String productImageUrl; //

    String productID; // Declare productID here

    private BroadcastReceiver likedProductsReceiver;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_like, container, false);
        recycler4 = view.findViewById(R.id.recycler4);
        img_Cart = view.findViewById(R.id.img_cart);
        emptyStateImage = view.findViewById(R.id.emptyStateImage);
        likedProducts = new ArrayList<>();
        adapter = new ProductAdapter(likedProducts);
        recycler4.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Adjust span count if needed
        recycler4.setAdapter(adapter);
        loadLikedProducts(); // Populate the list of liked products

        Intent intent = getActivity().getIntent();
        productID = intent.getStringExtra("productID");
        productName = intent.getStringExtra("productName");
        productImageUrl = intent.getStringExtra("productImageUrl");
        Log.d("ProductDetail", "Received productID: " + productID);



        img_Cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Assuming CartFragment has a default constructor
                CartFragment cartFragment = new CartFragment();

                fragmentTransaction.replace(R.id.fragment_container, cartFragment); // Replace with your container's ID
                fragmentTransaction.addToBackStack(null); // Optional: Adds to the back stack for navigation
                fragmentTransaction.commit();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false; // No need to handle move here, only swipe
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Product product = likedProducts.get(position);

                Log.d("LikeFragment", "Before removal: " + likedProducts.size() + " products");

                likedProducts.remove(position);
                adapter.notifyItemRemoved(position);

                Log.d("LikeFragment", "After removal from RecyclerView: " + likedProducts.size() + " products");

                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("ProductLikes", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Set<String> likedProductsSet = sharedPreferences.getStringSet("liked_products", new HashSet<>());
                Log.d("LikeFragment", "Liked products in SharedPreferences before removal: " + likedProductsSet);

                String productDetails = product.name + "," + product.imageUrl + "," + product.price;
                likedProductsSet.remove(productDetails);

                Log.d("LikeFragment", "ProductDetails to remove: " + productDetails);

                editor.putStringSet("liked_products", likedProductsSet);
                editor.apply();

                Log.d("LikeFragment", "Liked products in SharedPreferences after removal: " + likedProductsSet);
            }

        });

        itemTouchHelper.attachToRecyclerView(recycler4);



        return view;
    }




    private void loadLikedProducts() {
        likedProducts.clear();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("ProductLikes", Context.MODE_PRIVATE);

        Set<String> likedProductsSet = sharedPreferences.getStringSet("liked_products", new HashSet<>());

        if (likedProductsSet != null && !likedProductsSet.isEmpty()) {
            // Iterate through the liked products and add them to the list
            for (String productDetails : likedProductsSet) {
                String[] details = productDetails.split(",");
                if (details.length >= 3) {
                    String productName = details[0];
                    String productPrice = details[1];
                    String productImageUrl = details[2];
                    String productDescription = details.length > 3 ? details[3] : ""; // You can use a default description

                    likedProducts.add(new Product(productName, productPrice, productImageUrl, productDescription));
                }
            }

            adapter.notifyDataSetChanged();
        }
        if (likedProducts.isEmpty()) {
            emptyStateImage.setVisibility(View.VISIBLE);
            recycler4.setVisibility(View.GONE);
        } else {
            emptyStateImage.setVisibility(View.GONE);
            recycler4.setVisibility(View.VISIBLE);
        }

//        } else {
//            Log.d("LikeFragment", "No liked products available.");
//        }
    }
    private void saveLikedProducts() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("ProductLikes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> likedProductsSet = new HashSet<>();
        for (Product product : likedProducts) {
            // Saving the complete product details: name, price, imageUrl, and description
            String productDetails = product.name + "," + product.price + "," + product.imageUrl + "," + product.description;
            likedProductsSet.add(productDetails);
        }

        editor.putStringSet("liked_products", likedProductsSet);
        editor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Save the liked products when the fragment is destroyed
        saveLikedProducts();

        if (likedProductsReceiver != null) {
            requireActivity().unregisterReceiver(likedProductsReceiver);
        }
    }


    private void addToCart(String productName, String productImageUrl, float productPrice) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Cart", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> cartSet = sharedPreferences.getStringSet("cart_items", new HashSet<>());

        // Check if the productID is valid and use it in the cart item string
        String cartItemString = productID + "," + productName + "," + productImageUrl + "," + productPrice;

        // Add new item to set and save it back
        cartSet.add(cartItemString);

        editor.putStringSet("cart_items", cartSet);
        editor.apply();

        // Debugging: Log the entire cart
        Log.d("ProductDetail", "Added to cart: " + cartItemString);
        Log.d("CartItems", "Current cart items: " + cartSet);
    }



    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

        private final List<Product> products;

        public ProductAdapter(List<Product> products) {
            this.products = products;
        }

        @Override
        public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.like_item, parent, false);
            return new ProductViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ProductViewHolder holder, int position) {
            Product product = products.get(position);
            holder.productName.setText(product.name);
            holder.productPrice.setText(product.price);

            Glide.with(holder.itemView.getContext())
                    .load(product.imageUrl)
                    .into(holder.imageUrl);


//            holder.likeButton.setImageResource(R.drawable.favorite); // Show liked icon
//            holder.add_to_cart_button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    addToCart(product.name, product.imageUrl, Float.parseFloat(product.price));
//                    Toast.makeText(getContext(), "Product added to cart", Toast.LENGTH_SHORT).show();
//                }
//            });
            holder.add_to_cart_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to ProductDetailsActivity with the product details
                    Intent intent = new Intent(v.getContext(), ProductDetail.class);
                    intent.putExtra("productID", product.getProductID()); // Use the getter here
                    intent.putExtra("productName", product.name);
                    intent.putExtra("productPrice", product.price);
                    intent.putExtra("productImageUrl", product.imageUrl);
                    intent.putExtra("productDescription", product.description);

                    // Start the activity
                    v.getContext().startActivity(intent);
                }
            });
//            holder.likeButton.setOnClickListener(v -> {
//
//                likedProducts.remove(position);
//                notifyItemRemoved(position);
//                notifyItemRangeChanged(position, likedProducts.size());
//
//                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("ProductLikes", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//
//                Set<String> likedProductsSet = new HashSet<>(sharedPreferences.getStringSet("liked_products", new HashSet<>()));
//
//                String productDetails = product.name + "," + product.price + "," + product.imageUrl;
//
//                likedProductsSet.remove(productDetails);
//
//                editor.putStringSet("liked_products", likedProductsSet);
//                editor.apply();
//
//                Log.d("LikeFragment", "Removed product from likes: " + productDetails);
//
//                // Optional: Show feedback to the user, e.g., "Product removed from likes"
//            });

        }


            @Override
        public int getItemCount() {
            return products.size();
        }

        public class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView productName, productDescription, productPrice;
            ImageView imageUrl;
            Button add_to_cart_button;

            ImageView likeButton;

            public ProductViewHolder(View itemView) {
                super(itemView);
                productName = itemView.findViewById(R.id.product_name);
                productDescription = itemView.findViewById(R.id.description);
                productPrice = itemView.findViewById(R.id.price);
                imageUrl = itemView.findViewById(R.id.imageUrl);
                likeButton = itemView.findViewById(R.id.like_button);
                add_to_cart_button = itemView.findViewById(R.id.add_to_cart_button);
            }
        }
    }




}