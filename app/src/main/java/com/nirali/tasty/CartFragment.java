package com.nirali.tasty;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private ArrayList<CartItem> cartItems;
    private TextView totalAmount;
    ImageView img_Cart;
    Button checkoutbutton;
    FirebaseAuth mAuth;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // If not logged in, navigate to RegistrationActivity
            Intent intent = new Intent(getActivity(), RegistrationActivity.class);
            startActivity(intent);
            getActivity().finish(); // Close CartFragment as user needs to register/login
            return view; // Return early as there's no need to show the cart
        }


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        checkoutbutton = view.findViewById(R.id.checkoutButton);
        img_Cart = view.findViewById(R.id.img_cart);
        cartItems = loadCartItems();
        cartAdapter = new CartAdapter(cartItems);
        recyclerView.setAdapter(cartAdapter);

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


        checkoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CheckoutActivity1.class);

                ArrayList<String> itemNames = new ArrayList<>();
                ArrayList<String> itemIDs = new ArrayList<>();
                ArrayList<Integer> quantities = new ArrayList<>();
                float totalCartAmount = 0;

                for (CartItem item : cartItems) {
                    itemIDs.add(item.getProductID());  // Assuming CartItem has a getProductID method
                    itemNames.add(item.getProductName());
                    quantities.add(item.getQuantity());
                    totalCartAmount += item.getTotalPrice();  // Accumulate total price
                }

                Log.d("CartFragment", "itemIDs: " + itemIDs);
                Log.d("CartFragment", "itemNames: " + itemNames);
                Log.d("CartFragment", "quantities: " + quantities);
                Log.d("CartFragment", "totalAmount: " + totalCartAmount);

                intent.putStringArrayListExtra("itemIDs", itemIDs);
                intent.putStringArrayListExtra("itemNames", itemNames);
                intent.putIntegerArrayListExtra("quantities", quantities);
                intent.putExtra("totalAmount", totalCartAmount);


                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Cart", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isCheckoutComplete", true); // Mark checkout as complete
                editor.apply();
                startActivity(intent);

            }
        });




        totalAmount = view.findViewById(R.id.totalAmount); // Initialize the totalAmount TextView

        cartItems = loadCartItems();
        cartAdapter = new CartAdapter(cartItems);
        recyclerView.setAdapter(cartAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false; // No need to support move action
            }

//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                int position = viewHolder.getAdapterPosition();
//                CartItem itemToRemove = cartItems.get(position);  // Get the item to be removed
//
//                // Remove the item from the cart and update the list
//                cartItems.remove(position);
//                cartAdapter.notifyItemRemoved(position);
//
//                // Update the total amount after item removal
//                updateTotalAmount();
//
//                // Remove the product permanently from SharedPreferences
//                removeProductFromSharedPreferences(itemToRemove);
//                }
//        });
//
//
//        itemTouchHelper.attachToRecyclerView(recyclerView);
//
//
//
//        updateTotalAmount(); // Calculate and set the total amount initially
//
//        return view;
//    }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {




                int position = viewHolder.getAdapterPosition();
                CartItem itemToRemove = cartItems.get(position);  // Get the item to be removed




                // Remove the item from the cart and update the list
                cartItems.remove(position);
                cartAdapter.notifyItemRemoved(position);

                // Update the total amount after item removal
                updateTotalAmount();

                // Remove the product permanently from SharedPreferences
                removeProductFromSharedPreferences(itemToRemove);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);


        updateTotalAmount(); // Calculate and set the total amount initially
        saveCartItems();

        return view;
    }



//    private void saveCartItems() {
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Cart", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        Set<String> cartSet = new HashSet<>();
//        for (CartItem item : cartItems) {
//            // Save each item in a formatted string like "productID,name,imageUrl,originalPrice,totalPrice,quantity"
//            String cartItemString = item.getProductID() + "," + item.getProductName() + "," + item.getProductImageUrl() + "," + item.getOriginalPrice() + "," + item.getTotalPrice() + "," + item.getQuantity();
//            cartSet.add(cartItemString);
//        }
//        editor.putStringSet("cart_items", cartSet);
//        editor.putBoolean("isCheckoutComplete", false); // Mark that checkout is NOT complete
//        editor.apply();  // Save the cart items
//    }


//    private void removeProductFromSharedPreferences(CartItem itemToRemove) {
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Cart", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        // Load the current cart items
//        Set<String> cartSet = new HashSet<>(sharedPreferences.getStringSet("cart_items", new HashSet<>()));
//
//        // Use productID as a unique identifier to locate the item
//        String productIDToRemove = itemToRemove.getProductID();
//        Set<String> updatedCartSet = new HashSet<>();
//
//        for (String itemDetails : cartSet) {
//            if (!itemDetails.startsWith(productIDToRemove + ",")) {  // Exclude the item to be removed
//                updatedCartSet.add(itemDetails);
//            }
//        }
//
//        editor.putStringSet("cart_items", updatedCartSet);
//        editor.apply(); // Apply changes to SharedPreferences
//    }

    private void removeProductFromSharedPreferences(CartItem itemToRemove) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Cart", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Load the current cart items
        Set<String> cartSet = new HashSet<>(sharedPreferences.getStringSet("cart_items", new HashSet<>()));

        if (cartSet != null && !cartSet.isEmpty()) {
            // Iterate through the cartSet to find and remove the matching product
            String productIDToRemove = itemToRemove.getProductID();
            String itemToRemoveString = null;

            for (String itemDetails : cartSet) {
                if (itemDetails.startsWith(productIDToRemove + ",")) {
                    itemToRemoveString = itemDetails; // Found the item to remove
                    break;
                }
            }

            if (itemToRemoveString != null) {
                cartSet.remove(itemToRemoveString); // Remove the matching item
                editor.putStringSet("cart_items", cartSet); // Update SharedPreferences
                editor.apply(); // Save changes
                Log.d("CartFragment", "Item removed successfully: " + itemToRemoveString);
            } else {
                Log.d("CartFragment", "Item not found for removal: " + productIDToRemove);
            }
        } else {
            Log.d("CartFragment", "Cart is already empty or null.");
        }
    }


    private ArrayList<CartItem> loadCartItems() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Cart", Context.MODE_PRIVATE);



        Set<String> cartSet = sharedPreferences.getStringSet("cart_items", new HashSet<>());

        if (cartSet == null || cartSet.isEmpty()) {
            Log.d("CartFragment", "No items found in cart");
            return new ArrayList<>(); // Return an empty list if no items are found
        }


        ArrayList<CartItem> cartItemList = new ArrayList<>();

        for (String cartItemString : cartSet) {
            try {
                String[] itemDetails = cartItemString.split(",");
                if (itemDetails.length == 6) {  // Expecting 6 values (ID, Name, ImageUrl, Price, TotalPrice, Quantity)
                    String productID = itemDetails[0];
                    String productName = itemDetails[1];
                    String productImageUrl = itemDetails[2];
                    float originalPrice = Float.parseFloat(itemDetails[3]);
                    float totalPrice = Float.parseFloat(itemDetails[4]);
                    int quantity = Integer.parseInt(itemDetails[5]);
                    Log.d("CartFragment", "Loaded product ID: " + productID);

                    CartItem cartItem = new CartItem(productID, productName, productImageUrl, originalPrice, totalPrice, quantity);
                    cartItemList.add(cartItem);
                } else {
                    Log.e("CartFragment", "Invalid cart item format: " + cartItemString);
                }
            } catch (Exception e) {
                Log.e("CartFragment", "Error parsing cart item: " + cartItemString, e);
            }
        }

        return cartItemList;
    }

    // Method to calculate and update the total amount
    private void updateTotalAmount() {
        float sum = 0;
        for (CartItem item : cartItems) {
            sum += item.getTotalPrice();
        }
        totalAmount.setText(String.format("₹ %.2f", sum));
    }
    private void saveCartItems() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Cart", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> cartSet = new HashSet<>();

        // Save each cart item as a string in the format: productID,name,imageUrl,originalPrice,totalPrice,quantity
        for (CartItem item : cartItems) {
            String cartItemString = item.getProductID() + "," + item.getProductName() + "," + item.getProductImageUrl() + "," + item.getOriginalPrice() + "," + item.getTotalPrice() + "," + item.getQuantity();
            cartSet.add(cartItemString);
        }

        editor.putStringSet("cart_items", cartSet);  // Save cart items
        editor.putBoolean("isCheckoutComplete", false); // Checkout not complete yet
        editor.apply();  // Apply changes
    }

    public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

        private ArrayList<CartItem> cartItems;

        public CartAdapter(ArrayList<CartItem> cartItems) {
            this.cartItems = cartItems;
        }

        @Override
        public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d("CartFragment", "Inflating cart_item layout");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
            return new CartViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CartViewHolder holder, int position) {
            CartItem item = cartItems.get(position);

            // Set initial values
            holder.name.setText(item.getProductName());
            holder.price.setText(String.format("₹ %.2f", item.getTotalPrice()));
            holder.originalPrice.setText(String.format("₹ %.2f", item.getOriginalPrice()));
            holder.quantity.setText(String.valueOf(item.getQuantity()));

            Glide.with(holder.itemView.getContext())
                    .load(item.getProductImageUrl())
                    .into(holder.imageUrl);

            // Set listeners for increment and decrement
            holder.increment_button.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() + 1;
                item.setQuantity(newQuantity);
                updateItemPrice(holder, item);
                saveCartItems();// Update total price based on new quantity
                updateTotalAmount();  // Update total amount after changing quantity
            });

            holder.decrement_button.setOnClickListener(v -> {
                int currentQuantity = item.getQuantity();
                if (currentQuantity > 1) {  // Ensure quantity does not go below 1
                    int newQuantity = currentQuantity - 1;
                    item.setQuantity(newQuantity);
                    updateItemPrice(holder, item);
                    saveCartItems();// Update total price based on new quantity
                    updateTotalAmount();  // Update total amount after changing quantity
                }
            });

            // Handling the swipe image visibility
            ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    // Show the hidden image when item is swiped
                    holder.swipedImage.setVisibility(View.VISIBLE);
                }
            };
        }

        private void updateItemPrice(CartViewHolder holder, CartItem item) {
            float totalPrice = item.getOriginalPrice() * item.getQuantity();
            item.setTotalPrice(totalPrice);  // Update the item’s total price
            holder.price.setText(String.format("₹ %.2f", totalPrice));  // Update display
            holder.quantity.setText(String.valueOf(item.getQuantity()));  // Update display quantity
        }


        // Helper function to update the total price of an item


        @Override
        public int getItemCount() {
            return cartItems.size();
        }

        public class CartViewHolder extends RecyclerView.ViewHolder {

            TextView name, price, quantity, originalPrice;
            ImageView imageUrl,swipedImage;
            TextView increment_button, decrement_button;

            public CartViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                price = itemView.findViewById(R.id.price);
                quantity = itemView.findViewById(R.id.quantity);
                originalPrice = itemView.findViewById(R.id.originalPrice);
                imageUrl = itemView.findViewById(R.id.imageUrl);
                increment_button = itemView.findViewById(R.id.increment_button);
                decrement_button = itemView.findViewById(R.id.decrement_button);
            }
        }
    }
}



