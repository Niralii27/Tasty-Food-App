package com.nirali.tasty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity {

    EditText edt_email,edt_password;

    Button btn_login;
    TextView txt_signup,txt_forgot;
    Switch switch1;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_REMEMBER_ME = "RememberMe";

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        mAuth = FirebaseAuth.getInstance();
        btn_login = findViewById(R.id.btn_login);
        txt_signup = findViewById(R.id.txt_signup);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        txt_forgot = findViewById(R.id.txt_forgot);
        switch1 = findViewById(R.id.switch1);

     /*   sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean(KEY_REMEMBER_ME,false)){
            Intent intent = new Intent(getApplicationContext(),MainActivity2.class);
            startActivity(intent);
            finish();
        }*/

        txt_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              String email,password;
               /* email = edt_email.getText().toString().trim();
                password = edt_password.getText().toString().trim();

                if (switch1.isChecked()){
                    editor.putBoolean(KEY_REMEMBER_ME,true);
                    editor.apply();
                }*/
                email = String.valueOf(edt_email.getText());
                password = String.valueOf(edt_password.getText());

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Login.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(Login.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }


                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String email,password;
                        email = String.valueOf(edt_email.getText());
                        password = String.valueOf(edt_password.getText());


                        if(TextUtils.isEmpty(email)){
                            Toast.makeText(Login.this, "Enter Email", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(password)){
                            Toast.makeText(Login.this, "Enter Password", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information

                                         /*   Toast.makeText(Login.this, "Login Successfully.",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Login.this, MainActivity2.class);
                                            startActivity(intent);*/


                                            if (email.equals("admin@gmail.com") && password.equals("Admin@2717")) {
                                                Toast.makeText(Login.this, "Admin Login Successfully.",
                                                        Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Login.this, AdminHomeActivity.class);
                                                startActivity(intent);



                                            } else {
                                                Toast.makeText(Login.this, "Login Successfully.",
                                                        Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Login.this, MainActivity2.class);
                                                startActivity(intent);
                                            }

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(Login.this, "Unable to log in. Check your details and try again.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                });

            }
        });
        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });


    }
}