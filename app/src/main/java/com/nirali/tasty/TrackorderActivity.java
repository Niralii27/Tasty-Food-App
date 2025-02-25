package com.nirali.tasty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

public class TrackorderActivity extends AppCompatActivity {

    private ImageView prepared, packed, shipping, placed;
    private TextView trackingIdText;
    private ProgressBar progressBar;
    private DatabaseReference ordersRef;
    private MapView mapView;

    private GeoPoint defaultLocation = null; // Default location initialized as null

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trackorder);

        // Initialize UI components
        prepared = findViewById(R.id.prepared);
        packed = findViewById(R.id.packed);
        shipping = findViewById(R.id.shipping);
        placed = findViewById(R.id.placed);
        trackingIdText = findViewById(R.id.trackingIdText);
        progressBar = findViewById(R.id.progressBar);

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Initialize Firebase reference
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        // Set the default location from the address
        setDefaultLocation();

        // Fetch the orderId passed from the previous activity
        String orderId = getIntent().getStringExtra("orderID");
        Log.d("TrackorderActivity", "Received orderID: " + orderId);

        if (orderId != null) {
            fetchOrderData(orderId);
        } else {
            Toast.makeText(this, "Order ID not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchOrderData(String orderId) {
        progressBar.setVisibility(View.VISIBLE);

        ordersRef.child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    String status = dataSnapshot.child("status").getValue(String.class);
                    String trackingId = dataSnapshot.child("trackingNumber").getValue(String.class);
                    String address = dataSnapshot.child("address").getValue(String.class);

                    // Update status and tracking ID on UI
                    updateOrderStatus(status);
                    updateTrackingId(trackingId);

                    if (address != null && !address.isEmpty()) {
                        // Extract latitude and longitude from the address
                        GeoPoint extractedGeoPoint = extractCoordinatesFromAddress(address);

                        if (extractedGeoPoint != null) {
                            // Add the default location for the driver and the order's location separately
                            addDriverIcon(defaultLocation);
                            addOrderIcon(extractedGeoPoint);

                            // Draw a route between the driver and the order
                            highlightRoute(defaultLocation, extractedGeoPoint);
                        } else {
                            Toast.makeText(TrackorderActivity.this, "Unable to extract coordinates from the address.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TrackorderActivity.this, "Order address not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TrackorderActivity.this, "Order not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TrackorderActivity.this, "Error fetching order data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private GeoPoint extractCoordinatesFromAddress(String address) {
        try {
            // Find the latitude and longitude substrings
            String[] parts = address.split("Latitude");
            if (parts.length > 1) {
                String[] latLongParts = parts[1].split(", longitude:");
                if (latLongParts.length == 2) {
                    double latitude = Double.parseDouble(latLongParts[0].trim());
                    double longitude = Double.parseDouble(latLongParts[1].split(",")[0].trim());
                    return new GeoPoint(latitude, longitude);
                }
            }
        } catch (Exception e) {
            Log.e("ExtractCoordinates", "Error extracting coordinates: " + e.getMessage());
        }
        return null; // Return null if parsing fails
    }


    private boolean isNetworkAvailable() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection available.", Toast.LENGTH_SHORT).show();

        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
//    private void setDefaultLocation() {
//        String defaultAddress = "150 Feet Ring Road, Backbone park, 301, Shyam apartment, Rajkot, Gujarat 360002";
//
//        fetchCoordinatesFromAddress(defaultAddress, new OnGeocodeResult() {
//            @Override
//            public void onResult(GeoPoint geoPoint) {
//                if (geoPoint != null) {
//                    defaultLocation = geoPoint;
//                    Log.d("Default Location", "Latitude: " + geoPoint.getLatitude() + ", Longitude: " + geoPoint.getLongitude());
//                    mapView.getController().setCenter(defaultLocation);
//                    mapView.getController().setZoom(15);
//                    addDriverIcon(defaultLocation);
//                } else {
//                    // Fallback to hardcoded coordinates
//                    defaultLocation = new GeoPoint(22.3039, 70.8022); // Rajkot's general coordinates
//                    mapView.getController().setCenter(defaultLocation);
//                    mapView.getController().setZoom(15);
//                    addDriverIcon(defaultLocation);
//
//                    Toast.makeText(TrackorderActivity.this, "Using fallback coordinates for the default address.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//private void setDefaultLocation() {
//    defaultLocation = new GeoPoint(22.268855052621554,  70.77802854082695); // Rajkot coordinates 22.268855052621554, 70.77802854082695   22.270913930042166, 70.77997400432903
//    mapView.getController().setCenter(defaultLocation);
//    mapView.getController().setZoom(15);
//    addDriverIcon(defaultLocation);
//}

    private void setDefaultLocation() {
        defaultLocation = new GeoPoint(22.268855052621554, 70.77802854082695); // Rajkot coordinates
        Log.d("DefaultLocation", "Default location set: " + defaultLocation.getLatitude() + ", " + defaultLocation.getLongitude());

        mapView.getController().setCenter(defaultLocation);
        mapView.getController().setZoom(15);
        addDriverIcon(defaultLocation);
    }







    private void addDriverIcon(GeoPoint location) {
        if (location != null) {
            // Do not clear overlays here
            Marker driverMarker = new Marker(mapView);
            driverMarker.setPosition(location);
            driverMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            driverMarker.setIcon(getResources().getDrawable(R.drawable.driver));
            driverMarker.setTitle("Driver Location");
            mapView.getOverlays().add(driverMarker);

            mapView.invalidate(); // Refresh the map
        }
    }

    private void addOrderIcon(GeoPoint location) {
        if (location != null) {
            // Do not clear overlays here
            Marker orderMarker = new Marker(mapView);
            orderMarker.setPosition(location);
            orderMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            orderMarker.setIcon(getResources().getDrawable(R.drawable.locat));
            orderMarker.setTitle("Order Location");
            mapView.getOverlays().add(orderMarker);

            mapView.invalidate(); // Refresh the map
        }
    }


    private void updateOrderStatus(String status) {
        resetStatusBackgrounds();

        if (status == null || status.isEmpty()) {
            Toast.makeText(this, "Order status is not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        int highlightedColor = getResources().getColor(R.color.orange);
        int defaultColor = getResources().getColor(android.R.color.white);

        switch (status.toLowerCase()) {
            case "prepared":
                prepared.setBackgroundColor(highlightedColor);
                packed.setBackgroundColor(defaultColor);
                shipping.setBackgroundColor(defaultColor);
                placed.setBackgroundColor(defaultColor);
                break;
            case "packed":
                prepared.setBackgroundColor(highlightedColor);
                packed.setBackgroundColor(highlightedColor);
                shipping.setBackgroundColor(defaultColor);
                placed.setBackgroundColor(defaultColor);
                break;
            case "shipping":
                prepared.setBackgroundColor(highlightedColor);
                packed.setBackgroundColor(highlightedColor);
                shipping.setBackgroundColor(highlightedColor);
                placed.setBackgroundColor(defaultColor);
                break;
            case "placed":
                prepared.setBackgroundColor(highlightedColor);
                packed.setBackgroundColor(highlightedColor);
                shipping.setBackgroundColor(highlightedColor);
                placed.setBackgroundColor(highlightedColor);
                break;
            default:
                Toast.makeText(this, "Unknown status: " + status, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void updateTrackingId(String trackingId) {
        if (trackingId != null && !trackingId.isEmpty()) {
            trackingIdText.setText("Tracking ID: " + trackingId);
        } else {
            trackingIdText.setText("Tracking ID: Not available");
        }
    }

    private void resetStatusBackgrounds() {
        int defaultColor = getResources().getColor(android.R.color.white);
        prepared.setBackgroundColor(defaultColor);
        packed.setBackgroundColor(defaultColor);
        shipping.setBackgroundColor(defaultColor);
        placed.setBackgroundColor(defaultColor);
    }

    private void updateMap(GeoPoint orderLocation) {
        if (orderLocation == null) {
            Log.e("UpdateMap", "Order location is null, skipping map update.");
            return;
        }
        Log.d("UpdateMap", "Updating map with order location: Latitude = " + orderLocation.getLatitude() + ", Longitude = " + orderLocation.getLongitude());

        // Add order icon to the map
        addOrderIcon(orderLocation);

        // Center the map on the order location
        mapView.getController().setCenter(orderLocation);
        mapView.getController().setZoom(15);

        // Refresh map overlays
        mapView.invalidate();
    }




    private void highlightRoute(GeoPoint start, GeoPoint end) {
        String url = "https://router.project-osrm.org/route/v1/driving/"
                + start.getLongitude() + "," + start.getLatitude() + ";"
                + end.getLongitude() + "," + end.getLatitude()
                + "?geometries=geojson";

        new Thread(() -> {
            try {
                java.net.URL routeUrl = new java.net.URL(url);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) routeUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    java.io.InputStream inputStream = connection.getInputStream();
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    org.json.JSONObject jsonObject = new org.json.JSONObject(result.toString());
                    org.json.JSONArray routes = jsonObject.getJSONArray("routes");
                    if (routes.length() > 0) {
                        org.json.JSONObject route = routes.getJSONObject(0);
                        org.json.JSONObject geometry = route.getJSONObject("geometry");
                        org.json.JSONArray coordinates = geometry.getJSONArray("coordinates");

                        // Convert GeoJSON coordinates to GeoPoints
                        java.util.ArrayList<GeoPoint> geoPoints = new java.util.ArrayList<>();
                        for (int i = 0; i < coordinates.length(); i++) {
                            org.json.JSONArray coord = coordinates.getJSONArray(i);
                            double lon = coord.getDouble(0);
                            double lat = coord.getDouble(1);
                            geoPoints.add(new GeoPoint(lat, lon));
                        }

                        // Draw the route on the map
                        runOnUiThread(() -> {
                            Polyline roadOverlay = new Polyline();
                            roadOverlay.setPoints(geoPoints);
                            roadOverlay.setColor(getResources().getColor(R.color.orange));
                            roadOverlay.setWidth(8f);
                            mapView.getOverlays().add(roadOverlay);
                            mapView.invalidate(); // Refresh the map
                        });
                    }
                } else {
                    Log.e("RouteAPI", "Failed to fetch route. Response code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("RouteAPI", "Error: " + e.getMessage());
            }
        }).start();
    }


    private void fetchCoordinatesFromAddress(String address, OnGeocodeResult callback) {
        String url = "https://nominatim.openstreetmap.org/search?q=" + address.replace(" ", "%20") + "&format=json&limit=1";

        new Thread(() -> {
            try {
                java.net.URL geocodeUrl = new java.net.URL(url);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) geocodeUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                int responseCode = connection.getResponseCode();
                Log.d("Geocode API", "Response Code: " + responseCode);

                if (responseCode == 200) {
                    java.io.InputStream inputStream = connection.getInputStream();
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("Geocode API", "Response: " + result.toString());

                    org.json.JSONArray jsonArray = new org.json.JSONArray(result.toString());
                    if (jsonArray.length() > 0) {
                        org.json.JSONObject location = jsonArray.getJSONObject(0);
                        double lat = location.getDouble("lat");
                        double lon = location.getDouble("lon");

                        runOnUiThread(() -> callback.onResult(new GeoPoint(lat, lon)));
                    } else {
                        runOnUiThread(() -> callback.onResult(null));
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Geocode API failed with response code: " + responseCode, Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                Log.e("Geocode API", "Error: " + e.getMessage());
                runOnUiThread(() -> callback.onResult(null));
            }
        }).start();

    }

    interface OnGeocodeResult {
        void onResult(GeoPoint geoPoint);
    }
}
