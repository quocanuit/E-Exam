package com.example.e_exam;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ScoreStudent extends AppCompatActivity {

    private LinearLayout scoreContainer;
    private TextView tvAverageScore;
    private String studentId;
    private String className;
    private String examName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_student);

        scoreContainer = findViewById(R.id.scoreContainer);
        tvAverageScore = findViewById(R.id.tvAverageScore);

        // Nhận StudentId từ Intent
        studentId = getIntent().getStringExtra("StudentId");
        className = getIntent().getStringExtra("CLASS_NAME");
        examName = getIntent().getStringExtra("EXAM_NAME");

        fetchScoresFromFirebase(studentId);
    }

    private void fetchScoresFromFirebase(String studentId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Grade");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int resultCount = 1;
                int totalScore = 0;
                int numScores = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String idStudent = snapshot.child("IdStudent").getValue(String.class);
                    String ClassName = snapshot.child("className").getValue(String.class);
                    String ExamName = snapshot.child("examName").getValue(String.class);

                    if (studentId.equals(idStudent) && className.equals(ClassName) && examName.equals(ExamName)) {
                        long score = snapshot.child("score").getValue(Long.class);
                        String timestamp = snapshot.child("timestamp").getValue(String.class);
                        addScoreToView(resultCount, className, examName, score, timestamp);
                        resultCount++;
                        totalScore += score;
                        numScores++;
                    }
                }

                if (resultCount == 1) {
                    Toast.makeText(ScoreStudent.this, "Không có kết quả bài làm nào", Toast.LENGTH_SHORT).show();
                } else {
                    double averageScore = numScores > 0 ? (double) totalScore / numScores : 0;
                    tvAverageScore.setText("Điểm trung bình: " + averageScore);
                    saveAverageScoreToFirebase(className, examName, studentId, averageScore);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ScoreStudent.this, "Failed to load scores", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addScoreToView(int resultCount, String className, String examName, long score, String timestamp) {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 20, 0, 20);
        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(15);
        cardView.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        cardView.setContentPadding(36, 36, 36, 36);
        cardView.setCardElevation(8);

        LinearLayout cardLinearLayout = new LinearLayout(this);
        cardLinearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView resultTitle = new TextView(this);
        resultTitle.setText("Kết quả bài làm lần " + resultCount);
        resultTitle.setTextSize(18);
        resultTitle.setPadding(0, 0, 0, 10);

        TextView resultDetails = new TextView(this);
        resultDetails.setText("Lớp: " + className + "\nBài thi: " + examName + "\nĐiểm: " + score + "\nNgày làm bài: " + timestamp);
        resultDetails.setTextSize(16);

        cardLinearLayout.addView(resultTitle);
        cardLinearLayout.addView(resultDetails);
        cardView.addView(cardLinearLayout);

        scoreContainer.addView(cardView);
    }

    private String sanitizeFirebaseKey(String key) {
        return key.replace(".", ",")
                .replace("#", ",")
                .replace("$", ",")
                .replace("[", ",")
                .replace("]", ",");
    }

    private void saveAverageScoreToFirebase(String className, String examName, String studentId, double averageScore) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("AverageScores")
                .child(sanitizeFirebaseKey(className))
                .child(sanitizeFirebaseKey(studentId))
                .child(sanitizeFirebaseKey(examName));

        Map<String, Object> averageScoreData = new HashMap<>();
        averageScoreData.put("ScoreAverage", averageScore);

        databaseReference.setValue(averageScoreData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ScoreStudent.this, "Điểm trung bình đã được lưu thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ScoreStudent.this, "Lỗi khi lưu điểm trung bình", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
