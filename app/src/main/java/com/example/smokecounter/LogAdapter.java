package com.example.smokecounter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private List<SmokeManager.DailyLog> logs;

    public LogAdapter(List<SmokeManager.DailyLog> logs) {
        this.logs = logs;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        SmokeManager.DailyLog log = logs.get(position);
        holder.dateText.setText("Date: " + log.date);
        holder.countText.setText("Count: " + log.count);
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView dateText, countText;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(android.R.id.text1);
            countText = itemView.findViewById(android.R.id.text2);
        }
    }
}
