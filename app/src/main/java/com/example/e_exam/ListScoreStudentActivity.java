package com.example.e_exam;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
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
  //  private StudentAdapter2 studentAdapter2;
    private List<Student2> studentList;  // Dùng List<Student2>
    private TextView classNameTextView;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_score_student);


        String className = getIntent().getStringExtra("CLASS_NAME");
        String examName = getIntent().getStringExtra("ASSIGNMENT_NAME");

        classNameTextView = findViewById(R.id.class_name_text_view);
        classNameTextView.setText(className);


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentList = new ArrayList<>();



        tableLayout = findViewById(R.id.table_layout);
        addMainTitle();

        addTableHeader();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("AverageScores").child(className);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentList.clear();
                tableLayout.removeAllViews();
                addTableHeader();


                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String studentId = studentSnapshot.getKey();
                    Log.d("StudentId", "Student ID: " + studentId); // In ID học sinh


                    for (DataSnapshot subjectSnapshot : studentSnapshot.getChildren()) {
                        String subjectName = subjectSnapshot.getKey();
                        if (examName.equals(subjectName) && subjectSnapshot.hasChild("ScoreAverage")) {
                            String subjectName2 = subjectSnapshot.getKey();
                            Double scoreAverage = subjectSnapshot.child("ScoreAverage").getValue(Double.class);  // Lấy điểm trung bình


                            double score = (scoreAverage != null) ? scoreAverage : 0.0;


                            Log.d("SubjectData", "Subject: " + subjectName + ", ScoreAverage: " + scoreAverage);


                            Student2 student = new Student2(studentId, subjectName2, score);
                            studentList.add(student);


                            addTableRow(studentId, subjectName2, score);


                            String toastMessage = "Student ID: " + studentId + "\nSubject: " + subjectName2 + "\nScore: " + score;
                            Toast.makeText(ListScoreStudentActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                }

            
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }
    private void addMainTitle() {
        TextView mainTitle = new TextView(this);
        mainTitle.setText("Danh sách điểm của sinh viên");
        mainTitle.setTextSize(24);
        mainTitle.setTextColor(getResources().getColor(android.R.color.black));
        mainTitle.setGravity(android.view.Gravity.CENTER);
        mainTitle.setTypeface(mainTitle.getTypeface(), android.graphics.Typeface.BOLD);
        mainTitle.setPadding(0, 16, 0, 16);

        tableLayout.addView(mainTitle);
    }

    private void addTableHeader() {
        TableRow headerRow = new TableRow(this);

        TextView studentIdHeader = new TextView(this);
        studentIdHeader.setText("Mã học sinh");
        studentIdHeader.setPadding(8, 8, 8, 8);
        studentIdHeader.setTextSize(16);
        studentIdHeader.setTextColor(getResources().getColor(android.R.color.white));
        studentIdHeader.setBackgroundColor(getResources().getColor(R.color.purple_700));
        headerRow.addView(studentIdHeader);

        TextView subjectHeader = new TextView(this);
        subjectHeader.setText("Môn học");
        subjectHeader.setPadding(8, 8, 8, 8);
        subjectHeader.setTextSize(16);
        subjectHeader.setTextColor(getResources().getColor(android.R.color.white));
        subjectHeader.setBackgroundColor(getResources().getColor(R.color.purple_700));
        headerRow.addView(subjectHeader);

        TextView scoreHeader = new TextView(this);
        scoreHeader.setText("Điểm");
        scoreHeader.setPadding(8, 8, 8, 8);
        scoreHeader.setTextSize(16);
        scoreHeader.setTextColor(getResources().getColor(android.R.color.white));
        scoreHeader.setBackgroundColor(getResources().getColor(R.color.purple_700));
        headerRow.addView(scoreHeader);

        tableLayout.addView(headerRow);
    }

    private void addTableRow(String studentId, String subjectName, double score) {
        TableRow row = new TableRow(this);

        TextView studentIdTextView = new TextView(this);
        studentIdTextView.setText(studentId);
        studentIdTextView.setPadding(8, 8, 8, 8);
        studentIdTextView.setTextSize(16);
        studentIdTextView.setTextColor(getResources().getColor(android.R.color.black));
        studentIdTextView.setBackgroundColor(getResources().getColor(android.R.color.white));
        row.addView(studentIdTextView);

        TextView subjectTextView = new TextView(this);
        subjectTextView.setText(subjectName);
        subjectTextView.setPadding(8, 8, 8, 8);
        subjectTextView.setTextSize(16);
        subjectTextView.setTextColor(getResources().getColor(android.R.color.black));
        subjectTextView.setBackgroundColor(getResources().getColor(android.R.color.white));
        row.addView(subjectTextView);

        TextView scoreTextView = new TextView(this);
        scoreTextView.setText(String.valueOf(score));
        scoreTextView.setPadding(8, 8, 8, 8);
        scoreTextView.setTextSize(16);
        scoreTextView.setTextColor(getResources().getColor(R.color.red));
        scoreTextView.setBackgroundColor(getResources().getColor(android.R.color.white));
        scoreTextView.setGravity(android.view.Gravity.END);
        row.addView(scoreTextView);

        tableLayout.addView(row);
    }
}
