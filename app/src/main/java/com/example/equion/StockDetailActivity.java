package com.example.equion;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.CombinedChart;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.*;

public class StockDetailActivity extends AppCompatActivity {

    private CombinedChart combinedChart;
    private TextView aiAnalysisTitle, aiAnalysisContent;
    private Button btn1d, btn5d, btn1m, btn6m, btnYtd, btn1y, btnAll, btnReload, btnConservative, btnModerate, btnAggressive;
    private ImageButton backButton;

    private TextView companyNameText;
    private TextView stockDetailPriceText;
    private String symbol;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stockdetail);

        initViews();

        symbol = getIntent().getStringExtra("symbol");
        name = getIntent().getStringExtra("name");
        companyNameText.setText(name);

        stockDetailPriceText.setText("加载中...");

        updateChartWithInterval("1D");

        setupTimeButtons();
        setupAISection();

        // by clicking back button
        backButton.setOnClickListener(v -> finish());
    }

    private void initViews() {
        combinedChart = findViewById(R.id.stockCombinedChart);

        btn1d = findViewById(R.id.btn_conservative);
        btn5d = findViewById(R.id.btn_moderate);
        btn1m = findViewById(R.id.btn_aggressive);
        btn6m = findViewById(R.id.btn_reload);
        btnYtd = findViewById(R.id.btn_ytd);
        btn1y = findViewById(R.id.btn_1y);
        btnAll = findViewById(R.id.btn_all);
        btnReload = findViewById(R.id.btn_reload);
        btnConservative = findViewById(R.id.btn_conservative);
        btnModerate = findViewById(R.id.btn_moderate);
        btnAggressive = findViewById(R.id.btn_aggressive);
        backButton = findViewById(R.id.StockDetailActivityBackButton);

        aiAnalysisTitle = findViewById(R.id.aiAnalysisTitle);
        aiAnalysisContent = findViewById(R.id.aiAnalysisContent);

        companyNameText = findViewById(R.id.StockDetailActivityCompanyName);
        stockDetailPriceText = findViewById(R.id.stockDetailPrice);
    }

    private void setupTimeButtons() {
        View.OnClickListener listener = v -> {
            String selectedInterval = "";

            int id = v.getId();
            if (id == R.id.btn_conservative) {
                selectedInterval = "1D";
            } else if (id == R.id.btn_moderate) {
                selectedInterval = "5D";
            } else if (id == R.id.btn_aggressive) {
                selectedInterval = "1M";
            } else if (id == R.id.btn_reload) {
                selectedInterval = "6M";
            } else if (id == R.id.btn_ytd) {
                selectedInterval = "YTD";
            } else if (id == R.id.btn_1y) {
                selectedInterval = "1Y";
            } else if (id == R.id.btn_all) {
                selectedInterval = "ALL";
            }

            updateChartWithInterval(selectedInterval);
        };

        btn1d.setOnClickListener(listener);
        btn5d.setOnClickListener(listener);
        btn1m.setOnClickListener(listener);
        btn6m.setOnClickListener(listener);
        btnYtd.setOnClickListener(listener);
        btn1y.setOnClickListener(listener);
        btnAll.setOnClickListener(listener);
    }

    private void updateChartWithInterval(String interval) {
        String chartInterval;
        switch (interval) {
            case "1D":
                chartInterval = "5min";
                break;
            case "5D":
                chartInterval = "30min";
                break;
            case "1M":
                chartInterval = "1day";
                break;
            case "6M":
                chartInterval = "1week";
                break;
            case "YTD":
                chartInterval = "1week";
                break;
            case "1Y":
                chartInterval = "1week";
                break;
            case "ALL":
                chartInterval = "1month";
                break;
            default:
                chartInterval = "1day";
        }

        fetchChart(chartInterval);

        int selectedColor = getResources().getColor(R.color.text_color);
        int defaultColor = getResources().getColor(R.color.button_color);

        btn1d.setTextColor(interval.equals("1D") ? selectedColor : defaultColor);
        btn5d.setTextColor(interval.equals("5D") ? selectedColor : defaultColor);
        btn1m.setTextColor(interval.equals("1M") ? selectedColor : defaultColor);
        btn6m.setTextColor(interval.equals("6M") ? selectedColor : defaultColor);
        btnYtd.setTextColor(interval.equals("YTD") ? selectedColor : defaultColor);
        btn1y.setTextColor(interval.equals("1Y") ? selectedColor : defaultColor);
        btnAll.setTextColor(interval.equals("ALL") ? selectedColor : defaultColor);
    }

    private void setupAISection() {
        aiAnalysisTitle.setText("AI 分析与交易建议");
        aiAnalysisContent.setText("生成中...");
        fetchAIAnalysis();
    }

    private void fetchAIAnalysis() {
        new AsyncTask<Void, Void, String>() {
            protected String doInBackground(Void... voids) {
                try {
                    // 获取最近 2 周日期范围
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    String toDate = sdf.format(cal.getTime());
                    cal.add(java.util.Calendar.DAY_OF_MONTH, -14);
                    String fromDate = sdf.format(cal.getTime());

                    // 请求 Finnhub 新闻数据
                    String newsUrl = "https://finnhub.io/api/v1/company-news?symbol=" + symbol +
                                     "&from=" + fromDate + "&to=" + toDate +
                                     "&token=d09lgfpr01qnv9cjnpl0d09lgfpr01qnv9cjnplg";
                    URL newsApiUrl = new URL(newsUrl);
                    HttpURLConnection newsConn = (HttpURLConnection) newsApiUrl.openConnection();
                    newsConn.setRequestMethod("GET");

                    BufferedReader newsReader = new BufferedReader(new InputStreamReader(newsConn.getInputStream()));
                    StringBuilder newsResult = new StringBuilder();
                    String line;
                    while ((line = newsReader.readLine()) != null) newsResult.append(line);
                    newsReader.close();

                    JSONArray newsArray = new JSONArray(newsResult.toString());
                    StringBuilder newsBlock = new StringBuilder();
                    for (int i = 0; i < Math.min(newsArray.length(), 5); i++) {
                        JSONObject newsItem = newsArray.getJSONObject(i);
                        newsBlock.append("- ").append(newsItem.getString("headline")).append("\n");
                    }

                    String prompt = name + " (" + symbol + ") 股票近期新闻如下：\n" + newsBlock +
                            "\n请基于以上内容生成一条简洁明了的投资建议（100-200字）。";

                    JSONObject requestJson = new JSONObject();
                    JSONArray contents = new JSONArray();
                    JSONObject part = new JSONObject();
                    part.put("text", prompt);
                    JSONArray parts = new JSONArray();
                    parts.put(part);
                    JSONObject content = new JSONObject();
                    content.put("parts", parts);
                    contents.put(content);
                    requestJson.put("contents", contents);

                    String API_KEY = BuildConfig.GEMINI_API_KEY;
                    URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(requestJson.toString().getBytes("UTF-8"));

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    while ((line = reader.readLine()) != null) result.append(line);
                    reader.close();

                    JSONObject response = new JSONObject(result.toString());
                    return response
                            .getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text");

                } catch (Exception e) {
                    Log.e("GeminiAI", "AI分析失败", e);
                    return "生成失败，请稍后重试。";
                }
            }

            protected void onPostExecute(String responseText) {
                aiAnalysisContent.setText(responseText);
            }
        }.execute();
    }

    private void fetchQuote() {
        new AsyncTask<Void, Void, String>() {
            protected String doInBackground(Void... voids) {
                try {
                    String urlStr = "https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=d09lgfpr01qnv9cjnpl0d09lgfpr01qnv9cjnplg";
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) result.append(line);
                    reader.close();
                    return result.toString();
                } catch (Exception e) {
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    double current = json.getDouble("c");
                    stockDetailPriceText.setText(String.format("$%.2f", current));
                } catch (Exception e) {
                    stockDetailPriceText.setText("获取失败");
                }
            }
        }.execute();
    }

    private void fetchChart(String interval) {
        new AsyncTask<Void, Void, ArrayList<Entry>>() {
            protected ArrayList<Entry> doInBackground(Void... voids) {
                ArrayList<Entry> entries = new ArrayList<>();
                try {
                    String urlStr = "https://api.twelvedata.com/time_series?symbol=" + symbol + "&interval=" + interval + "&apikey=fe6b50f31bee472ea13c3275737ef04d";
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) result.append(line);
                    reader.close();

                    JSONObject json = new JSONObject(result.toString());
                    JSONArray values = json.getJSONArray("values");
                    for (int i = values.length() - 1; i >= 0; i--) {
                        JSONObject point = values.getJSONObject(i);
                        float close = Float.parseFloat(point.getString("close"));
                        entries.add(new Entry(values.length() - 1 - i, close));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return entries;
            }

            protected void onPostExecute(ArrayList<Entry> entries) {
                LineDataSet lineDataSet = new LineDataSet(entries, "股价走势");
                lineDataSet.setColor(getResources().getColor(R.color.button_color));
                lineDataSet.setDrawCircles(false);
                lineDataSet.setLineWidth(2f);
                lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet.setDrawValues(false);
                lineDataSet.setHighlightEnabled(true);
                lineDataSet.setDrawHorizontalHighlightIndicator(false);
                lineDataSet.setDrawVerticalHighlightIndicator(true);

                LineData lineData = new LineData(lineDataSet);
                CombinedData data = new CombinedData();
                data.setData(lineData);
                combinedChart.setData(data);

                combinedChart.getDescription().setEnabled(false);
                combinedChart.getAxisLeft().setEnabled(false);
                combinedChart.getAxisRight().setEnabled(true);
                combinedChart.getAxisRight().setDrawGridLines(false);
                combinedChart.getXAxis().setEnabled(false);
                combinedChart.getLegend().setEnabled(false);

                combinedChart.setTouchEnabled(true);
                combinedChart.setHighlightPerTapEnabled(true);
                combinedChart.setHighlightPerDragEnabled(true);

                MarkerView marker = new StockMarkerView(StockDetailActivity.this, R.layout.marker_view_layout, entries);
                marker.setChartView(combinedChart);
                combinedChart.setMarker(marker);

                combinedChart.invalidate();
            }
        }.execute();
    }
}