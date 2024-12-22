package com.example.e_exam;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.TableLayout;
import android.widget.TableRow;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ListStudentActivity extends AppCompatActivity {

    private TextView NameClass;
    private TableLayout tableLayoutStudents;
    private ArrayList<String> studentsList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_student);

        // Nhận tên lớp từ Intent
        String className = getIntent().getStringExtra("CLASS_NAME");

        // Ánh xạ TextView và TableLayout
        NameClass = findViewById(R.id.tvClassName);
        tableLayoutStudents = findViewById(R.id.tableLayoutStudents);

        // Cập nhật tên lớp vào TextView
        if (className != null) {
            NameClass.setText("Lớp: " + className);
        }


        studentsList = new ArrayList<>();

        // Firebase reference để lấy danh sách sinh viên
        databaseReference = FirebaseDatabase.getInstance().getReference("Classes").child(className).child("students");

        // Truy cập Firebase để lấy danh sách sinh viên
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                studentsList.clear(); // Làm sạch danh sách sinh viên hiện tại

                // Lặp qua danh sách sinh viên và thêm vào TableLayout
                int index = 1; // Số thứ tự
                for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                    String studentName = studentSnapshot.child("studentName").getValue(String.class);
                    if (studentName != null) {
                        studentsList.add(studentName); // Thêm tên sinh viên vào danh sách

                        // Tạo một hàng cho mỗi sinh viên
                        TableRow tableRow = new TableRow(ListStudentActivity.this);

                        // Thêm số thứ tự
                        TextView numberTextView = new TextView(ListStudentActivity.this);
                        numberTextView.setText(String.valueOf(index));
                        numberTextView.setPadding(16, 8, 16, 8);
                        tableRow.addView(numberTextView);

                        // Thêm tên sinh viên
                        TextView nameTextView = new TextView(ListStudentActivity.this);
                        nameTextView.setText(studentName);
                        nameTextView.setPadding(16, 8, 16, 8);
                        tableRow.addView(nameTextView);

                        // Thêm hàng vào TableLayout
                        tableLayoutStudents.addView(tableRow);

                        index++; // Tăng số thứ tự
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ListStudentActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
