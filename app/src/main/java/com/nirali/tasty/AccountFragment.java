package com.nirali.tasty;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;


public class AccountFragment extends Fragment {

    CardView card1,card2,card4,card3;
    ImageView profile_img;

    DatabaseReference databaseReference;
    FirebaseUser user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account,container,false);

        card1 = view.findViewById(R.id.card1);
        card2 = view.findViewById(R.id.card2);
        card4 = view.findViewById(R.id.card4);
        card3 = view.findViewById(R.id.card3);
        profile_img = view.findViewById(R.id.profile_img);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Profile");

        if (user != null) {
            loadProfilePicture(user.getUid());
        } else {
            Toast.makeText(getContext(), "No user is currently logged in.", Toast.LENGTH_SHORT).show();
        }

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),PersonalDetails.class);
                startActivity(intent);
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),HelpActivity.class);
                startActivity(intent);
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ContactUsActivity.class);
                startActivity(intent);
            }
        });

        return view;

    }


    private void loadProfilePicture(String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Profile");

        if (userId != null) {
            databaseReference.child(userId).get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    String profilePicture = snapshot.child("profilePicture").getValue(String.class);

                    if (profilePicture != null && !profilePicture.isEmpty()) {
                        // Proceed to load the image from the local path
                        File imgFile = new File(profilePicture);
                        if (imgFile.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            profile_img.setImageBitmap(bitmap);
                        } else {
                            // Handle case where file doesn't exist
                            profile_img.setImageResource(R.drawable.profile); // Set default image
                        }
                    } else {
                        // Handle the case where profilePicture is null or empty
                        profile_img.setImageResource(R.drawable.profile); // Set default image
                    }
                } else {
                    // If the snapshot doesn't exist, set a default image
                    profile_img.setImageResource(R.drawable.profile); // Set default image
                }
            }).addOnFailureListener(e -> {
                Log.e("AccountFragment", "Error loading profile picture", e);
                // Handle any error gracefully
                profile_img.setImageResource(R.drawable.profile); // Set default image
            });
        } else {
            // If userId is null, set default image
            profile_img.setImageResource(R.drawable.profile);
        }
    }

}