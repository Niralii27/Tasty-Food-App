package com.nirali.tasty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button btn_reset,btn_back;
    EditText edt_email;

    ProgressBar progressBar;

    FirebaseAuth mAuth;
    String stremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btn_back = findViewById(R.id.btn_back);
        btn_reset = findViewById(R.id.btn_reset);
        edt_email = findViewById(R.id.edt_email);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 stremail = edt_email.getText().toString().trim();

                if (!TextUtils.isEmpty(stremail)){
                    ResetPassword();

                }
                else {
                    edt_email.setError("Email field can't be empty");
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private  void ResetPassword(){
        progressBar.setVisibility(View.INVISIBLE);
        btn_reset.setVisibility(View.INVISIBLE);

        mAuth.sendPasswordResetEmail(stremail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ForgotPasswordActivity.this, "Reset Password link has been sent to your registered Email", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);

                Intent intent = new Intent(ForgotPasswordActivity.this,Login.class);
                startActivity(intent);
                finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPasswordActivity.this, "Error :- "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        btn_reset.setVisibility(View.VISIBLE);


                    }
                });

    }

}