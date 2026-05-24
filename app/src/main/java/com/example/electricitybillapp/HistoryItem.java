package com.example.electricitybillapp;

public class HistoryItem {
    private int id;
    private String month;
    private double amount;

    public HistoryItem(int id, String month, double amount) {
        this.id = id;
        this.month = month;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public String getMonth() {
        return month;
    }

    public double getAmount() {
        return amount;
    }
}