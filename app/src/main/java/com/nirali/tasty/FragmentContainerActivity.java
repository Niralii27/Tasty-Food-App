package com.nirali.tasty;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class FragmentContainerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new CartFragment()) // Load the Fragment
                    .commit();
        }
    }
}