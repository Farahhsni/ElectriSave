package com.example.electricitybillapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] items;
    private String hintText = "Please select month";

    public CustomSpinnerAdapter(Context context, int textViewResourceId, String[] objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.items = objects;
    }

    @Override
    public int getCount() {
        return items.length + 1; // Add 1 for hint
    }

    @Override
    public String getItem(int position) {
        if (position == 0) {
            return hintText;
        }
        return items[position - 1];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        if (position == 0) {
            view.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            view.setText(hintText);
        } else {
            view.setTextColor(context.getResources().getColor(android.R.color.black));
            view.setText(items[position - 1]);
        }
        view.setTextSize(16);
        view.setPadding(16, 16, 16, 16);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        if (position == 0) {
            view.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            view.setText(hintText);
            view.setEnabled(false); // Disable clicking on hint
        } else {
            view.setTextColor(context.getResources().getColor(android.R.color.black));
            view.setText(items[position - 1]);
        }
        view.setTextSize(16);
        view.setPadding(16, 16, 16, 16);
        view.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        return view;
    }
}

