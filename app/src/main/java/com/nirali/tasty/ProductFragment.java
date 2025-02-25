package com.nirali.tasty;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class ProductFragment extends Fragment {

    RecyclerView recycler3;

    ArrayList<Category> categoryList = new ArrayList<>();


    DatabaseReference databaseReference;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu,container,false);

        recycler3 = view.findViewById(R.id.recycler2);
        recycler3.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseReference = FirebaseDatabase.getInstance().getReference("Category");

        fetchCategories();
        return view;
    }

    private void fetchCategories() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear(); // Clear the list before adding new data

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    categoryList.add(category); // Add category to list
                }

                // Set adapter after data is fetched
                ProductFragment.MyAdapter adapter = new ProductFragment.MyAdapter();
                recycler3.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors here if needed
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<ProductFragment.MyAdapter.MyHolder> {
        @NonNull
        @Override
        public ProductFragment.MyAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.category_item, parent, false);
            ProductFragment.MyAdapter.MyHolder myHolder = new ProductFragment.MyAdapter.MyHolder(view);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ProductFragment.MyAdapter.MyHolder myHolder, int position) {

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
                    Intent intent = new Intent(getContext(), CategoryActivity.class);
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
}