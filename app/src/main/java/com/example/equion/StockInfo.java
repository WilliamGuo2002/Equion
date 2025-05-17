package com.example.equion;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

public class StockInfo {
    String symbol;
    String name;
    double price;
    double changePercent;
    public List<Entry> chartData;
    public boolean swiped = false;

    public StockInfo(String symbol, String name, double price, double changePercent, List<Entry> chartData) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.changePercent = changePercent;
        this.chartData = chartData;
    }
}
