package com.example.e_exam;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExamStart extends AppCompatActivity {

    private TextView tvClassName;
    private TextView tvExamName;
    private TextView tvDeadline;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_exam);

        tvClassName = findViewById(R.id.tvClassName);
        tvExamName = findViewById(R.id.tvExamName);
        tvDeadline = findViewById(R.id.tvDeadline);

        // Nhận giá trị className và examName từ Intent
        String className = getIntent().getStringExtra("CLASS_NAME");
        String examName = getIntent().getStringExtra("EXAM_NAME");

        // Hiển thị className và examName trên TextViews
        if (className != null) {
            tvClassName.setText("Class Name: " + className);
        }
        if (examName != null) {
            tvExamName.setText("Exam Name: " + examName);
        }

        // Truy xuất deadline từ Firebase
        fetchDeadlineFromFirebase(className, examName);
    }

    private void fetchDeadlineFromFirebase(String className, String examName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("exams");

        databaseReference.orderByChild("class").equalTo(className).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    if (examName.equals(name)) {
                        Long deadlineMillis = snapshot.child("deadline").getValue(Long.class);

                        // Hiển thị giá trị deadline trong Toast để kiểm tra
                        Toast.makeText(ExamStart.this, "Deadline (Millis): " + deadlineMillis, Toast.LENGTH_LONG).show();

                        if (deadlineMillis != null) {
                            // Chuyển đổi deadline từ milliseconds sang Date
                            Date deadlineDate = new Date(deadlineMillis);

                            // Định dạng Date thành chuỗi ngày giờ
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                            String formattedDeadline = sdf.format(deadlineDate);

                            // Hiển thị chuỗi ngày giờ trên TextView
                            tvDeadline.setText(formattedDeadline);
                        } else {
                            tvDeadline.setText("No deadline set");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
            }
        });
    }
}
