package com.nirali.tasty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

public class MainActivity2 extends AppCompatActivity {

    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    NavigationView navigationView,nav_view;
    ImageView img_profile,img_cart;
    LinearLayout linear1,linear2;
    TextView nav_email,nav_name;

    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    private String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        auth = FirebaseAuth.getInstance();
        bottomNavigationView = findViewById(R.id.bottom_navigationView);
        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        img_profile = findViewById(R.id.img_profile);
        nav_view = findViewById(R.id.nav_view);
        View headerView = nav_view.getHeaderView(0); // Assuming nav_name is in the first header
         nav_email = headerView.findViewById(R.id.nav_email);
         nav_name = headerView.findViewById(R.id.nav_name);
        databaseReference = FirebaseDatabase.getInstance().getReference("Profile");


        userId = getIntent().getStringExtra("userId");

        if (userId != null) {
            Log.d("MainActivity2", "Received userId: " + userId);
            // Aap Firebase operations ke liye is userId ka use kar sakte hain
        } else {
            Log.e("MainActivity2", "No userId received! Retrieving from FirebaseAuth...");
            // Agar intent me userId nahi mila, FirebaseAuth se le lein
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                userId = currentUser.getUid();
                Log.d("MainActivity2", "Fetched userId from FirebaseAuth: " + userId);
            } else {
                // User logged out hai ya koi issue hai
                Log.e("MainActivity2", "User not logged in!");
                Intent intent = new Intent(MainActivity2.this, Login.class);
                startActivity(intent);
                finish();
            }
        }




        linear1 = findViewById(R.id.linear1);
        linear2 = findViewById(R.id.linear2);
        img_cart = findViewById(R.id.img_cart);
        user = auth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        else{
            nav_email.setText(user.getEmail());
            String displayName = user.getDisplayName();
            if (displayName != null) {
                nav_name.setText(displayName); // Set the display name
            } else {
                nav_name.setText("No Name Provide"); // Fallback if no name is set
            }

            loadProfilePicture(user.getUid());

        }

      //  SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
       // boolean saveLogin = preferences.getBoolean("saveLogin", false);

//       FirebaseAuth.getInstance().signOut();

//        nav_view.setNavigationItemSelectedListener(item -> {
//            if (item.getItemId() == R.id.nav_logout) {
//                // Perform logout
//                auth.signOut();
//                Intent intent = new Intent(MainActivity2.this, Login.class);
//                startActivity(intent);
//                finish();
//                return true;
//            }
//            return false;
//        });
//
//        nav_view.setNavigationItemSelectedListener(item -> {
//            if (item.getItemId() == R.id.nav_share) {
//                Intent intent = new Intent(MainActivity2.this, ContactUsActivity.class);
//                startActivity(intent);
//                finish();
//                return true;
//            }
//            return false;
//        });
//
//        nav_view.setNavigationItemSelectedListener(item -> {
//            if (item.getItemId() == R.id.nav_about) {
//                Intent intent = new Intent(MainActivity2.this, AboutUsActivity.class);
//                startActivity(intent);
//                finish();
//                return true;
//            }
//            return false;
//        });

        nav_view.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
                auth.signOut();
                Intent logoutIntent = new Intent(MainActivity2.this, Login.class);
                startActivity(logoutIntent);
                finish();
                return true;
            } else if (id == R.id.nav_share) {
                Intent shareIntent = new Intent(MainActivity2.this, ContactUsActivity.class);
                startActivity(shareIntent);
                return true;
            } else if (id == R.id.nav_about) {
                Intent aboutIntent = new Intent(MainActivity2.this, AboutUsActivity.class);
                startActivity(aboutIntent);
                return true;
            }else if (id == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

                return true;
            }

            drawerLayout.closeDrawer(GravityCompat.START); // Close drawer after item is selected
            return false;
        });


        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               drawerLayout.openDrawer(GravityCompat.START);

            }
        });

img_cart.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CartFragment()).commit();

    }
});
        linear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CartFragment()).commit();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new LikeFragment()).commit();
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LikeFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrderFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccountFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CartFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();








        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {

          /* switch (item.getItemId()) {
                case R.id.home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                    break;

                case R.id.menu:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuFragment()).commit();
                    break;

                case R.id.order:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrderFragment()).commit();
                    break;

                case R.id.more:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccountFragment()).commit();
                    break;
            }*/

            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            } else if (itemId == R.id.menu) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuFragment()).commit();
            } else if (itemId == R.id.order) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrderFragment()).commit();
            } else if (itemId == R.id.more) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccountFragment()).commit();
            } else {
                return false;
            }

            return true;
        });


    }

    private void loadProfilePicture(String userId) {
        databaseReference.child(userId).child("profilePicture").get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String profilePicturePath = snapshot.getValue(String.class);
                        Log.d("ProfilePicturePath", "Path: " + profilePicturePath); // Log the path for debugging
                        if (profilePicturePath != null && !profilePicturePath.isEmpty()) {
                            File file = new File(profilePicturePath);
                            if (file.exists()) {
                                Bitmap profilePicture = BitmapFactory.decodeFile(profilePicturePath);
                                if (profilePicture != null) {
                                    img_profile.setImageBitmap(profilePicture);
//                                    Toast.makeText(this, "Profile picture loaded successfully.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Failed to decode profile picture.", Toast.LENGTH_SHORT).show();
                                    img_profile.setImageResource(R.drawable.profile); // Fallback image
                                }
                            } else {
                                Toast.makeText(this, "Profile picture file does not exist.", Toast.LENGTH_SHORT).show();
                                img_profile.setImageResource(R.drawable.profile); // Fallback image
                            }
                        } else {
                            Toast.makeText(this, "Profile picture path is empty.", Toast.LENGTH_SHORT).show();
                            img_profile.setImageResource(R.drawable.profile); // Fallback image
                        }
                    } else {
                        Toast.makeText(this, "Profile data not found in Firebase.", Toast.LENGTH_SHORT).show();
                        img_profile.setImageResource(R.drawable.profile); // Fallback image
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch profile data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    img_profile.setImageResource(R.drawable.profile); // Fallback image
                });
    }


//    private void saveTestProfilePicture() {
//        try {
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile); // Example: replace with your image
//            File file = new File(getFilesDir(), "test_profile.jpg");
//            FileOutputStream outputStream = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//            outputStream.close();
//            Log.d("ProfilePicturePath", "Test image saved at: " + file.getAbsolutePath());
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Failed to save test profile picture.", Toast.LENGTH_SHORT).show();
//        }
//    }



//    public void createOrder(String orderDetails) {
//        FirebaseUser user = auth.getCurrentUser();
//        if (user != null) {
//            String userId = user.getUid(); // Fetch the UID of the logged-in user
//            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
//
//            HashMap<String, Object> orderData = new HashMap<>();
//            orderData.put("userId", userId); // Add the UID
//            orderData.put("orderDetails", orderDetails); // Add your order details
//            orderData.put("timestamp", System.currentTimeMillis()); // Add a timestamp
//
//            ordersRef.push().setValue(orderData)
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(this, "Order created successfully!", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(this, "Failed to create order.", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        } else {
//            Toast.makeText(this, "User is not logged in!", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void openDrawer(){
            drawerLayout.openDrawer(GravityCompat.START);
    }
}