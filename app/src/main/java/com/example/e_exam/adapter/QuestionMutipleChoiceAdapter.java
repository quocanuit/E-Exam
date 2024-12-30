package com.example.e_exam.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_exam.R;
import com.example.e_exam.model.Question;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionMutipleChoiceAdapter extends RecyclerView.Adapter<QuestionMutipleChoiceAdapter.QuestionViewHolder> {

    private List<Question> questions;
    private Map<Integer, String> userAnswers = new HashMap<>(); // Lưu câu trả lời của người dùng

    private OnAnswerSelectedListener answerSelectedListener;

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(int position, String selectedAnswer);
    }

    public QuestionMutipleChoiceAdapter(List<Question> questions, OnAnswerSelectedListener listener) {
        this.questions = questions;
        this.answerSelectedListener = listener;
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_mutiple_choice, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.bind(question, position);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {

        private TextView questionText;
        private RadioGroup radioGroup;
        private RadioButton optionA, optionB, optionC, optionD;

        public QuestionViewHolder(View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.question_text);
            radioGroup = itemView.findViewById(R.id.answerGroup);
            optionA = itemView.findViewById(R.id.answerA);
            optionB = itemView.findViewById(R.id.answerB);
            optionC = itemView.findViewById(R.id.answerC);
            optionD = itemView.findViewById(R.id.answerD);
        }

        public void bind(Question question, int position) {
            questionText.setText(question.getQuestionText());

            // Sử dụng Map để hiển thị đáp án
            optionA.setText("A");
            optionB.setText("B");
            optionC.setText("C");
            optionD.setText("D");

            radioGroup.clearCheck();
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                String selectedAnswer = null;
                if (checkedId == R.id.answerA) {
                    selectedAnswer = "A";
                } else if (checkedId == R.id.answerB) {
                    selectedAnswer = "B";
                } else if (checkedId == R.id.answerC) {
                    selectedAnswer = "C";
                } else if (checkedId == R.id.answerD) {
                    selectedAnswer = "D";
                }

                // Lưu câu trả lời người dùng đã chọn vào map
                if (selectedAnswer != null) {
                    userAnswers.put(position, selectedAnswer);
                }

                if (answerSelectedListener != null && selectedAnswer != null) {
                    answerSelectedListener.onAnswerSelected(position, selectedAnswer);
                }
            });
        }
    }

    // Phương thức để lấy câu trả lời của người dùng
    public Map<Integer, String> getUserAnswers() {
        return userAnswers;
    }
}