package com.nirali.tasty;

import android.app.Application;

import com.google.firebase.database.collection.BuildConfig;

import org.osmdroid.config.Configuration;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize OSMDroid configuration
        Configuration.getInstance().setUserAgentValue("com.nirali.tasty"); // Use your application's package name
    }
}
