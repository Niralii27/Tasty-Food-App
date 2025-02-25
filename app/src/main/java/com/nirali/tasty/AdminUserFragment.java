package com.nirali.tasty;

import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AdminUserFragment extends Fragment {

    RecyclerView recyclerView;
    private AdminUserAdapter adapter;
    private List<User> userList;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_user, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        adapter = new AdminUserAdapter(userList);
        recyclerView.setAdapter(adapter);

        // Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Profile");

        fetchUsers();


        return view;
    }

    private void fetchUsers() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear(); // Clear list to avoid duplicates
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        user.setId(dataSnapshot.getKey());
                        userList.add(user);
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter of data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch users: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

        private List<User> userList;

        public AdminUserAdapter(List<User> userList) {
            this.userList = userList;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_admin, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = userList.get(position);
            holder.userName.setText(user.getName());
            holder.userEmail.setText(user.getEmail());
//            holder.userPhone.setText(user.getNumber());
//            holder.userAddress.setText(user.getBirthDate());

            Glide.with(holder.itemView.getContext())
                    .load(user.getProfilePicture()) // URL of the profile image
                    .placeholder(R.drawable.like) // Placeholder image
                    .error(R.drawable.order) // Error image
                    .into(holder.img1);
            holder.relative1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("AdminUserFragment", "User ID: " + user.getId());

                    Intent intent = new Intent(holder.itemView.getContext(), admin_profile_Details.class);

                    intent.putExtra("userId", user.getId());
                    intent.putExtra("userName", user.getName());
                    intent.putExtra("userEmail", user.getEmail());
                    intent.putExtra("usernumber",user.getNumber());
                    intent.putExtra("userBirthDate",user.getBirthDate());
                    intent.putExtra("userProfilePicture", user.getProfilePicture());

                    holder.itemView.getContext().startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        public class UserViewHolder extends RecyclerView.ViewHolder {
            TextView userName, userEmail, userPhone, userAddress;
            ImageView img1;
            RelativeLayout relative1;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                userName = itemView.findViewById(R.id.name);
                userEmail = itemView.findViewById(R.id.email);
                img1 = itemView.findViewById(R.id.img1);
                relative1 = itemView.findViewById(R.id.relative1);
//                userPhone = itemView.findViewById(R.id.number);
//                userAddress = itemView.findViewById(R.id.birthDate);
            }
        }
    }

}