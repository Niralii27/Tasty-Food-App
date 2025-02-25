package com.nirali.tasty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity1 extends AppCompatActivity {

    Button add_new_address_button, proceed_button;
    ImageView btn_back;
    List<String> addressList;
    AddressAdapter adapter;
    private String selectedAddress;

    private static final int ADD_ADDRESS_REQUEST = 1;
    private static final String PREFS_NAME = "AddressPrefs"; // SharedPreferences name
    private static final String ADDRESS_LIST_KEY = "addressList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout1);

        add_new_address_button = findViewById(R.id.add_new_address_button);
        proceed_button = findViewById(R.id.proceed_button);
        btn_back = findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        // Load the address list from SharedPreferences
        addressList = loadAddressList();

        ArrayList<String> itemIDs = getIntent().getStringArrayListExtra("itemIDs");
        ArrayList<String> itemNames = getIntent().getStringArrayListExtra("itemNames");
        ArrayList<Integer> quantities = getIntent().getIntegerArrayListExtra("quantities");
        float totalAmount = getIntent().getFloatExtra("totalAmount", 0);

        // Log the received data to verify
        Log.d("CheckoutActivity1", "Received itemIDs: " + (itemIDs != null ? itemIDs : "No itemIDs passed"));
        Log.d("CheckoutActivity1", "Received itemNames: " + (itemNames != null ? itemNames : "No itemNames passed"));
        Log.d("CheckoutActivity1", "Received quantities: " + (quantities != null ? quantities : "No quantities passed"));
        Log.d("CheckoutActivity1", "Received totalAmount: " + totalAmount);

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddressAdapter(addressList);
        recyclerView.setAdapter(adapter);

        // CheckoutActivity1.java
        proceed_button.setOnClickListener(v -> {
            // Check if data is passed properly in logs
            Log.d("CheckoutActivity1", "Passing itemIDs: " + itemIDs);
            Log.d("CheckoutActivity1", "Passing itemNames: " + itemNames);
            Log.d("CheckoutActivity1", "Passing quantities: " + quantities);
            Log.d("CheckoutActivity1", "Passing totalAmount: " + totalAmount);
            Log.d("CheckoutActivity1", "Passing selectedAddress: " + (selectedAddress != null ? selectedAddress : "No address selected"));

            // Create the intent for CheckoutActivity2
            Intent intent = new Intent(CheckoutActivity1.this, CheckoutActivity2.class);

            // Pass all data to the next activity
            intent.putStringArrayListExtra("itemIDs", itemIDs);
            intent.putStringArrayListExtra("itemNames", itemNames);
            intent.putIntegerArrayListExtra("quantities", quantities);
            intent.putExtra("totalAmount", totalAmount);
            intent.putExtra("address", selectedAddress != null ? selectedAddress : "No address selected");

            // Start CheckoutActivity2
            startActivity(intent);
        });


        // Setup ItemTouchHelper to delete addresses
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                addressList.remove(position);
                adapter.notifyItemRemoved(position);

                // Save the updated address list to SharedPreferences
                saveAddressList(addressList);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Set up button to add a new address
        add_new_address_button.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddressActivity.class);
            startActivityIfNeeded(intent, ADD_ADDRESS_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("CheckoutActivity1", "onActivityResult triggered");

        if (requestCode == ADD_ADDRESS_REQUEST && resultCode == RESULT_OK && data != null) {
            Log.d("CheckoutActivity1", "Data received: " + data.toString());

            String fullAddress = data.getStringExtra("addressType") + ": " +
                    data.getStringExtra("firstName") + " " + data.getStringExtra("lastName") + "\n" +
                    data.getStringExtra("address") + ", " + data.getStringExtra("apartment") + "\n" + "Latitude" +
                    data.getStringExtra("latitude") + ", "+"longitude:" + data.getStringExtra("longitude") + ", " + "\n" +
                    data.getStringExtra("city") + ", " + data.getStringExtra("state") + ", " +
                    data.getStringExtra("country") + "\nPostal Code: " + data.getStringExtra("postalCode");

            addressList.add(fullAddress);
            saveAddressList(addressList);

            adapter.notifyDataSetChanged();
        } else {
            Log.d("CheckoutActivity1", "No data received or result not OK");
        }
    }

    private void saveAddressList(List<String> addressList) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ADDRESS_LIST_KEY, String.join(";", addressList));
        editor.apply();
    }

    private List<String> loadAddressList() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedAddresses = sharedPreferences.getString(ADDRESS_LIST_KEY, "");
        List<String> loadedAddressList = new ArrayList<>();

        if (!savedAddresses.isEmpty()) {
            String[] addresses = savedAddresses.split(";");
            for (String address : addresses) {
                loadedAddressList.add(address);
            }
        }
        return loadedAddressList;
    }

    public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

        private List<String> addressList;
        private int selectedPosition = 0;

        public AddressAdapter(List<String> addressList) {
            this.addressList = addressList;
        }

        @NonNull
        @Override
        public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_item, parent, false);
            return new AddressViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
            String address = addressList.get(position);

            // Set address details
            holder.addressTitle.setText(position == 0 ? "Office" : "Home");
            holder.addressDetails.setText(address);

            // Set the radio button checked state based on selected position
            holder.radioSelectAddress.setChecked(position == selectedPosition);

            // Handle radio button clicks to update selection
            holder.radioSelectAddress.setOnClickListener(v -> {
                selectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();

                selectedAddress = address;
            });
        }

        @Override
        public int getItemCount() {
            return addressList.size();
        }

        public class AddressViewHolder extends RecyclerView.ViewHolder {
            TextView addressTitle;
            TextView addressDetails;
            RadioButton radioSelectAddress;

            public AddressViewHolder(@NonNull View itemView) {
                super(itemView);
                addressTitle = itemView.findViewById(R.id.address_title);
                addressDetails = itemView.findViewById(R.id.address_details);
                radioSelectAddress = itemView.findViewById(R.id.radio_select_address);
            }
        }
    }
}
