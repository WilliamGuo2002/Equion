package com.example.equion;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;
import java.util.Locale;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {
    private List<StockInfo> stockList;

    public StockAdapter(List<StockInfo> stockList) {
        this.stockList = stockList;
    }

    /**
     * 拖动时调用：ItemTouchHelper.Callback 推荐调用
     */
    public void onItemSelected(ViewHolder holder) {
        holder.onItemSelected();
    }

    /**
     * 拖动结束时调用：ItemTouchHelper.Callback 推荐调用
     */
    public void onItemClear(ViewHolder holder) {
        holder.onItemClear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView symbol, name, price, change;
        LineChart chart;
        Button deleteButton, cancelButton;
        LinearLayout swipeActionContainer;

        public ViewHolder(View view) {
            super(view);
            symbol = view.findViewById(R.id.stock_symbol);
            name = view.findViewById(R.id.stock_name);
            price = view.findViewById(R.id.stock_price);
            change = view.findViewById(R.id.stock_change);
            // 避免类型转换异常，只有当view为LineChart时才赋值，否则为null
            View chartView = view.findViewById(R.id.stock_chart);
            if (chartView instanceof LineChart) {
                chart = (LineChart) chartView;
            } else {
                chart = null;
            }
            deleteButton = view.findViewById(R.id.delete_button);
            cancelButton = view.findViewById(R.id.cancel_button);
            swipeActionContainer = view.findViewById(R.id.swipe_action_container);
        }

        public void onItemSelected() {
            itemView.setBackgroundResource(R.drawable.ripple_effect);
        }

        public void onItemClear() {
            itemView.setBackgroundResource(android.R.color.transparent);
        }

        public void setDragged(boolean isDragged) {
            if (isDragged) {
                itemView.setScaleX(1.05f);
                itemView.setScaleY(1.05f);
            } else {
                itemView.setScaleX(1f);
                itemView.setScaleY(1f);
            }
        }
    }

    @Override
    public StockAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StockInfo stock = stockList.get(position);
        holder.symbol.setText(stock.symbol);
        holder.name.setText(stock.name);
        holder.price.setText(String.format(Locale.US, "%.2f USD", stock.price));

        // 设置涨跌幅及颜色
        double change = stock.changePercent;
        if (change > 0) {
            holder.change.setTextColor(Color.parseColor("#008000")); // 绿色
            holder.change.setText("+" + String.format("%.2f%%", change));
        } else if (change < 0) {
            holder.change.setTextColor(Color.parseColor("#FF0000")); // 红色
            holder.change.setText(String.format("%.2f%%", change));
        } else {
            holder.change.setTextColor(Color.BLACK);
            holder.change.setText("0.00%");
        }

        if (holder.chart != null) {
            if (stock.chartData != null && !stock.chartData.isEmpty()) {
                LineDataSet dataSet = new LineDataSet(stock.chartData, "价格");
                dataSet.setDrawValues(false);
                dataSet.setDrawCircles(false);
                dataSet.setColor(Color.BLUE);

                LineData lineData = new LineData(dataSet);
                holder.chart.setData(lineData);

                // 隐藏所有不必要的图表元素，仅保留折线
                holder.chart.setDescription(null); // 移除描述
                holder.chart.getLegend().setEnabled(false); // 移除图例
                holder.chart.getXAxis().setEnabled(false); // 不显示 X 轴
                holder.chart.getAxisLeft().setEnabled(false); // 不显示左 Y 轴
                holder.chart.getAxisRight().setEnabled(false); // 不显示右 Y 轴
                holder.chart.setTouchEnabled(false); // 禁用触摸
                holder.chart.setScaleEnabled(false); // 禁止缩放
                holder.chart.setPinchZoom(false); // 禁止双指缩放
                holder.chart.setDrawGridBackground(false); // 不显示网格背景
                holder.chart.setDrawBorders(false); // 不显示边框
                holder.chart.setNoDataText(""); // 无数据时也不显示说明文字

                holder.chart.invalidate();  // 刷新图表
            } else {
                holder.chart.clear(); // 无数据时清空图表
            }
        }

        if (stock.swiped) {
            holder.swipeActionContainer.setVisibility(View.VISIBLE);
            holder.chart.setVisibility(View.GONE);
            holder.price.setVisibility(View.GONE);
            holder.change.setVisibility(View.GONE);
        } else {
            holder.swipeActionContainer.setVisibility(View.GONE);
            holder.chart.setVisibility(View.VISIBLE);
            holder.price.setVisibility(View.VISIBLE);
            holder.change.setVisibility(View.VISIBLE);
        }

        holder.deleteButton.setOnClickListener(v -> {
            stockList.remove(position);
            notifyItemRemoved(position);
        });

        holder.cancelButton.setOnClickListener(v -> {
            stock.swiped = false;
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

    // 删除 onViewAttachedToWindow 放大处理，拖动放大逻辑将集中在 setDragged

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.itemView.setScaleX(1f);
        holder.itemView.setScaleY(1f);
    }
}
