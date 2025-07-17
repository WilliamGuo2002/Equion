package com.example.equion;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.MPPointF;

public class StockMarkerView extends MarkerView {

    private final TextView textView;

    public StockMarkerView(Context context, int layoutResource, java.util.List<Entry> entries) {
        super(context, layoutResource);
        textView = findViewById(R.id.marker_text);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        String label = String.format("时间点：%s\n价格：%.2f", (int) e.getX(), e.getY());
        textView.setText(label);
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight());
    }
}