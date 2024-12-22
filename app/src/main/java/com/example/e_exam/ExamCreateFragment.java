package com.example.e_exam;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_exam.adapter.QuestionAdapter;
import com.example.e_exam.model.Question;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamCreateFragment extends Fragment {

    private static final String TAG = "ExamCreate";

    private EditText examNameInput, questionCountInput;
    private Spinner classPicker;
    private TextView deadlinePicker;
    private RecyclerView questionsRecyclerView;

    private Calendar selectedDeadline;
    private String selectedClass;

    private QuestionAdapter questionAdapter;
    private List<Question> questionsList = new ArrayList<>();

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam_create, container, false);

        // Initialize Firebase
        FirebaseApp.initializeApp(requireContext());
        databaseReference = FirebaseDatabase.getInstance().getReference("exams");

        // Bind UI elements
        examNameInput = view.findViewById(R.id.examNameInput);
        questionCountInput = view.findViewById(R.id.questionCountInput);
        classPicker = view.findViewById(R.id.classPicker);
        deadlinePicker = view.findViewById(R.id.deadlinePicker);
        questionsRecyclerView = view.findViewById(R.id.questionsRecyclerView);

        Button generateQuestionsButton = view.findViewById(R.id.generateQuestionsButton);
        Button createExamButton = view.findViewById(R.id.createExamButton);

        setupClassPicker();
        setupDeadlinePicker();

        // Setup RecyclerView
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        questionAdapter = new QuestionAdapter(questionsList);
        questionsRecyclerView.setAdapter(questionAdapter);

        generateQuestionsButton.setOnClickListener(v -> {
            String countText = questionCountInput.getText().toString().trim();
            if (countText.isEmpty()) {
                Toast.makeText(getContext(), "Please enter the number of questions", Toast.LENGTH_SHORT).show();
                return;
            }
            int questionCount = Integer.parseInt(countText);
            populateQuestions(questionCount);
        });

        createExamButton.setOnClickListener(v -> createExam());

        return view;
    }

    private void populateQuestions(int count) {
        questionsList.clear();
        for (int i = 0; i < count; i++) {
            questionsList.add(new Question("Question " + (i + 1), new HashMap<>(), ""));
        }
        questionAdapter.notifyDataSetChanged();
    }

    private void createExam() {
        String examName = examNameInput.getText().toString().trim();
        String questionCountText = questionCountInput.getText().toString().trim();

        // Basic validation checks
        if (examName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an exam name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (questionCountText.isEmpty()) {
            Toast.makeText(getContext(), "Please enter the number of questions", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDeadline == null) {
            Toast.makeText(getContext(), "Please set a deadline", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedClass == null || selectedClass.isEmpty()) {
            Toast.makeText(getContext(), "Please select a class", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate questions
        for (int i = 0; i < questionsList.size(); i++) {
            Question question = questionsList.get(i);

            // Validate question text
            if (question.getQuestionText() == null || question.getQuestionText().trim().isEmpty()) {
                Toast.makeText(getContext(), "Please enter text for question " + (i + 1), Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate all answers are filled
            Map<String, String> answers = question.getAnswers();
            if (answers == null || answers.size() < 4 ||
                    answers.get("A") == null || answers.get("A").trim().isEmpty() ||
                    answers.get("B") == null || answers.get("B").trim().isEmpty() ||
                    answers.get("C") == null || answers.get("C").trim().isEmpty() ||
                    answers.get("D") == null || answers.get("D").trim().isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all answers for question " + (i + 1), Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate correct answer is selected
            String correctAnswer = question.getCorrectAnswer();
            if (correctAnswer == null || correctAnswer.trim().isEmpty() ||
                    !Arrays.asList("A", "B", "C", "D").contains(correctAnswer)) {
                Toast.makeText(getContext(), "Please select correct answer for question " + (i + 1), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Prepare exam data
        Map<String, Object> examData = new HashMap<>();
        examData.put("name", examName);
        examData.put("class", selectedClass);
        examData.put("deadline", selectedDeadline.getTimeInMillis());

        // Convert questions to map
        Map<String, Object> questionsMap = new HashMap<>();
        for (int i = 0; i < questionsList.size(); i++) {
            Question question = questionsList.get(i);
            Map<String, Object> questionData = new HashMap<>();
            questionData.put("questionText", question.getQuestionText().trim());
            questionData.put("answers", question.getAnswers());
            questionData.put("correctAnswer", question.getCorrectAnswer());
            questionsMap.put("question" + (i + 1), questionData);
        }
        examData.put("questions", questionsMap);

        // Upload to Firebase
        String examId = databaseReference.push().getKey();
        if (examId == null) {
            Toast.makeText(getContext(), "Failed to create exam. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.child(examId).setValue(examData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Exam created successfully!", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save exam: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void clearForm() {
        examNameInput.setText("");
        questionCountInput.setText("");
        deadlinePicker.setText("Set Deadline");
        questionsList.clear();
        questionAdapter.notifyDataSetChanged();
        selectedDeadline = null;
    }

    private void setupClassPicker() {
        List<String> classList = List.of("NT531", "NT533", "NT131", "NT118");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, classList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classPicker.setAdapter(adapter);
        classPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClass = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupDeadlinePicker() {
        deadlinePicker.setOnClickListener(v -> {
            Calendar currentDate = Calendar.getInstance();
            selectedDeadline = Calendar.getInstance();

            new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                selectedDeadline.set(Calendar.YEAR, year);
                selectedDeadline.set(Calendar.MONTH, month);
                selectedDeadline.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                new TimePickerDialog(requireContext(), (timeView, hourOfDay, minute) -> {
                    selectedDeadline.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDeadline.set(Calendar.MINUTE, minute);

                    @SuppressLint("DefaultLocale")
                    String deadlineText = String.format("%02d/%02d/%d %02d:%02d",
                            dayOfMonth, month + 1, year, hourOfDay, minute);
                    deadlinePicker.setText(deadlineText);
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();

            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show();
        });
    }
}
