package com.example.e_exam;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Thiết lập sự kiện bấm cho nút Teacher Test
        findViewById(R.id.teacherTestButton).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, TeacherActivity.class));
        });

        // Thiết lập sự kiện bấm cho nút Student Test
        findViewById(R.id.studentTestButton).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, StudentActivity.class));
        });
        findViewById(R.id.btn_register).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });
    }
}
