package com.example.equion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    // This class is currently empty, but you can implement registration logic here.
    // For example, you can use Firebase Authentication to register new users.
    private EditText emailEditText, passwordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components and set up listeners for registration actions.
        // You can also handle user input validation and Firebase registration here.

        ImageButton backButton = findViewById(R.id.register_back_button);
        emailEditText = findViewById(R.id.register_email_bar);
        passwordEditText = findViewById(R.id.register_password_bar);

        // by clicking back button
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close the RegisterActivity
        });
    }

}
