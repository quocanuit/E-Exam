package com.example.e_exam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;

import java.util.Locale;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExamAction extends AppCompatActivity {

    private LinearLayout questionContainer;
    private Map<String, String> correctAnswers = new HashMap<>(); // Lưu câu trả lời đúng
    private Map<String, RadioGroup> userAnswers = new HashMap<>(); // Lưu câu trả lời của người dùng
    private int questionNumber = 1; // Biến đếm số thứ tự câu hỏi

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_action);

        questionContainer = findViewById(R.id.questionContainer);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        // Nhận giá trị className và examName từ Intent
        String className = getIntent().getStringExtra("CLASS_NAME");
        String examName = getIntent().getStringExtra("EXAM_NAME");
        String StudentId = getIntent().getStringExtra("StudentId");
        // Truy xuất câu hỏi từ Firebase
        fetchQuestionsFromFirebase(className, examName);

        // Xử lý sự kiện click vào nút "Nộp bài"
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswersAndSaveScore(className, examName, StudentId);
            }
        });
    }

    private void fetchQuestionsFromFirebase(String className, String examName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("exams");

        databaseReference.orderByChild("class").equalTo(className).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    if (examName.equals(name)) {
                        DataSnapshot questionsSnapshot = snapshot.child("questions");
                        for (DataSnapshot questionSnapshot : questionsSnapshot.getChildren()) {
                            addQuestionToView(questionSnapshot);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ExamAction.this, "Failed to load questions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addQuestionToView(DataSnapshot questionSnapshot) {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 20, 0, 20);
        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(15);
        cardView.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        cardView.setContentPadding(36, 36, 36, 36);
        cardView.setCardElevation(8);

        LinearLayout cardLinearLayout = new LinearLayout(this);
        cardLinearLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = LayoutInflater.from(this);

        // Tạo một TextView cho số thứ tự câu hỏi
        TextView questionNumberText = new TextView(this);
        questionNumberText.setText("Câu hỏi " + questionNumber);
        questionNumberText.setTextSize(18); // Bạn có thể điều chỉnh kích thước chữ tại đây
        questionNumberText.setPadding(0, 20, 0, 10); // Thêm khoảng cách giữa các câu hỏi, bạn có thể điều chỉnh giá trị padding

        View questionView = inflater.inflate(R.layout.question_item, questionContainer, false);

        TextView questionText = questionView.findViewById(R.id.questionText);
        RadioGroup answersGroup = questionView.findViewById(R.id.answersGroup);
        RadioButton answerA = questionView.findViewById(R.id.answerA);
        RadioButton answerB = questionView.findViewById(R.id.answerB);
        RadioButton answerC = questionView.findViewById(R.id.answerC);
        RadioButton answerD = questionView.findViewById(R.id.answerD);

        String questionTextValue = questionSnapshot.child("questionText").getValue(String.class);
        questionText.setText(questionTextValue);

        for (DataSnapshot answerSnapshot : questionSnapshot.child("answers").getChildren()) {
            String answerKey = answerSnapshot.getKey();
            String answerValue = answerSnapshot.getValue(String.class);

            if ("A".equals(answerKey)) {
                answerA.setText(answerValue);
            } else if ("B".equals(answerKey)) {
                answerB.setText(answerValue);
            } else if ("C".equals(answerKey)) {
                answerC.setText(answerValue);
            } else if ("D".equals(answerKey)) {
                answerD.setText(answerValue);
            }
        }

        // Lưu đáp án đúng vào HashMap
        String correctAnswer = questionSnapshot.child("correctAnswer").getValue(String.class);
        correctAnswers.put(questionSnapshot.getKey(), correctAnswer);

        // Lưu RadioGroup vào HashMap với key là mã câu hỏi
        userAnswers.put(questionSnapshot.getKey(), answersGroup);

        // Thêm TextView số thứ tự câu hỏi vào container
        questionContainer.addView(questionNumberText);
        // Thêm câu hỏi vào container
        questionContainer.addView(questionView);

        // Tăng biến đếm số thứ tự câu hỏi
        questionNumber++;

    }

    private void checkAnswersAndSaveScore(String className, String examName, String StudentId) {
        int correctCount = 0;
        int totalCount = correctAnswers.size();

        for (Map.Entry<String, RadioGroup> entry : userAnswers.entrySet()) {
            String questionKey = entry.getKey();
            RadioGroup answersGroup = entry.getValue();

            int selectedId = answersGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedId);
                String userAnswer = getAnswerKeyFromRadioButtonId(selectedId);

                String correctAnswer = correctAnswers.get(questionKey);

                // So sánh đáp án của người dùng với đáp án đúng
                if (userAnswer.equals(correctAnswer)) {
                    correctCount++;
                }
            }
        }

        // Tính điểm và làm tròn
        double score = ((double) correctCount / totalCount) * 10;
        int roundedScore = (int) Math.round(score);

        // Lưu điểm lên Firebase
        saveScoreToFirebase(className, examName, roundedScore, StudentId);

        // Hiển thị kết quả
        Toast.makeText(this, "Số câu đúng: " + correctCount + "/" + totalCount + "\nĐiểm số: " + roundedScore, Toast.LENGTH_LONG).show();
        // Chuyển đến activity ScoreStudent
        Intent intent = new Intent(ExamAction.this, ScoreStudent.class);
        intent.putExtra("StudentId", StudentId);
        intent.putExtra("CLASS_NAME", className);
        intent.putExtra("EXAM_NAME", examName);
        startActivity(intent);
    }

    private void saveScoreToFirebase(String className, String examName, int score, String StudentId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Grade");
        String gradeId = databaseReference.push().getKey();

        // Lấy thời gian hiện tại
        String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        Map<String, Object> gradeData = new HashMap<>();
        gradeData.put("className", className);
        gradeData.put("examName", examName);
        gradeData.put("IdStudent", StudentId);
        gradeData.put("score", score);
        gradeData.put("timestamp", currentTime); // Thêm thông tin thời gian hiện tại

        if (gradeId != null) {
            databaseReference.child(gradeId).setValue(gradeData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ExamAction.this, "Điểm đã được lưu thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ExamAction.this, "Lỗi khi lưu điểm", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private String getAnswerKeyFromRadioButtonId(int radioButtonId) {
        if (radioButtonId == R.id.answerA) {
            return "A";
        } else if (radioButtonId == R.id.answerB) {
            return "B";
        } else if (radioButtonId == R.id.answerC) {
            return "C";
        } else if (radioButtonId == R.id.answerD) {
            return "D";
        } else {
            return "";
        }
    }
}
