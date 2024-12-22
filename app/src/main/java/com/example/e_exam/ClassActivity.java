package com.example.e_exam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ClassActivity extends AppCompatActivity {

    private CardView cardListStudent;
    private String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        // Nhận tên lớp từ Intent
        className = getIntent().getStringExtra("CLASS_NAME");

        // Hiển thị tên lớp
        TextView classNameTextView = findViewById(R.id.class_name_text_view);
        classNameTextView.setText(className);

        // CardView cho danh sách sinh viên
        cardListStudent = findViewById(R.id.card_listStudent);
        cardListStudent.setOnClickListener(v -> openListStudentActivity());
    }

    private void openListStudentActivity() {
        Intent intent = new Intent(ClassActivity.this, ListStudentActivity.class);
        intent.putExtra("CLASS_NAME", className);
        startActivity(intent);
    }
}
