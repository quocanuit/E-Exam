package com.example.e_exam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExamCreateFragment extends Fragment {

    private static final String TAG = "ExamCreate";

    private EditText examNameInput;
    private Spinner classPicker;
    private TextView deadlinePicker, pdfNameTextView;
    private LinearLayout questionContainer;
    private Uri pdfUri;
    private Calendar selectedDeadline;
    private String selectedClass;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam_create, container, false);

        // Initialize Firebase
        FirebaseApp.initializeApp(requireContext());
        storageReference = FirebaseStorage.getInstance().getReference("exams");
        databaseReference = FirebaseDatabase.getInstance().getReference("exams");

        // UI element bindings
        examNameInput = view.findViewById(R.id.examNameInput);
        classPicker = view.findViewById(R.id.classPicker);
        deadlinePicker = view.findViewById(R.id.deadlinePicker);
        pdfNameTextView = view.findViewById(R.id.pdfNameTextView);
        questionContainer = view.findViewById(R.id.questionContainer);

        Button createExamButton = view.findViewById(R.id.createExamButton);
        Button uploadPdfButton = view.findViewById(R.id.uploadPdfButton);

        setupClassPicker();
        setupDeadlinePicker();

        uploadPdfButton.setOnClickListener(v -> pickPdfFile());
        createExamButton.setOnClickListener(v -> createExam());

        return view;
    }

    private void pickPdfFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        pdfPickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> pdfPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    pdfUri = result.getData().getData();
                    if (pdfUri != null) {
                        String fileName = getFileNameFromUri(pdfUri);
                        pdfNameTextView.setText("Selected PDF: " + fileName);
                        promptForQuestionCount();
                    }
                }
            }
    );

    @SuppressLint("Range")
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = Objects.requireNonNull(result).lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void promptForQuestionCount() {
        EditText input = new EditText(getContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Enter Number of Questions")
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {
                    int questionCount;
                    try {
                        questionCount = Integer.parseInt(input.getText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Invalid number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    displayQuestions(questionCount);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void displayQuestions(int count) {
        questionContainer.removeAllViews();
        for (int i = 1; i <= count; i++) {
            TextView questionLabel = new TextView(getContext());
            questionLabel.setText(i + ". What is the correct answer?");
            questionLabel.setTextSize(18);

            RadioGroup radioGroup = new RadioGroup(getContext());
            radioGroup.setOrientation(RadioGroup.VERTICAL);

            for (char option = 'A'; option <= 'D'; option++) {
                RadioButton radioButton = new RadioButton(getContext());
                radioButton.setText(String.valueOf(option));
                radioGroup.addView(radioButton);
            }

            questionContainer.addView(questionLabel);
            questionContainer.addView(radioGroup);
        }
    }

    private void createExam() {
        String examName = examNameInput.getText().toString().trim();
        if (examName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an exam name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pdfUri == null) {
            Toast.makeText(getContext(), "Please upload a PDF file", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDeadline == null) {
            Toast.makeText(getContext(), "Please set a deadline", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference fileRef = storageReference.child(examName + ".pdf");
        fileRef.putFile(pdfUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> saveExamToDatabase(uri.toString())))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "PDF Upload Failed", e);
                    Toast.makeText(getContext(), "Failed to upload PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void saveExamToDatabase(String pdfUrl) {
        String examName = examNameInput.getText().toString().trim();

        Map<String, String> answers = new HashMap<>();
        for (int i = 0; i < questionContainer.getChildCount(); i += 2) {
            RadioGroup radioGroup = (RadioGroup) questionContainer.getChildAt(i + 1);
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Please answer all questions", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton selectedRadioButton = radioGroup.findViewById(selectedId);
            answers.put("Question " + ((i / 2) + 1), selectedRadioButton.getText().toString());
        }

        Map<String, Object> examData = new HashMap<>();
        examData.put("name", examName);
        examData.put("class", selectedClass);
        examData.put("deadline", selectedDeadline.getTimeInMillis());
        examData.put("pdfUrl", pdfUrl);
        examData.put("answers", answers);

        String examId = databaseReference.push().getKey();
        if (examId == null) {
            Log.e(TAG, "Failed to generate exam ID");
            return;
        }

        databaseReference.child(examId).setValue(examData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Exam created successfully!", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Database write failed", e);
                    Toast.makeText(getContext(), "Failed to save exam: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void clearForm() {
        examNameInput.setText("");
        deadlinePicker.setText("Set Deadline");
        pdfNameTextView.setText("No PDF selected");
        questionContainer.removeAllViews();
        pdfUri = null;
        selectedDeadline = null;
    }

    private void setupClassPicker() {
        List<String> classList = new ArrayList<>();
        classList.add("NT531");
        classList.add("NT533");
        classList.add("NT131");
        classList.add("NT118");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, classList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classPicker.setAdapter(adapter);
        classPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClass = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
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

                    // Display the selected deadline in the TextView
                    @SuppressLint("DefaultLocale")
                    String deadlineText = String.format("%02d/%02d/%d %02d:%02d",
                            dayOfMonth, month + 1, year, hourOfDay, minute);
                    deadlinePicker.setText(deadlineText);
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();

            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show();
        });
    }
}