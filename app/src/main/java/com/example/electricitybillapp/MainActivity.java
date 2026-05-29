package com.example.electricitybillapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.text.DecimalFormat;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class MainActivity extends AppCompatActivity {

    private DataHelper dbHelper;
    private Spinner spinnerMonth;
    private EditText etUnit, etRebate;
    private MaterialButton btnCalculate, btnViewList, btnAbout;  // REMOVED btnSave
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
        if (selectedMonth.equals("📅 Please select month") || spinnerMonth.getSelectedItemPosition() == 0) {
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

    private void showResultPopup(double totalCharges, double finalCost) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_result, null);

        TextView tvPopupTotal = dialogView.findViewById(R.id.tvPopupTotal);
        TextView tvPopupFinal = dialogView.findViewById(R.id.tvPopupFinal);
        MaterialButton btnSaveFromDialog = dialogView.findViewById(R.id.btnSaveFromDialog);
        MaterialButton btnCloseDialog = dialogView.findViewById(R.id.btnCloseDialog);

        final double finalTotal = totalCharges;
        final double finalFinalCost = finalCost;

        tvPopupTotal.setText("RM " + df.format(totalCharges));
        tvPopupFinal.setText("RM " + df.format(finalCost));

        builder.setView(dialogView);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();

        btnSaveFromDialog.setOnClickListener(v -> {
            String month = spinnerMonth.getSelectedItem().toString();
            if (month.equals("📅 Please select month") || spinnerMonth.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Please select a month first!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            String unitStr = etUnit.getText().toString().trim();
            String rebateStr = etRebate.getText().toString().trim();

            if (unitStr.isEmpty()) {
                Toast.makeText(this, "Please calculate first!", Toast.LENGTH_SHORT).show();
                return;
            }

            int unit = Integer.parseInt(unitStr);
            double rebate = rebateStr.isEmpty() ? 0 : Double.parseDouble(rebateStr);

            long result = dbHelper.insertBill(month, unit, finalTotal, rebate, finalFinalCost);

            if (result != -1) {
                Toast.makeText(this, "✓ Data saved successfully!", Toast.LENGTH_SHORT).show();
                etUnit.setText("");
                etRebate.setText("");
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show();
            }
        });

        btnCloseDialog.setOnClickListener(v -> dialog.dismiss());
    }

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
}
