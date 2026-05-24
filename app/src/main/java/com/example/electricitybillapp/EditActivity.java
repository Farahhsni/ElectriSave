package com.example.electricitybillapp;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.text.DecimalFormat;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;

public class EditActivity extends AppCompatActivity {

    private DataHelper dbHelper;
    private Spinner spinnerMonth;
    private EditText etUnit, etRebate;
    private MaterialButton btnUpdate, btnCancel;
    private TextView tvTotalCharges, tvFinalCost;
    private MaterialCardView resultCard;
    private int billId;
    private DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

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
        billId = getIntent().getIntExtra("bill_id", -1);

        // Initialize views
        spinnerMonth = findViewById(R.id.spinnerMonth);
        etUnit = findViewById(R.id.etUnit);
        etRebate = findViewById(R.id.etRebate);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);
        tvTotalCharges = findViewById(R.id.tvTotalCharges);
        tvFinalCost = findViewById(R.id.tvFinalCost);
        resultCard = findViewById(R.id.resultCard);

        // Setup months
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);
        spinnerMonth.setAdapter(adapter);

        // Load existing bill data
        loadBillData();

        // Update button
        btnUpdate.setOnClickListener(v -> updateBill());

        // Cancel button
        btnCancel.setOnClickListener(v -> finish());
    }

    public void setSupportActionBar(Toolbar toolbar) {
    }

    private void loadBillData() {
        Cursor cursor = dbHelper.getBillById(billId);
        if (cursor.moveToFirst()) {
            String month = cursor.getString(cursor.getColumnIndexOrThrow(DataHelper.COLUMN_MONTH));
            int unit = cursor.getInt(cursor.getColumnIndexOrThrow(DataHelper.COLUMN_UNIT));
            double rebate = cursor.getDouble(cursor.getColumnIndexOrThrow(DataHelper.COLUMN_REBATE));
            double total = cursor.getDouble(cursor.getColumnIndexOrThrow(DataHelper.COLUMN_TOTAL));
            double finalCost = cursor.getDouble(cursor.getColumnIndexOrThrow(DataHelper.COLUMN_FINAL));

            // Set month spinner selection
            for (int i = 0; i < spinnerMonth.getAdapter().getCount(); i++) {
                if (spinnerMonth.getAdapter().getItem(i).toString().equals(month)) {
                    spinnerMonth.setSelection(i);
                    break;
                }
            }

            etUnit.setText(String.valueOf(unit));
            etRebate.setText(String.valueOf(rebate));
            tvTotalCharges.setText("RM " + df.format(total));
            tvFinalCost.setText("RM " + df.format(finalCost));
            resultCard.setVisibility(View.VISIBLE);
        }
        cursor.close();
    }

    private double calculateTotalCharges(int unit) {
        double total = 0;
        int remaining = unit;
        if (remaining > 0) {
            int block = Math.min(remaining, 200);
            total += block * 0.218;
            remaining -= block;
        }
        if (remaining > 0) {
            int block = Math.min(remaining, 100);
            total += block * 0.334;
            remaining -= block;
        }
        if (remaining > 0) {
            int block = Math.min(remaining, 300);
            total += block * 0.516;
            remaining -= block;
        }
        if (remaining > 0) {
            total += remaining * 0.546;
        }
        return total;
    }

    private void updateBill() {
        String month = spinnerMonth.getSelectedItem().toString();
        String unitStr = etUnit.getText().toString().trim();
        String rebateStr = etRebate.getText().toString().trim();

        if (unitStr.isEmpty()) {
            Toast.makeText(this, "Please enter unit value", Toast.LENGTH_SHORT).show();
            return;
        }

        int unit = Integer.parseInt(unitStr);
        if (unit < 1 || unit > 1000) {
            Toast.makeText(this, "Unit must be 1-1000 kWh", Toast.LENGTH_SHORT).show();
            return;
        }

        double rebate = rebateStr.isEmpty() ? 0 : Double.parseDouble(rebateStr);
        if (rebate < 0 || rebate > 5) {
            Toast.makeText(this, "Rebate must be 0-5%", Toast.LENGTH_SHORT).show();
            return;
        }

        double total = calculateTotalCharges(unit);
        double finalCost = total - (total * rebate / 100);

        int result = dbHelper.updateBill(billId, month, unit, total, rebate, finalCost);
        if (result > 0) {
            Toast.makeText(this, "✓ Data updated successfully!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
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

