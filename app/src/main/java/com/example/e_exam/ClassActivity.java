package com.example.e_exam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        // Nhận tên lớp từ Intent
        Intent intent = getIntent();
        String className = intent.getStringExtra("CLASS_NAME");

        // Hiển thị tên lớp
        TextView classNameTextView = findViewById(R.id.class_name_text_view);
        classNameTextView.setText(className);
    }
}

