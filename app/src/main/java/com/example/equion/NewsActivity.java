package com.example.equion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.webkit.WebViewClient;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        ImageButton homeButton = findViewById(R.id.news_home_button);
        ImageButton newsButton = findViewById(R.id.news_news_button);
        ImageButton chatButton = findViewById(R.id.news_chat_button);
        ImageButton menuButton = findViewById(R.id.news_menu_button);
        ImageButton backButton = findViewById(R.id.news_left_button);
        ImageButton forwardButton = findViewById(R.id.news_right_button);
        ImageButton refreshButton = findViewById(R.id.news_refresh_button);
        WebView webView = findViewById(R.id.news_webview);
        TextView date = findViewById(R.id.news_date);

        // Load the news website in the WebView
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        String  url = "https://news.google.com/search?q=finance&hl=en-US&gl=US&ceid=US%3Aen";
        webView.loadUrl(url);

        // Display the current date
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d", Locale.ENGLISH);
        String formattedDate = dateFormat.format(currentDate);
        date.setText(formattedDate);


        // By clicking Home button
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsActivity.this, MyholdingActivity.class);
                startActivity(intent);
            }
        });
        // By clicking Chat button
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
        // By clicking Menu button
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
        // By clicking Back button
        backButton.setOnClickListener(v -> {
            if (webView.canGoBack()) {
                webView.goBack();
            }
        });
        // By clicking Forward button
        forwardButton.setOnClickListener(v -> {
            if (webView.canGoForward()) {
                webView.goForward();
            }
        });
        // By clicking Refresh button
        refreshButton.setOnClickListener(v -> {
            webView.reload();
        });

    }

    // Add any additional methods or functionality as needed

}
