package com.example.equion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

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
        TextView date = findViewById(R.id.news_date);

        RecyclerView recyclerView = findViewById(R.id.news_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setBackgroundResource(android.R.color.transparent);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://finnhub.io/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        FinnhubApi api = retrofit.create(FinnhubApi.class);
        Call<List<NewsItem>> call = api.getGeneralNews("general", "d09lgfpr01qnv9cjnpl0d09lgfpr01qnv9cjnplg");
        call.enqueue(new Callback<List<NewsItem>>() {
            @Override
            public void onResponse(Call<List<NewsItem>> call, Response<List<NewsItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NewsAdapter adapter = new NewsAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<NewsItem>> call, Throwable t) {
                Toast.makeText(NewsActivity.this, "Failed to load news", Toast.LENGTH_SHORT).show();
            }
        });

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
                overridePendingTransition(0, 0);  // 禁用切换动画
            }
        });
        // By clicking Chat button
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsActivity.this, ChatActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);  // 禁用切换动画
            }
        });
        // By clicking Menu button
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsActivity.this, MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);  // 禁用切换动画
            }
        });
        // Back, Forward buttons removed because no WebView
        backButton.setVisibility(View.GONE);
        forwardButton.setVisibility(View.GONE);
        refreshButton.setOnClickListener(v -> {
            // Refresh news list
            Call<List<NewsItem>> refreshCall = api.getGeneralNews("general", "d09lgfpr01qnv9cjnpl0d09lgfpr01qnv9cjnplg");
            refreshCall.enqueue(new Callback<List<NewsItem>>() {
                @Override
                public void onResponse(Call<List<NewsItem>> call, Response<List<NewsItem>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        NewsAdapter adapter = new NewsAdapter(response.body());
                        recyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<List<NewsItem>> call, Throwable t) {
                    Toast.makeText(NewsActivity.this, "Failed to refresh news", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    public interface FinnhubApi {
        @GET("news")
        Call<List<NewsItem>> getGeneralNews(@Query("category") String category, @Query("token") String apiKey);
    }

    public static class NewsItem {
        String headline;
        String datetime;
        String url;
        String image;
    }

    public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
        private final List<NewsItem> newsList;

        public NewsAdapter(List<NewsItem> newsList) {
            this.newsList = newsList;
        }

        @Override
        public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
            return new NewsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(NewsViewHolder holder, int position) {
            NewsItem newsItem = newsList.get(position);
            holder.title.setText(newsItem.headline);
            if (newsItem.image != null && !newsItem.image.isEmpty()) {
                Glide.with(holder.image.getContext())
                        .load(newsItem.image)
                        .into(holder.image);
            } else {
                holder.image.setImageResource(R.drawable.placeholder_image);
            }
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), NewsDetailActivity.class);
                intent.putExtra("url", newsItem.url);
                holder.itemView.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return newsList.size();
        }

        class NewsViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            ImageView image;

            public NewsViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.news_item_title);
                image = itemView.findViewById(R.id.news_item_image);
            }
        }
    }
}
