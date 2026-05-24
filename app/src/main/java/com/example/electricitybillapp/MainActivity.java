package com.example.electricitybillapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.text.DecimalFormat;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class MainActivity extends AppCompatActivity {

    private DataHelper dbHelper;
    private Spinner spinnerMonth;
    private EditText etUnit, etRebate;
    private MaterialButton btnCalculate, btnSave, btnViewList, btnAbout;
    private DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DataHelper(this);

        // Initialize views
        spinnerMonth = findViewById(R.id.spinnerMonth);
        etUnit = findViewById(R.id.etUnit);
        etRebate = findViewById(R.id.etRebate);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnSave = findViewById(R.id.btnSave);
        btnViewList = findViewById(R.id.btnViewList);
        btnAbout = findViewById(R.id.btnAbout);

        // Setup months with CUSTOM ADAPTER for black text
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, R.layout.spinner_item, months);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        // Set default selection to hint (position 0)
        spinnerMonth.setSelection(0, false);

        // Calculate button
        btnCalculate.setOnClickListener(v -> calculateBill());

        // Save button
        btnSave.setOnClickListener(v -> saveToDatabase());

        // View List button
        btnViewList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            startActivity(intent);
        });

        // About button
        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });
    }

    private void calculateBill() {
        String selectedMonth = spinnerMonth.getSelectedItem().toString();
        if (selectedMonth.equals("Please select month") || spinnerMonth.getSelectedItemPosition() == 0) {
            showErrorDialog("Please select a month first!");
            return;
        }

        String unitStr = etUnit.getText().toString().trim();
        String rebateStr = etRebate.getText().toString().trim();

        if (unitStr.isEmpty()) {
            showErrorDialog("Please enter electricity unit (kWh)");
            return;
        }

        int unit = Integer.parseInt(unitStr);
        if (unit < 1 || unit > 1000) {
            showErrorDialog("Unit must be between 1 - 1000 kWh");
            return;
        }

        double rebate = 0;
        if (!rebateStr.isEmpty()) {
            rebate = Double.parseDouble(rebateStr);
            if (rebate < 0 || rebate > 5) {
                showErrorDialog("Rebate must be between 0% - 5%");
                return;
            }
        }

        double totalCharges = calculateTotalCharges(unit);
        double finalCost = totalCharges - (totalCharges * rebate / 100);

        // Show popup dialog with results
        showResultPopup(totalCharges, finalCost);
    }

    // Add this new method for popup results
    private void showResultPopup(double totalCharges, double finalCost) {
        // Create custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_result, null);

        TextView tvPopupTotal = dialogView.findViewById(R.id.tvPopupTotal);
        TextView tvPopupFinal = dialogView.findViewById(R.id.tvPopupFinal);
        Button btnClose = dialogView.findViewById(R.id.btnClose);

        tvPopupTotal.setText("RM " + df.format(totalCharges));
        tvPopupFinal.setText("RM " + df.format(finalCost));

        builder.setView(dialogView);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    // Add this new method for error dialog
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("⚠️ Oops!")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
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

    private void saveToDatabase() {
        String month = spinnerMonth.getSelectedItem().toString();
        // Check if hint is selected (not a real month)
        if (month.equals("Please select month") || spinnerMonth.getSelectedItemPosition() == 0) {
            showErrorDialog("Please select a month first!");
            return;
        }
        String unitStr = etUnit.getText().toString().trim();
        String rebateStr = etRebate.getText().toString().trim();

        // Check if unit is empty
        if (unitStr.isEmpty()) {
            showErrorDialog("Please calculate first!");
            return;
        }

        int unit = Integer.parseInt(unitStr);

        // Validate unit range
        if (unit < 1 || unit > 1000) {
            showErrorDialog("Unit must be between 1-1000 kWh");
            return;
        }

        double rebate = 0;
        if (!rebateStr.isEmpty()) {
            rebate = Double.parseDouble(rebateStr);
            if (rebate < 0 || rebate > 5) {
                showErrorDialog("Rebate must be between 0-5%");
                return;
            }
        }

        // Calculate charges
        double totalCharges = calculateTotalCharges(unit);
        double finalCost = totalCharges - (totalCharges * rebate / 100);

        // Save to database
        long result = dbHelper.insertBill(month, unit, totalCharges, rebate, finalCost);

        if (result != -1) {
            Toast.makeText(this, "✓ Data saved successfully!", Toast.LENGTH_SHORT).show();
            // Clear fields
            etUnit.setText("");
            etRebate.setText("");
        } else {
            Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show();
        }
    }
}