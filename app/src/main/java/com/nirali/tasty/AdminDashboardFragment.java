package com.nirali.tasty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardFragment extends Fragment {
    ImageView img_profile;
    DrawerLayout drawerLayout;

    NavigationView navigationView, nav_view;
//    private RecyclerView recyclerView;
//    private List<DeliveryBoy> deliveryBoyList;
//    private DeliveryBoyAdapter adapter;
//    private DatabaseReference deliveryBoyRef;


    LinearLayout linear2;
    Button delivery_boy;

    FirebaseUser user;
    FirebaseAuth auth;



    TextView nav_email, nav_name;

    private DatabaseReference categoriesRef, productsRef, ordersRef, usersRef;

    private TextView totalCategory, totalProduct, totalOrders, totalUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

//        recyclerView = view.findViewById(R.id.recycler2);
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//
//        deliveryBoyList = new ArrayList<>();
//        adapter = new DeliveryBoyAdapter(requireContext(), deliveryBoyList);
//        recyclerView.setAdapter(adapter);
//
//        deliveryBoyRef = FirebaseDatabase.getInstance().getReference("Delivery_Boy");

//        fetchDeliveryBoyData();

        // Initialize views
        img_profile = view.findViewById(R.id.img_profile);
        drawerLayout = view.findViewById(R.id.drawer_layout);
        nav_view = view.findViewById(R.id.nav_view);

        linear2 = view.findViewById(R.id.linear2);
        View headerView = nav_view.getHeaderView(0);
        nav_email = headerView.findViewById(R.id.nav_email);
        nav_name = headerView.findViewById(R.id.nav_name);
//        delivery_boy = view.findViewById(R.id.delivery_boy);

        totalCategory = view.findViewById(R.id.total_category);
        totalProduct = view.findViewById(R.id.total_product);
        totalOrders = view.findViewById(R.id.total_orders);
        totalUser = view.findViewById(R.id.total_user);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        categoriesRef = database.getReference("Category");
        productsRef = database.getReference("Product");
        ordersRef = database.getReference("Orders");
        usersRef = database.getReference("Profile");

        // Fetch and update totals
        fetchTotalCount(categoriesRef, totalCategory);
        fetchTotalCount(productsRef, totalProduct);
        fetchTotalCount(ordersRef, totalOrders);
        fetchTotalCount(usersRef, totalUser);

//        delivery_boy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(),DeliveryBoyActivity.class);
//                startActivity(intent);
//            }
//        });
        // Initialize FirebaseAuth and get current user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser(); // Initialize user

        // Check if user is logged in
        if (user == null) {
            Intent intent = new Intent(getContext(), Login.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            // Set email and name in the navigation drawer header
            nav_email.setText(user.getEmail());
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                nav_name.setText(displayName);
            } else {
                nav_name.setText("No Name Provided"); // Fallback if display name is not set
            }
        }

        // Set NavigationView item click listener
        nav_view.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                // Logout user
                auth.signOut();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
                getActivity().finish(); // Close the activity
                return true;
            }
            return false;
        });

        linear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Admin_contact())
                        .commit();

            }
        });
        // Open drawer on profile image click
        img_profile.setOnClickListener(view1 -> drawerLayout.openDrawer(GravityCompat.START));

        return view;
    }
//    private void fetchDeliveryBoyData() {
//        deliveryBoyRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                deliveryBoyList.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    DeliveryBoy deliveryBoy = dataSnapshot.getValue(DeliveryBoy.class);
//                    deliveryBoyList.add(deliveryBoy);
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle error
//            }
//        });
//    }
    private void fetchTotalCount(DatabaseReference ref, TextView textView) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Count the number of child nodes
                    long count = snapshot.getChildrenCount();
                    textView.setText(String.valueOf(count));
                } else {
                    textView.setText("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                textView.setText("Error");
            }
        });
    }



//    public class DeliveryBoyAdapter extends RecyclerView.Adapter<DeliveryBoyAdapter.ViewHolder> {
//
//        private Context context;
//        private List<DeliveryBoy> deliveryBoyList;
//
//        public DeliveryBoyAdapter(Context context, List<DeliveryBoy> deliveryBoyList) {
//            this.context = context;
//            this.deliveryBoyList = deliveryBoyList;
//        }
//
//        @NonNull
//        @Override
//        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(context).inflate(R.layout.delivery_boy_item, parent, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//            DeliveryBoy deliveryBoy = deliveryBoyList.get(position);
//
//            holder.name.setText(deliveryBoy.getName());
////            holder.number.setText(deliveryBoy.getNumber());
////            holder.status.setChecked(deliveryBoy.isStatus());
//
//            Glide.with(context)
//                    .load(deliveryBoy.getImagePath()) // Load image from Firebase storage or URL
//                    .placeholder(R.drawable.order) // Placeholder image
//                    .into(holder.image);
//
//            holder.itemView.setOnClickListener(v -> {
//                Intent intent = new Intent(context, DeliveryBoyActivity.class);
//                intent.putExtra("id", deliveryBoy.getId()); // Pass the deliveryBoyId
//
//                intent.putExtra("name", deliveryBoy.getName());
//                intent.putExtra("number", deliveryBoy.getNumber());
//                intent.putExtra("status", deliveryBoy.isStatus());
//                intent.putExtra("img", deliveryBoy.getImagePath());
//
//
//                intent.putExtra("imagePath", deliveryBoy.getImagePath());
//                // Add more fields as required
//                context.startActivity(intent);
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return deliveryBoyList.size();
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            TextView name, number,imagepath;
//            ImageView image;
//            Switch status;
//
//            public ViewHolder(@NonNull View itemView) {
//                super(itemView);
//                name = itemView.findViewById(R.id.name);
//                number = itemView.findViewById(R.id.deliverynumber);
//                image = itemView.findViewById(R.id.iurl);
//                imagepath = itemView.findViewById(R.id.DeliveryImage);
//                status = itemView.findViewById(R.id.switch2);
//            }
//        }
//    }
//

}
