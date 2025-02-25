package com.nirali.tasty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class AddressActivity extends AppCompatActivity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        String[] statesArray = {"Select State", "Gujarat", "Utarakhand", "Delhi", "Goa"};
        String[] countriesArray = {"Select Country", "India", "Canada", "Mexico", "UK"};

        // Initialize Spinners
        Spinner stateSpinner = findViewById(R.id.state);
        Spinner countrySpinner = findViewById(R.id.country);

        // Set up adapter for state Spinner
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, statesArray);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);

        // Set up adapter for country Spinner
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, countriesArray);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);

        // Set up the Save button functionality
        Button saveButton = findViewById(R.id.save_address_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve input data
                String firstName = ((EditText) findViewById(R.id.first_name)).getText().toString().trim();
                String lastName = ((EditText) findViewById(R.id.last_name)).getText().toString().trim();
                String address = ((EditText) findViewById(R.id.address)).getText().toString().trim();
                String apartment = ((EditText) findViewById(R.id.apartment)).getText().toString().trim();
                String city = ((EditText) findViewById(R.id.city)).getText().toString().trim();
                String postalCode = ((EditText) findViewById(R.id.postal_code)).getText().toString().trim();
                String latitude = ((EditText) findViewById(R.id.latitude)).getText().toString().trim();
                String longitude = ((EditText) findViewById(R.id.longitude)).getText().toString().trim();

                // Get state and country from spinners
                String state = ((Spinner) findViewById(R.id.state)).getSelectedItem().toString();
                String country = ((Spinner) findViewById(R.id.country)).getSelectedItem().toString();

                // Get address type (Home/Office)
                RadioGroup addressTypeGroup = findViewById(R.id.address_type_group);
                int selectedRadioButtonId = addressTypeGroup.getCheckedRadioButtonId();
                String addressType = "";

                if (selectedRadioButtonId == R.id.radio_home) {
                    addressType = "Home";
                } else if (selectedRadioButtonId == R.id.radio_office) {
                    addressType = "Office";
                }

                // Validation
                if (firstName.isEmpty()) {
                    Toast.makeText(AddressActivity.this, "Please enter your first name.", Toast.LENGTH_SHORT).show();
                } else if (lastName.isEmpty()) {
                    Toast.makeText(AddressActivity.this, "Please enter your last name.", Toast.LENGTH_SHORT).show();
                } else if (address.isEmpty()) {
                    Toast.makeText(AddressActivity.this, "Please enter your address.", Toast.LENGTH_SHORT).show();
                } else if (city.isEmpty()) {
                    Toast.makeText(AddressActivity.this, "Please enter your city.", Toast.LENGTH_SHORT).show();
                } else if (postalCode.isEmpty()) {
                    Toast.makeText(AddressActivity.this, "Please enter your postal code.", Toast.LENGTH_SHORT).show();
                } else if (state.equals("Select State")) {
                    Toast.makeText(AddressActivity.this, "Please select a state.", Toast.LENGTH_SHORT).show();
                } else if (country.equals("Select Country")) {
                    Toast.makeText(AddressActivity.this, "Please select a country.", Toast.LENGTH_SHORT).show();
                } else if (addressType.isEmpty()) {
                    Toast.makeText(AddressActivity.this, "Please select an address type (Home or Office).", Toast.LENGTH_SHORT).show();
                } else if (latitude.isEmpty()) {
                    Toast.makeText(AddressActivity.this, "Please select latitude ", Toast.LENGTH_SHORT).show();
                } else if (longitude.isEmpty()) {
                    Toast.makeText(AddressActivity.this, "Please select longitude .", Toast.LENGTH_SHORT).show();
                } else {
                    // All validation passed, proceed with saving data
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("addressType", addressType);  // Use the correct addressType
                    resultIntent.putExtra("firstName", firstName);
                    resultIntent.putExtra("lastName", lastName);
                    resultIntent.putExtra("address", address);
                    resultIntent.putExtra("apartment", apartment);
                    resultIntent.putExtra("city", city);
                    resultIntent.putExtra("state", state);
                    resultIntent.putExtra("country", country);
                    resultIntent.putExtra("postalCode", postalCode);
                   resultIntent.putExtra("latitude",latitude);
                   resultIntent.putExtra("longitude",longitude);

                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });


    }

    }

