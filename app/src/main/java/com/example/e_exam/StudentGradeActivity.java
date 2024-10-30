package com.example.e_exam;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentGradeActivity extends AppCompatActivity {

    private Map<String, List<String>> testDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_grade);

        TextView tvSubjectName = findViewById(R.id.tv_subject_name);
        LinearLayout llTestDetailsContainer = findViewById(R.id.ll_test_details_container);

        initTestDatabase();

        String subjectName = getIntent().getStringExtra("subjectName");
        tvSubjectName.setText(subjectName);

        List<String> testDetailsList = getTestDetails(subjectName);
        displayTestDetails(testDetailsList, llTestDetailsContainer);
    }

    private void initTestDatabase() {
        testDatabase = new HashMap<>();

        List<String> subjectATests = new ArrayList<>();
        subjectATests.add("Tên bài kiểm tra: Bài kiểm tra 1\nNgày làm: 01/01/2024\nThời gian: 60 phút\nĐiểm: 0");
        subjectATests.add("Tên bài kiểm tra: Bài kiểm tra 2\nNgày làm: 04/01/2024\nThời gian: 60 phút\nĐiểm: 9.5");
        testDatabase.put("Điểm Môn A", subjectATests);

        List<String> subjectBTests = new ArrayList<>();
        subjectBTests.add("Tên bài kiểm tra: Bài kiểm tra 1\nNgày làm: 02/01/2024\nThời gian: 60 phút\nĐiểm: 0");
        testDatabase.put("Điểm Môn B", subjectBTests);

        List<String> subjectCTests = new ArrayList<>();
        subjectCTests.add("Tên bài kiểm tra: Bài kiểm tra 1\nNgày làm: 03/01/2024\nThời gian: 60 phút\nĐiểm: 0");
        testDatabase.put("Điểm Môn C", subjectCTests);
    }

    private List<String> getTestDetails(String subjectName) {
        return testDatabase.getOrDefault(subjectName, new ArrayList<>());
    }

    private void displayTestDetails(List<String> testDetailsList, LinearLayout container) {
        for (String testDetails : testDetailsList) {
            TextView textView = new TextView(this);
            textView.setText(testDetails);
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.white)); // Chữ màu trắng
            textView.setBackgroundResource(R.drawable.rounded_background); // Áp dụng nền bo tròn
            textView.setPadding(16, 16, 16, 16);
            textView.setTextSize(16);

            // Thiết lập khoảng cách giữa các khung
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 16, 0, 0);
            textView.setLayoutParams(params);

            container.addView(textView);
        }
    }
}
