package com.nirali.tasty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminHomeActivity extends AppCompatActivity {

    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    NavigationView navigationView,nav_view;
    ImageView img_profile;
    LinearLayout linear1,linear2;
    TextView nav_email,nav_name;

    FirebaseUser user;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

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


        linear1 = findViewById(R.id.linear1);
        linear2 = findViewById(R.id.linear2);
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
        }
//        FirebaseAuth.getInstance().signOut();
        nav_view.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                // Perform logout
                auth.signOut();
                Intent intent = new Intent(AdminHomeActivity.this, Login.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });
        linear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CartFragment()).commit();
            }
        });

        linear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Admin_contact()).commit();

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AdminFeedbackFragment()).commit();
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AdminFeedbackFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AdminProductFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AdminOrderFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AdminUserFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CartFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AdminDashboardFragment()).commit();




        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AdminDashboardFragment()).commit();
            } else if (itemId == R.id.menu) {
//                Intent intent = new Intent(AdminHomeActivity.this, CategoryActivity.class);
//                startActivity(intent);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AdminProductFragment()).commit();

            } else if (itemId == R.id.order) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AdminOrderFragment()).commit();
            } else if (itemId == R.id.user) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AdminUserFragment()).commit();
            } else {
                return false;
            }

            return true;
        });


    }
    public void openDrawer(){
        drawerLayout.openDrawer(GravityCompat.START);
    }
}