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
import java.util.HashMap;
import java.util.Map;

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

        Button submitButton = findViewById(R.id.register_submit_button);
        submitButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "请输入邮箱和密码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(RegisterActivity.this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseAuth auth = FirebaseController.getInstance().getAuth();
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // 创建用户文档，包含基础字段
                            Map<String, Object> data = new HashMap<>();
                            data.put("email", email);
                            data.put("username", email.split("@")[0]);
                            data.put("avatarUrl", "");
                            data.put("createdAt", com.google.firebase.Timestamp.now());
                            FirebaseController.getInstance().getDb()
                                .collection("users")
                                .document(user.getUid())
                                .set(data);
                        }
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "注册失败: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        });
    }

}
