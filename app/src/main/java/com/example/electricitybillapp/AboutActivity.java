package com.example.electricitybillapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Back button click listener
        MaterialButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ImageView ivStudentPicture = findViewById(R.id.ivStudentPicture);
        ivStudentPicture.setImageResource(R.drawable.student_photo);

        TextView tvStudentName = findViewById(R.id.tvStudentName);
        TextView tvStudentId = findViewById(R.id.tvStudentId);
        TextView tvCourse = findViewById(R.id.tvCourse);
        TextView tvCopyright = findViewById(R.id.tvCopyright);
        Button btnGithub = findViewById(R.id.btnGithub);
        Button btnHowToUse = findViewById(R.id.btnHowToUse);

        // UPDATE WITH YOUR INFORMATION
        tvStudentName.setText("Name: FARAH SABRENA BINTI HASNI");
        tvStudentId.setText("Student ID: 2024907767");
        tvCourse.setText("Course: ICT602 - Mobile Technology and Development");


        btnHowToUse.setOnClickListener(v -> {
            Intent intent = new Intent(AboutActivity.this, HowToUseActivity.class);
            startActivity(intent);
        });

        btnGithub.setOnClickListener(v -> {
            String url = "https://github.com/Farahhsni/ElectriSave";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
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
