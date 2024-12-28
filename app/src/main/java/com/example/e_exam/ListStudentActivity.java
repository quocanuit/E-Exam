package com.example.e_exam;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListStudentActivity extends AppCompatActivity {

    private TextView NameClass;
    private TextView teacherNameView;
    private TextView teacherIdView;
    private TableLayout tableLayoutStudents;
    private ArrayList<String> studentsList;
    private DatabaseReference databaseReference;
    private String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_student);

        // Nhận tên lớp từ Intent
        className = getIntent().getStringExtra("CLASS_NAME");

        // Ánh xạ các view
        NameClass = findViewById(R.id.tvClassName);
        teacherNameView = findViewById(R.id.tvTeacherName);
        tableLayoutStudents = findViewById(R.id.tableLayoutStudents);

        // Cập nhật tên lớp vào TextView
        if (className != null) {
            NameClass.setText("Lớp: " + className);

            // Initialize database reference
            databaseReference = FirebaseDatabase.getInstance().getReference("Classes").child(className);

            // Load class info and students
            loadClassInfo();
            loadStudents();
        }

        studentsList = new ArrayList<>();

        FloatingActionButton fabAddStudent = findViewById(R.id.fabAddStudent);
        fabAddStudent.setOnClickListener(v -> showStudentSelectionDialog());
    }

    private void showStudentSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_student_selection, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerViewStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<UserModel> studentList = new ArrayList<>();
        StudentSelectionAdapter adapter = new StudentSelectionAdapter(studentList);

        // Create the dialog first
        AlertDialog dialog = builder.setView(dialogView)
                .setNegativeButton("Hủy", null)
                .create();

        // Then use it in the listener
        adapter.setOnStudentSelectedListener(student -> {
            addStudentToClass(student);
            dialog.dismiss();
        });

        recyclerView.setAdapter(adapter);

        // Load students from Firebase
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("role").equalTo("Student")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        studentList.clear();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            UserModel user = userSnapshot.getValue(UserModel.class);
                            if (user != null) {
                                studentList.add(user);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ListStudentActivity.this,
                                "Lỗi khi tải danh sách học sinh: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

        dialog.show();
    }

    private void addStudentToClass(UserModel student) {
        DatabaseReference studentRef = databaseReference.child("students").push();
        Map<String, Object> studentData = new HashMap<>();
        studentData.put("studentName", student.getFullName());
        studentData.put("studentId", student.getUid());
        studentData.put("email", student.getEmail());

        studentRef.setValue(studentData)
                .addOnSuccessListener(aVoid -> Toast.makeText(this,
                        "Đã thêm học sinh thành công", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Lỗi khi thêm học sinh: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadClassInfo() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Class classInfo = snapshot.getValue(Class.class);
                if (classInfo != null) {
                    teacherNameView.setText("Giáo viên: " +
                            (classInfo.getTeacherName() != null ? classInfo.getTeacherName() : "Chưa có"));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ListStudentActivity.this,
                        "Lỗi khi tải thông tin lớp: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStudents() {
        DatabaseReference studentsRef = databaseReference.child("students");
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Xóa tất cả các hàng cũ trừ hàng tiêu đề
                int childCount = tableLayoutStudents.getChildCount();
                if (childCount > 1) {
                    tableLayoutStudents.removeViews(1, childCount - 1);
                }

                studentsList.clear();

                // Lặp qua danh sách sinh viên và thêm vào TableLayout
                int index = 1;
                for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                    String studentKey = studentSnapshot.getKey();
                    String studentName = studentSnapshot.child("studentName").getValue(String.class);

                    if (studentName != null) {
                        studentsList.add(studentName);

                        // Tạo một hàng mới cho mỗi sinh viên
                        TableRow tableRow = createStudentRow(index, studentName, studentKey);
                        tableLayoutStudents.addView(tableRow);
                        index++;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ListStudentActivity.this,
                        "Lỗi: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private TableRow createStudentRow(int index, String studentName, String studentKey) {
        TableRow tableRow = new TableRow(this);
        tableRow.setPadding(0, 8, 0, 8);

        // Thêm số thứ tự
        TextView numberTextView = new TextView(this);
        numberTextView.setText(String.valueOf(index));
        numberTextView.setPadding(16, 8, 16, 8);
        numberTextView.setTextColor(getResources().getColor(android.R.color.black));
        tableRow.addView(numberTextView);

        // Thêm tên sinh viên
        TextView nameTextView = new TextView(this);
        nameTextView.setText(studentName);
        nameTextView.setPadding(16, 8, 16, 8);
        nameTextView.setTextColor(getResources().getColor(android.R.color.black));
        tableRow.addView(nameTextView);

        // Thêm effect khi nhấn giữ
        tableRow.setBackgroundResource(android.R.drawable.list_selector_background);

        // Xử lý sự kiện khi nhấn giữ
        tableRow.setOnLongClickListener(view -> {
            showDeleteConfirmationDialog(studentKey, studentName);
            return true;
        });

        return tableRow;
    }

    private void showDeleteConfirmationDialog(String studentKey, String studentName) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa học sinh " + studentName + " khỏi lớp?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    removeStudent(studentKey);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void removeStudent(String studentKey) {
        DatabaseReference studentRef = databaseReference.child("students").child(studentKey);
        studentRef.removeValue()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Đã xóa học sinh thành công", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi khi xóa học sinh: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}