package com.nirali.tasty;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Admin_contact extends Fragment {

    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private List<Contact> contactList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_admin_contact, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        contactList = new ArrayList<>();
        adapter = new ContactAdapter(contactList);
        recyclerView.setAdapter(adapter);

        fetchContacts();
        return  view;
    }

    private void fetchContacts() {
        DatabaseReference contactRef = FirebaseDatabase.getInstance().getReference("Contacts");

        contactRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactList.clear(); // Clear previous data

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get contact data from Firebase
                    Contact contact = snapshot.getValue(Contact.class);
                    if (contact != null) {
                        contactList.add(contact);
                    }
                }

                // Notify adapter to refresh the data
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Log.e("FirebaseError", "Error fetching contact data", databaseError.toException());
            }
        });
    }

    public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

        private List<Contact> contactList;

        // Constructor
        public ContactAdapter(List<Contact> contactList) {
            this.contactList = contactList;
        }

        @NonNull
        @Override
        public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate the item layout
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_contact, parent, false);
            return new ContactViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
            // Get the current contact
            Contact contact = contactList.get(position);

            // Bind data to the views
            holder.txtName.setText("Name: " + contact.getName());
            holder.txtEmail.setText("Email: " + contact.getEmail());
            holder.txtPhone.setText("Phone: " + contact.getNumber());
            holder.txtAddress.setText("Address: " + contact.getAddress());
            holder.txtMessage.setText("Message: " + contact.getMessage());
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        // ViewHolder for the contact item
        public class ContactViewHolder extends RecyclerView.ViewHolder {
            TextView txtName, txtEmail, txtPhone, txtAddress, txtMessage;

            public ContactViewHolder(View itemView) {
                super(itemView);
                txtName = itemView.findViewById(R.id.txt_name);
                txtEmail = itemView.findViewById(R.id.txt_email);
                txtPhone = itemView.findViewById(R.id.txt_number);
                txtAddress = itemView.findViewById(R.id.txt_address);
                txtMessage = itemView.findViewById(R.id.txt_message);
            }
        }
    }

}