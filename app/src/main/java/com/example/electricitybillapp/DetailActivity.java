package com.example.electricitybillapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private DataHelper dbHelper;
    private ListView listView;
    private List<HistoryItem> historyList;
    private HistoryAdapter adapter;
    private DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Back button click listener
        MaterialButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        dbHelper = new DataHelper(this);
        listView = findViewById(R.id.listViewBills);
        historyList = new ArrayList<>();

        loadBillList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBillList();
    }

    private void loadBillList() {
        historyList.clear();

        Cursor cursor = dbHelper.getAllBills();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DataHelper.COLUMN_ID));
                String month = cursor.getString(cursor.getColumnIndexOrThrow(DataHelper.COLUMN_MONTH));
                double finalCost = cursor.getDouble(cursor.getColumnIndexOrThrow(DataHelper.COLUMN_FINAL));

                historyList.add(new HistoryItem(id, month, finalCost));
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new HistoryAdapter(this, historyList);

        // Set the click listener on the adapter
        adapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                HistoryItem item = historyList.get(position);
                Toast.makeText(DetailActivity.this, "Opening details for " + item.getMonth(), Toast.LENGTH_SHORT).show();
                showFloatingDetailsDialog(item.getId());
            }
        });

        listView.setAdapter(adapter);

        if (historyList.isEmpty()) {
            Toast.makeText(this, "No records found. Please save some data first!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Found " + historyList.size() + " records. Tap any card to view details.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to show floating details dialog
    private void showFloatingDetailsDialog(int billId) {
        Cursor cursor = dbHelper.getBillById(billId);
        if (cursor.moveToFirst()) {
            String month = cursor.getString(cursor.getColumnIndexOrThrow(DataHelper.COLUMN_MONTH));
            int unit = cursor.getInt(cursor.getColumnIndexOrThrow(DataHelper.COLUMN_UNIT));
            double total = cursor.getDouble(cursor.getColumnIndexOrThrow(DataHelper.COLUMN_TOTAL));
            double rebate = cursor.getDouble(cursor.getColumnIndexOrThrow(DataHelper.COLUMN_REBATE));
            double finalCost = cursor.getDouble(cursor.getColumnIndexOrThrow(DataHelper.COLUMN_FINAL));

            // Store billId for edit and delete
            final int currentBillId = billId;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_details, null);

            TextView tvMonth = dialogView.findViewById(R.id.detailMonth);
            TextView tvUnit = dialogView.findViewById(R.id.detailUnit);
            TextView tvTotal = dialogView.findViewById(R.id.detailTotal);
            TextView tvRebate = dialogView.findViewById(R.id.detailRebate);
            TextView tvFinal = dialogView.findViewById(R.id.detailFinal);
            MaterialButton btnEdit = dialogView.findViewById(R.id.btnEditDetail);
            MaterialButton btnDelete = dialogView.findViewById(R.id.btnDeleteDetail);
            MaterialButton btnClose = dialogView.findViewById(R.id.btnCloseDetail);

            tvMonth.setText(month);
            tvUnit.setText(unit + " kWh");
            tvTotal.setText("RM " + df.format(total));

            if (rebate > 0) {
                tvRebate.setText(df.format(rebate) + "%");
            } else {
                tvRebate.setText("0% (No rebate)");
            }

            tvFinal.setText("RM " + df.format(finalCost));

            builder.setView(dialogView);
            builder.setCancelable(true);

            AlertDialog dialog = builder.create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }

            dialog.show();

            // EDIT Button
            btnEdit.setOnClickListener(v -> {
                dialog.dismiss();
                Intent intent = new Intent(DetailActivity.this, EditActivity.class);
                intent.putExtra("bill_id", currentBillId);
                startActivity(intent);
            });

            // DELETE Button
            btnDelete.setOnClickListener(v -> {
                dialog.dismiss();
                confirmDelete(currentBillId);
            });

            // CLOSE Button
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }
        cursor.close();
    }


    private void confirmDelete(int billId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Record");
        builder.setMessage("Are you sure you want to delete this record?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            dbHelper.deleteBill(billId);
            loadBillList();
            Toast.makeText(this, "✓ Record deleted successfully!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}