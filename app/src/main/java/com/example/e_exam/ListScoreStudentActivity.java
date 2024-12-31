package com.example.e_exam;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class ListScoreStudentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentAdapter2 studentAdapter2;
    private List<Student2> studentList;  // Dùng List<Student2>
    private TextView classNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_score_student);

        // Nhận tên lớp từ Intent
        String className = getIntent().getStringExtra("CLASS_NAME");

        // Hiển thị tên lớp
        classNameTextView = findViewById(R.id.class_name_text_view);
        classNameTextView.setText(className);

        // Thiết lập RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentList = new ArrayList<>();
        studentAdapter2 = new StudentAdapter2(studentList);
        recyclerView.setAdapter(studentAdapter2);

        // Kết nối đến Firebase và đọc dữ liệu
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("AverageScores").child(className);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentList.clear();

                // Duyệt qua các học sinh trong lớp khớp với className
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String studentId = studentSnapshot.getKey();  // Lấy ID học sinh
                    Log.d("StudentId", "Student ID: " + studentId); // In ID học sinh

                    // Lấy điểm trung bình từ các môn học của học sinh
                    for (DataSnapshot subjectSnapshot : studentSnapshot.getChildren()) {
                        if (subjectSnapshot.hasChild("ScoreAverage")) {
                            String subjectName = subjectSnapshot.getKey();  // Lấy tên môn học
                            Double scoreAverage = subjectSnapshot.child("ScoreAverage").getValue(Double.class);  // Lấy điểm trung bình

                            // Kiểm tra nếu scoreAverage là null, gán mặc định là 0.0
                            double score = (scoreAverage != null) ? scoreAverage : 0.0;

                            // In ra log để kiểm tra dữ liệu
                            Log.d("SubjectData", "Subject: " + subjectName + ", ScoreAverage: " + scoreAverage);

                            // Tạo đối tượng Student2 với Mã học sinh, Môn học và Điểm trung bình
                            Student2 student = new Student2(studentId, subjectName, score);
                            studentList.add(student);

                            // Hiển thị thông tin dưới dạng Toast
                            String toastMessage = "Student ID: " + studentId + "\nSubject: " + subjectName + "\nScore: " + score;
                            Toast.makeText(ListScoreStudentActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                }

                // Cập nhật lại giao diện RecyclerView
                studentAdapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }
}
