package com.example.equion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Profile section
        ShapeableImageView profilePic = findViewById(R.id.menu_profile_pic);
        TextView username = findViewById(R.id.menu_username);
        TextView userEmail = findViewById(R.id.menu_user_email);

        // Profile buttons
        Button editProfileButton = findViewById(R.id.menu_edit_profile_button);

        // Settings buttons
        Button appearanceSettingButton = findViewById(R.id.menu_appearance_setting_button);
        Button notificationSettingButton = findViewById(R.id.menu_notification_setting_button);
        Button shareButton = findViewById(R.id.menu_share_button);
        Button supportButton = findViewById(R.id.menu_support_button);
        Button contactButton = findViewById(R.id.menu_contact_button);
        Button copyrightInfoButton = findViewById(R.id.menu_copyrightinfo_button);
        Button logoutButton = findViewById(R.id.menu_logout_button);

        // Navigation bar buttons
        ImageButton homeButton = findViewById(R.id.menu_home_button);
        ImageButton newsButton = findViewById(R.id.menu_news_button);
        ImageButton chatButton = findViewById(R.id.menu_chat_button);
        ImageButton menuButton = findViewById(R.id.menu_menu_button);

        // Initialize UI components and set up listeners for menu actions.
        // by clicking home button
        homeButton.setOnClickListener(v -> {
            // Navigate to HomeActivity
            startActivity(new Intent(MenuActivity.this, MyholdingActivity.class));
            overridePendingTransition(0, 0);
            finish(); // Close MenuActivity
            overridePendingTransition(0, 0);
        });

        // by clicking news button
        newsButton.setOnClickListener(v -> {
            // Navigate to NewsActivity
            startActivity(new Intent(MenuActivity.this, NewsActivity.class));
            overridePendingTransition(0, 0);
            finish(); // Close MenuActivity
            overridePendingTransition(0, 0);
        });

        // by clicking chat button
        chatButton.setOnClickListener(v -> {
            // Navigate to ChatActivity
            startActivity(new Intent(MenuActivity.this, ChatActivity.class));
            overridePendingTransition(0, 0);
            finish(); // Close MenuActivity
            overridePendingTransition(0, 0);
        });
    }

    // Add any additional methods or functionality as needed
}
