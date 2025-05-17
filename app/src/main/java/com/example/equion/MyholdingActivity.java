package com.example.equion;
import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.example.equion.ChatActivity;
import com.example.equion.MenuActivity;
import com.example.equion.NewsActivity;
import com.example.equion.R;
import com.example.equion.StockAdapter;
import com.example.equion.StockInfo;
import com.github.mikephil.charting.data.Entry;
import android.view.ViewGroup;

public class MyholdingActivity extends AppCompatActivity {

    private List<StockInfo> stockList = new ArrayList<>();
    private StockAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_myholding);

        LinearLayout marketContainer = findViewById(R.id.market_info_container);
        android.widget.HorizontalScrollView scrollView = findViewById(R.id.market_scroll_view);
        String twelveApiKey = "fe6b50f31bee472ea13c3275737ef04d"; // 替换为你自己的 API Key

        String[] symbols = {"SPY", "QQQ", "DIA"};
        String[] names = {"S&P 500", "NASDAQ", "Dow Jones"};

        for (int i = 0; i < symbols.length; i++) {
            final int index = i;
            String url = "https://api.twelvedata.com/quote?symbol=" + symbols[i] + "&apikey=" + twelveApiKey;

            Request request = new Request.Builder().url(url).build();
            OkHttpClient client = new OkHttpClient();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(MyholdingActivity.this, "大盘数据获取失败", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) return;

                    try {
                        String jsonData = response.body().string();
                        Log.d("MarketDataRaw", "原始返回: " + jsonData);
                        JSONObject json = new JSONObject(jsonData);

                        double priceVal = Double.parseDouble(json.getString("close"));
                        double percentVal = Double.parseDouble(json.getString("percent_change")) * 100;

                        runOnUiThread(() -> {
                            TextView textView = new TextView(MyholdingActivity.this);
                            textView.setTextSize(14);
                            textView.setPadding(32, 0, 32, 0);

                            double changeVal = percentVal;
                            String price = String.format("%.2f", priceVal);
                            String changeStr = String.format("%.2f", Math.abs(changeVal));
                            String change = changeVal < 0 ? "-" + changeStr : "+" + changeStr;
                            SpannableString spannable = new SpannableString(names[index] + ": " + price + " (" + change + "%)");
                            int start = spannable.toString().indexOf('(');
                            int end = spannable.toString().indexOf(')') + 1;
                            int color = changeVal < 0 ? Color.RED : Color.parseColor("#008000");
                            spannable.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            textView.setText(spannable);
                            textView.setTextColor(Color.BLACK);
                            marketContainer.addView(textView);
                        });
                    } catch (Exception e) {
                        Log.e("MarketData", "解析失败: " + e.getMessage(), e);  // 打印详细错误
                        runOnUiThread(() -> Toast.makeText(MyholdingActivity.this, "大盘数据解析失败", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        }

        // 自动横向滚动（无限循环）
        scrollView.post(() -> {
            final int scrollSpeed = 2; // px per step
            final int delay = 30; // ms

            scrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int maxScroll = marketContainer.getWidth() - scrollView.getWidth();
                    int currentX = scrollView.getScrollX();

                    if (currentX >= maxScroll) {
                        scrollView.scrollTo(0, 0); // 回到开头实现无限循环
                    } else {
                        scrollView.scrollBy(scrollSpeed, 0);
                    }

                    scrollView.postDelayed(this, delay);
                }
            }, delay);
        });

        ImageButton addButton = findViewById(R.id.myholding_add_button);
        ImageButton homeButton = findViewById(R.id.myholding_home_button);
        ImageButton newsButton = findViewById(R.id.myholding_news_button);
        ImageButton chatButton = findViewById(R.id.myholding_chat_button);
        ImageButton menuButton = findViewById(R.id.myholding_menu_button);
        TextView date = findViewById(R.id.myholding_date);
        RecyclerView recyclerView = findViewById(R.id.myholding_list);
        adapter = new StockAdapter(stockList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 添加拖拽排序和左滑删除功能
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(stockList, fromPosition, toPosition);
                adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                stockList.get(position).swiped = true;
                adapter.notifyItemChanged(position);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+ 高斯模糊
            View glassView = findViewById(R.id.myholding_navbar_layout);
            RenderEffect blurEffect = RenderEffect.createBlurEffect(
                    80f, 80f, Shader.TileMode.CLAMP);
            glassView.setRenderEffect(blurEffect);
        }*/

        // Display the current date
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d", Locale.ENGLISH);
        String formattedDate = dateFormat.format(currentDate);
        date.setText(formattedDate);

        // By clicking Add button
        addButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MyholdingActivity.this);
            builder.setTitle("输入美股代码");

            final EditText input = new EditText(MyholdingActivity.this);
            input.setHint("如 AAPL, MSFT");
            builder.setView(input);

            builder.setPositiveButton("添加", (dialog, which) -> {
                String symbol = input.getText().toString().trim().toUpperCase();
                fetchStockData(symbol); // 下一步来写这个
            });

            builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

            builder.show();
        });

        // By clicking News button
        newsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for the news button
                // For example, start a new activity or show a dialog
                Intent intent = new Intent(MyholdingActivity.this, NewsActivity.class);
                startActivity(intent);
            }
        });
        // By clicking Chat button
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for the chat button
                // For example, start a new activity or show a dialog
                Intent intent = new Intent(MyholdingActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
        // By clicking Menu button
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for the menu button
                // For example, start a new activity or show a dialog
                Intent intent = new Intent(MyholdingActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

    }
    private void fetchStockData(String symbol) {
        // 添加重复检查
        for (StockInfo stock : stockList) {
            if (stock.symbol.equalsIgnoreCase(symbol)) {
                Toast.makeText(MyholdingActivity.this, "不可重复添加", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        OkHttpClient client = new OkHttpClient();
        String apiKey = "d09lgfpr01qnv9cjnpl0d09lgfpr01qnv9cjnplg";

        // 第一步：获取公司名称
        String profileUrl = "https://finnhub.io/api/v1/stock/profile2?symbol=" + symbol + "&token=" + apiKey;
        Request profileRequest = new Request.Builder().url(profileUrl).build();

        client.newCall(profileRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                    Toast.makeText(MyholdingActivity.this, "公司信息请求失败", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                        Toast.makeText(MyholdingActivity.this, "公司信息获取失败: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                try {
                    String profileData = response.body().string();
                    JSONObject profileJson = new JSONObject(profileData);
                    String name = profileJson.optString("name", symbol);

                    // 第二步：获取股价信息
                    String quoteUrl = "https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + apiKey;
                    Request quoteRequest = new Request.Builder().url(quoteUrl).build();

                    client.newCall(quoteRequest).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(() ->
                                Toast.makeText(MyholdingActivity.this, "股价请求失败", Toast.LENGTH_SHORT).show()
                            );
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                runOnUiThread(() ->
                                    Toast.makeText(MyholdingActivity.this, "股价获取失败: " + response.code(), Toast.LENGTH_SHORT).show()
                                );
                                return;
                            }

                            try {
                                String quoteData = response.body().string();
                                JSONObject quoteJson = new JSONObject(quoteData);

                                if (!quoteJson.has("c")) {
                                    runOnUiThread(() ->
                                        Toast.makeText(MyholdingActivity.this, "未找到该股票代码", Toast.LENGTH_SHORT).show()
                                    );
                                    return;
                                }

                                double price = quoteJson.getDouble("c");
                                double previousClose = quoteJson.getDouble("pc");
                                double changePercent = ((price - previousClose) / previousClose) * 100.0;

                                // 第三步：使用 Twelve Data 获取图表数据
                                String twelveApiKey = "fe6b50f31bee472ea13c3275737ef04d"; // 替换为你的 Twelve Data API Key
                                String chartUrl = "https://api.twelvedata.com/time_series?symbol=" + symbol + "&interval=5min&outputsize=30&apikey=" + twelveApiKey;

                                Request chartRequest = new Request.Builder().url(chartUrl).build();

                                client.newCall(chartRequest).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        runOnUiThread(() -> Toast.makeText(MyholdingActivity.this, "图表请求失败", Toast.LENGTH_SHORT).show());
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if (!response.isSuccessful()) {
                                            runOnUiThread(() -> Toast.makeText(MyholdingActivity.this, "图表数据获取失败: " + response.code(), Toast.LENGTH_SHORT).show());
                                            return;
                                        }

                                        try {
                                            String chartData = response.body().string();
                                            JSONObject chartJson = new JSONObject(chartData);
                                            JSONArray values = chartJson.optJSONArray("values");

                                            if (values == null || values.length() == 0) {
                                                runOnUiThread(() -> Toast.makeText(MyholdingActivity.this, "图表数据为空", Toast.LENGTH_SHORT).show());
                                                return;
                                            }

                                            List<Entry> entries = new ArrayList<>();
                                            for (int i = values.length() - 1; i >= 0; i--) {
                                                JSONObject point = values.getJSONObject(i);
                                                float close = Float.parseFloat(point.getString("close"));
                                                entries.add(new Entry(values.length() - 1 - i, close));
                                            }

                                            runOnUiThread(() -> {
                                                stockList.add(new StockInfo(symbol, name, price, changePercent, entries));
                                                adapter.notifyItemInserted(stockList.size() - 1);
                                            });

                                        } catch (Exception e) {
                                            runOnUiThread(() -> Toast.makeText(MyholdingActivity.this, "图表解析失败", Toast.LENGTH_SHORT).show());
                                        }
                                    }
                                });

                            } catch (Exception e) {
                                runOnUiThread(() ->
                                    Toast.makeText(MyholdingActivity.this, "股价解析失败", Toast.LENGTH_SHORT).show()
                                );
                            }
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(() ->
                        Toast.makeText(MyholdingActivity.this, "公司信息解析失败", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}