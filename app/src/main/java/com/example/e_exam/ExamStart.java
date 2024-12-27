package com.example.e_exam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private Button btnStartExam;
    private String className;
    private String examName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_exam);

        tvClassName = findViewById(R.id.tvClassName);
        tvExamName = findViewById(R.id.tvExamName);
        tvDeadline = findViewById(R.id.tvDeadline);
        btnStartExam = findViewById(R.id.btn_start_exam);

        // Nhận giá trị className và examName từ Intent
        className = getIntent().getStringExtra("CLASS_NAME");
        examName = getIntent().getStringExtra("EXAM_NAME");

        // Hiển thị className và examName trên TextViews
        if (className != null) {
            tvClassName.setText("Class Name: " + className);
        }
        if (examName != null) {
            tvExamName.setText("Exam Name: " + examName);
        }

        // Truy xuất deadline từ Firebase
        fetchDeadlineFromFirebase(className, examName);

        // Xử lý sự kiện click vào nút btnStartExam
        btnStartExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnStartExam.isEnabled()) {
                    // Chuyển sang ExamAction nếu vẫn còn hạn
                    Intent intent = new Intent(ExamStart.this, ExamAction.class);
                    intent.putExtra("CLASS_NAME", className);
                    intent.putExtra("EXAM_NAME", examName);
                    startActivity(intent);
                }
            }
        });
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
                            tvDeadline.setText("Ngày hết hạn: " + formattedDeadline);

                            // So sánh thời gian hiện tại với deadline
                            Date currentDate = new Date();
                            if (currentDate.after(deadlineDate)) {
                                // Đã quá hạn làm bài
                                tvDeadline.setText("Đã hết hạn làm bài");
                                btnStartExam.setEnabled(false); // Vô hiệu hóa nút btn_start_exam
                                btnStartExam.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(ExamStart.this, "Đã hết thời gian làm bài", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                // Còn trong thời gian làm bài
                                btnStartExam.setEnabled(true); // Kích hoạt nút btn_start_exam
                            }
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
