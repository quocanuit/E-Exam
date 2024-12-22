package com.example.e_exam.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_exam.R;
import com.example.e_exam.model.Question;

import java.text.BreakIterator;
import java.util.HashMap;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private final List<Question> questions;

    public QuestionAdapter(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questions.get(position);

        // Set question number
        holder.questionNumber.setText("Câu " + (position + 1));

        // Initialize answers map if empty
        if (question.getAnswers() == null) {
            question.setAnswers(new HashMap<>());
        }

        // Set up question text
        holder.questionText.setText(question.getQuestionText());
        holder.questionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                question.setQuestionText(s.toString());
            }
        });

        // Rest of the binding code remains the same...
        setupAnswerEditText(holder.answerA, "A", question);
        setupAnswerEditText(holder.answerB, "B", question);
        setupAnswerEditText(holder.answerC, "C", question);
        setupAnswerEditText(holder.answerD, "D", question);

        // Reset RadioGroup listener and selection
        holder.answersRadioGroup.setOnCheckedChangeListener(null);
        holder.answersRadioGroup.clearCheck();

        // Set the correct radio button based on saved answer
        if (question.getCorrectAnswer() != null) {
            switch (question.getCorrectAnswer()) {
                case "A":
                    holder.radioA.setChecked(true);
                    break;
                case "B":
                    holder.radioB.setChecked(true);
                    break;
                case "C":
                    holder.radioC.setChecked(true);
                    break;
                case "D":
                    holder.radioD.setChecked(true);
                    break;
            }
        }

        // Set up radio button listener
        holder.answersRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedAnswer = null;
            if (checkedId == R.id.radioA) {
                selectedAnswer = "A";
            } else if (checkedId == R.id.radioB) {
                selectedAnswer = "B";
            } else if (checkedId == R.id.radioC) {
                selectedAnswer = "C";
            } else if (checkedId == R.id.radioD) {
                selectedAnswer = "D";
            }
            question.setCorrectAnswer(selectedAnswer);
        });

        // Ensure radio buttons are mutually exclusive
        holder.radioA.setOnClickListener(v -> handleRadioButtonClick(holder, holder.radioA, "A", question));
        holder.radioB.setOnClickListener(v -> handleRadioButtonClick(holder, holder.radioB, "B", question));
        holder.radioC.setOnClickListener(v -> handleRadioButtonClick(holder, holder.radioC, "C", question));
        holder.radioD.setOnClickListener(v -> handleRadioButtonClick(holder, holder.radioD, "D", question));
    }

    private void handleRadioButtonClick(QuestionViewHolder holder, RadioButton clickedButton, String answer, Question question) {
        // Uncheck all other radio buttons
        holder.radioA.setChecked(false);
        holder.radioB.setChecked(false);
        holder.radioC.setChecked(false);
        holder.radioD.setChecked(false);

        // Check the clicked button and update the correct answer
        clickedButton.setChecked(true);
        question.setCorrectAnswer(answer);
    }

    private void setupAnswerEditText(EditText editText, String option, Question question) {
        // Set existing answer text if available
        String currentAnswer = question.getAnswers().get(option);
        if (currentAnswer != null && !currentAnswer.isEmpty()) {
            editText.setText(currentAnswer);
        }

        // Listen for answer text changes
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                question.getAnswers().put(option, s.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView questionNumber;
        EditText questionText;
        RadioGroup answersRadioGroup;
        RadioButton radioA, radioB, radioC, radioD;
        EditText answerA, answerB, answerC, answerD;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionNumber = itemView.findViewById(R.id.questionNumber);
            questionText = itemView.findViewById(R.id.questionText);
            answersRadioGroup = itemView.findViewById(R.id.answersRadioGroup);
            radioA = itemView.findViewById(R.id.radioA);
            radioB = itemView.findViewById(R.id.radioB);
            radioC = itemView.findViewById(R.id.radioC);
            radioD = itemView.findViewById(R.id.radioD);
            answerA = itemView.findViewById(R.id.answerA);
            answerB = itemView.findViewById(R.id.answerB);
            answerC = itemView.findViewById(R.id.answerC);
            answerD = itemView.findViewById(R.id.answerD);
        }
    }
}