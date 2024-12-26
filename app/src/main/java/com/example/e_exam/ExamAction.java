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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        // Truy xuất câu hỏi từ Firebase
        fetchQuestionsFromFirebase(className, examName);

        // Xử lý sự kiện click vào nút "Nộp bài"
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswers();
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

    private void checkAnswers() {
        int correctCount = 0;

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

        // Hiển thị kết quả
        Toast.makeText(this, "Số câu đúng: " + correctCount, Toast.LENGTH_SHORT).show();
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
