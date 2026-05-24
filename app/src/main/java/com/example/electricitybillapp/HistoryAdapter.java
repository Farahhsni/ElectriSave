package com.example.electricitybillapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class HistoryAdapter extends ArrayAdapter<HistoryItem> {

    private Context context;
    private List<HistoryItem> items;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public HistoryAdapter(Context context, List<HistoryItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_history, parent, false);
        }

        HistoryItem item = items.get(position);

        TextView tvMonth = convertView.findViewById(R.id.tvMonth);
        TextView tvAmount = convertView.findViewById(R.id.tvAmount);
        MaterialCardView cardView = convertView.findViewById(R.id.cardView);

        tvMonth.setText(item.getMonth());
        tvAmount.setText("RM " + String.format("%.2f", item.getAmount()));

        // Make the card clickable
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onItemClick(position);
                }
            }
        });

        return convertView;
    }
}