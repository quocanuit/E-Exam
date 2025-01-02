package com.example.e_exam;

import android.os.Bundle;
import android.util.Log;
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

public class ListAssignment extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AssignmentAdapter assignmentAdapter;
    private List<Assignment> assignmentList;
    private TextView classNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_assignment);

        // Nhận tên lớp từ Intent
        String className = getIntent().getStringExtra("CLASS_NAME");

        // Hiển thị tên lớp
        classNameTextView = findViewById(R.id.class_name_text_view);
        classNameTextView.setText(className);

        // Thiết lập RecyclerView
        recyclerView = findViewById(R.id.recycler_view_assignments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        assignmentList = new ArrayList<>();
        assignmentAdapter = new AssignmentAdapter(assignmentList, className, this);
        recyclerView.setAdapter(assignmentAdapter);

        // Kết nối đến Firebase và đọc dữ liệu
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("exams");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                assignmentList.clear();

                // Duyệt qua các bài kiểm tra trong "exams"
                for (DataSnapshot examSnapshot : dataSnapshot.getChildren()) {
                    String examClass = examSnapshot.child("class").getValue(String.class);

                    // Kiểm tra nếu "class" trùng với tên lớp từ Intent
                    if (className.equals(examClass)) {
                        String examName = examSnapshot.child("name").getValue(String.class);
                        Assignment assignment = new Assignment(examName);
                        assignmentList.add(assignment);
                    }
                }

                // Cập nhật lại giao diện RecyclerView
                assignmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
                Toast.makeText(ListAssignment.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
                Log.e("ListAssignment", "DatabaseError: ", databaseError.toException());
            }
        });
    }
}
