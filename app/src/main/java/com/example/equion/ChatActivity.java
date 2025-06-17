package com.example.equion;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    private LinearLayout chatContainer;
    // private static final String API_KEY = "";
    String API_KEY = BuildConfig.GEMINI_API_KEY;
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1/models/gemini-1.0-flash:generateContent?key=" + API_KEY;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ImageButton homeButton = findViewById(R.id.chat_home_button);
        ImageButton newsButton = findViewById(R.id.chat_news_button);
        ImageButton chatButton = findViewById(R.id.chat_chat_button);
        ImageButton menuButton = findViewById(R.id.chat_menu_button);
        ImageButton sendButton = findViewById(R.id.chat_send_button);
        ImageButton clearButton = findViewById(R.id.chat_delete_button);
        EditText userInput = findViewById(R.id.chat_edittext);
        ScrollView scrollView = findViewById(R.id.chat_scrollview);
        chatContainer = findViewById(R.id.chat_container);

        // By clicking Home button
        homeButton.setOnClickListener(view -> {
            Intent intent = new Intent(ChatActivity.this, MyholdingActivity.class);
            startActivity(intent);
        });
        // By clicking News button
        newsButton.setOnClickListener(view -> {
            Intent intent = new Intent(ChatActivity.this, NewsActivity.class);
            startActivity(intent);
        });
        // By clicking Menu button
        menuButton.setOnClickListener(view -> {
            Intent intent = new Intent(ChatActivity.this, MenuActivity.class);
            startActivity(intent);
        });


        // send message
        sendButton.setOnClickListener(v -> {
            String userText = userInput.getText().toString().trim();
            if (!userText.isEmpty()) {
                addMessage("\ud83e\udd16 You: " + userText, true);
                userInput.setText("");
                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                fetchGeminiReply(userText);
            }
        });

        // delete chat
        clearButton.setOnClickListener(v -> chatContainer.removeAllViews());
    }

    private void fetchGeminiReply(String userText) {
        try {
            JSONObject payload = new JSONObject();
            JSONArray contents = new JSONArray();

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");

            // JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();
            part.put("text", userText);
            parts.put(part);


            JSONObject content = new JSONObject();
            content.put("parts", parts);
            contents.put(content);

            payload.put("contents", contents);
            /*
            userMessage.put("parts", parts);
            contents.put(userMessage);
            payload.put("contents", contents);*/

            Log.d("ORION_DEBUG", "Request JSON: " + payload.toString());

            RequestBody body = RequestBody.create(
                    payload.toString(),
                    MediaType.get("application/json")
            );

            Request request = new Request.Builder()
                    .url(ENDPOINT)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> addMessage("\u274c Orion: 网络请求失败。", false));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String jsonData = response.body().string();
                            Log.d("ORION_DEBUG", "Response JSON: " + jsonData);
                            JSONObject json = new JSONObject(jsonData);
                            JSONArray candidates = json.getJSONArray("candidates");
                            JSONObject candidate = candidates.getJSONObject(0);
                            JSONObject content = candidate.getJSONObject("content");
                            JSONArray parts = content.getJSONArray("parts");
                            String reply = parts.getJSONObject(0).getString("text");

                            runOnUiThread(() -> addMessage("\ud83e\udd16 Orion: " + reply, false));
                        } catch (Exception e) {
                            runOnUiThread(() -> addMessage("\u274c Orion: 解析回复失败。", false));
                        }
                    } else {
                        runOnUiThread(() -> addMessage("\u274c Orion: 接口响应失败（" + response.code() + ")。", false));
                    }
                }
            });
        } catch (Exception e) {
            addMessage("\u274c Orion: 请求构造失败。", false);
        }
    }

    private void addMessage(String text, boolean isUser) {
        TextView message = new TextView(this);
        message.setText(text);
        message.setTextSize(16);
        message.setPadding(24, 16, 24, 16);
        message.setTextColor(Color.BLACK);
        message.setTextAlignment(isUser ? View.TEXT_ALIGNMENT_TEXT_END : View.TEXT_ALIGNMENT_TEXT_START);

        chatContainer.addView(message);
    }
}
