package com.nirali.tasty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashSet;
import java.util.Set;

public class ProductDetail extends AppCompatActivity {

    TextView name, description, price, quantity;
    ImageView imageUrl, like_button,img_profile;
    LinearLayout linear1;



    Button increment_button, decrement_button, cart_button;


    boolean isLiked = false; // Track like status

    String productName; // Name of the product
    String productImageUrl; //
    int currentQuantity = 1; // Default quantity
    float basePrice; // Base price of the product
    String productID; // Declare productID here


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Initialize UI components
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);
        imageUrl = findViewById(R.id.imageUrl);
        linear1 = findViewById(R.id.linear1);
        increment_button = findViewById(R.id.increment_button);
        decrement_button = findViewById(R.id.decrement_button);
        quantity = findViewById(R.id.quantity);
        like_button = findViewById(R.id.like_button);
        cart_button = findViewById(R.id.cart_button);
        img_profile = findViewById(R.id.img_profile);


       linear1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               // Replace the current Fragment with the desired Fragment
               Intent intent = new Intent(ProductDetail.this, FragmentContainerActivity.class);
               startActivity(intent);


           }
       });



        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            finish();
            }
        });
        // Retrieve data from the Intent
        Intent intent = getIntent();

         productID = intent.getStringExtra("productID"); // Retrieve the product ID
        Log.d("ProductDetail", "Received productID: " + productID); // Log the productID


        productName = intent.getStringExtra("productName");
        String productPriceStr = intent.getStringExtra("productPrice");
        String productDescription = intent.getStringExtra("productDescription");
        productImageUrl = intent.getStringExtra("productImageUrl"); // Set productImageUrl here

        // Parse base price safely
        try {
            basePrice = Float.parseFloat(productPriceStr);
        } catch (NumberFormatException e) {
            Log.e("ProductDetail", "Error parsing product price: " + productPriceStr, e);
            Toast.makeText(this, "Invalid product price", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if price is invalid
            return;
        }

        // Set text to TextViews
        name.setText(productName);
        price.setText(String.format("₹ %.2f", basePrice)); // Format price display
        description.setText(productDescription);

        Glide.with(this)
                .load(productImageUrl)
                .into(imageUrl);

        isLiked = getProductLikedStatus(productName);
        updateLikeButtonVisibility();

        increment_button.setOnClickListener(v -> {
            currentQuantity++;
            quantity.setText(String.valueOf(currentQuantity));
            updateTotalPrice();
        });

        decrement_button.setOnClickListener(v -> {
            if (currentQuantity > 1) {  // Ensure quantity does not go below 1
                currentQuantity--;
                quantity.setText(String.valueOf(currentQuantity));
                updateTotalPrice();
            }
        });

        cart_button.setOnClickListener(v -> {
            addToCart( productName, productImageUrl, basePrice);
            Toast.makeText(ProductDetail.this, "Product added to cart", Toast.LENGTH_SHORT).show();
        });

        like_button.setOnClickListener(v -> {

            isLiked = !isLiked; // Toggle like status
            saveProductLikedStatus(productName, isLiked); // Save the current like status
            updateLikeButtonVisibility(); // Update button appearance based on like status


            if (isLiked) {
                Toast.makeText(ProductDetail.this, "Added to favorites", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProductDetail.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void updateTotalPrice() {
        float totalPrice = currentQuantity * basePrice;
        price.setText(String.format("₹ %.2f", totalPrice));
    }

    private void addToCart(String productName, String productImageUrl, float productPrice) {
        SharedPreferences sharedPreferences = getSharedPreferences("Cart", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> cartSet = sharedPreferences.getStringSet("cart_items", new HashSet<>());

        // Calculate total price based on current quantity
        float totalPrice = currentQuantity * productPrice;

        // Include product name, image URL, original price, total price, and quantity in the cart item string
        String cartItemString = productID + "," + productName + "," + productImageUrl + "," + productPrice + "," + totalPrice + "," + currentQuantity;

        // Add new item to set and save it back
        cartSet.add(cartItemString);

        editor.putStringSet("cart_items", cartSet);
        editor.apply();

        Log.d("ProductDetail", "Added to cart: " + cartItemString);
    }

    private void saveProductLikedStatus(String productName, boolean isLiked) {
        SharedPreferences sharedPreferences = getSharedPreferences("ProductLikes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Retrieve the current liked products set
        Set<String> likedProducts = sharedPreferences.getStringSet("liked_products", new HashSet<>());

        // Create a unique string representation of the product details
        String productDetails = productName + "," + productImageUrl + "," + basePrice;

        // Add or remove the product from the liked products set
        if (isLiked) {
            likedProducts.add(productDetails); // Add the product to the set if it is liked
        } else {
            likedProducts.remove(productDetails); // Remove the product if it was previously liked
        }

        // Save the updated set back to SharedPreferences
        editor.putStringSet("liked_products", likedProducts);
        editor.apply();
    }



    private boolean getProductLikedStatus(String productName) {
        SharedPreferences sharedPreferences = getSharedPreferences("ProductLikes", Context.MODE_PRIVATE);
        Set<String> likedProducts = sharedPreferences.getStringSet("liked_products", new HashSet<>());

        // Check if the product name and image URL exist in saved liked product details
        String productDetails = productName + "," + productImageUrl + "," + basePrice;
        return likedProducts.contains(productDetails); // Match exact product details
    }


    // Update the visibility of the like button based on liked status
    private void updateLikeButtonVisibility() {
        if (isLiked) {
            like_button.setImageResource(R.drawable.favorite); // Show liked image if already liked
        } else {
            like_button.setImageResource(R.drawable.like); // Show unliked image if not liked
        }
    }
}