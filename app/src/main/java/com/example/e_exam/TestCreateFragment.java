package com.example.e_exam;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCreateFragment extends Fragment {
    private static final String TAG = "TestCreate";
    private static final int PICK_PDF_REQUEST = 1;
    private static final int PICK_EXCEL_REQUEST = 2;

    private View mView;
    private String teacherId;
    private EditText testNameInput, numberOfQuestionsInput, countDownTimerInput;
    private Spinner classPicker;
    private TextView deadlinePicker;
    private Button generateFileButton, createTestButton, uploadAnswerButton;
    private Uri pdfUri;
    private Uri excelUri;
    private Calendar selectedDeadline;
    private ClassInfo selectedClassInfo;
    private List<ClassInfo> classList;
    private DatabaseReference databaseReference;

    // Class to hold class information
    private static class ClassInfo {
        private final String id;
        private final String name;

        public ClassInfo(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @NonNull
        @Override
        public String toString() {
            return name;
        }
    }

    public static TestCreateFragment newInstance(String teacherId) {
        TestCreateFragment fragment = new TestCreateFragment();
        Bundle args = new Bundle();
        args.putString("teacherId", teacherId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_test_create, container, false);

        if (getArguments() != null) {
            teacherId = getArguments().getString("teacherId");
        }

        // Initialize Firebase
        initFirebase();

        // Initialize the classList
        classList = new ArrayList<>();

        // Bind UI elements
        initUI();

        // Set up
        setupClassPicker();
        setupDeadlinePicker();

        // Handle click event
        onClickListener();

        return mView;
    }

    private void initUI(){
        testNameInput = mView.findViewById(R.id.testNameInput);
        classPicker = mView.findViewById(R.id.classPicker);
        deadlinePicker = mView.findViewById(R.id.deadlinePicker);
        numberOfQuestionsInput = mView.findViewById(R.id.numberOfQuestionsInput);
        countDownTimerInput = mView.findViewById(R.id.countDownTimerInput);
        generateFileButton = mView.findViewById(R.id.generateFileButton);
        createTestButton = mView.findViewById(R.id.createTestButton);
        uploadAnswerButton = mView.findViewById(R.id.uploadAnswerButton);
    }

    private void onClickListener(){
        generateFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
        });

        uploadAnswerButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            startActivityForResult(Intent.createChooser(intent, "Select Excel File"), PICK_EXCEL_REQUEST);
        });

        createTestButton.setOnClickListener(v -> createTest());
    }

    private void initFirebase(){
        FirebaseApp.initializeApp(requireContext());
        databaseReference = FirebaseDatabase.getInstance().getReference("tests");
    }

    private void showProgressDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupClassPicker() {
        DatabaseReference classesRef = FirebaseDatabase.getInstance().getReference("Classes");

        classesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                classList.clear();
                for (DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
                    String teacherIdInClass = classSnapshot.child("teacherId").getValue(String.class);
                    if (teacherIdInClass != null && teacherIdInClass.equals(teacherId)) {
                        String className = classSnapshot.child("className").getValue(String.class);
                        String classId = classSnapshot.getKey();
                        if (className != null && classId != null) {
                            classList.add(new ClassInfo(classId, className));
                        }
                    }
                }

                ArrayAdapter<ClassInfo> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, classList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                classPicker.setAdapter(adapter);
                classPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedClassInfo = (ClassInfo) parent.getItemAtPosition(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedClassInfo = null;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load classes: " + databaseError.getMessage());
                Toast.makeText(requireContext(), "Failed to load classes. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDeadlinePicker() {
        deadlinePicker.setOnClickListener(v -> {
            Calendar currentDate = Calendar.getInstance();
            int year = currentDate.get(Calendar.YEAR);
            int month = currentDate.get(Calendar.MONTH);
            int day = currentDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        int hour = currentDate.get(Calendar.HOUR_OF_DAY);
                        int minute = currentDate.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                                (timeView, selectedHour, selectedMinute) -> {
                                    selectedDeadline = Calendar.getInstance();
                                    selectedDeadline.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
                                    @SuppressLint("DefaultLocale") String formattedDeadline = String.format("%02d/%02d/%04d %02d:%02d",
                                            selectedDay, selectedMonth + 1, selectedYear, selectedHour, selectedMinute);
                                    deadlinePicker.setText(formattedDeadline);
                                }, hour, minute, true);

                        timePickerDialog.show();
                    }, year, month, day);

            datePickerDialog.show();
        });
    }

    private void createTest() {
        String testName = testNameInput.getText().toString().trim();
        String numberOfQuestionsStr = numberOfQuestionsInput.getText().toString().trim();
        String countDownTimerStr = countDownTimerInput.getText().toString().trim();

        if (testName.isEmpty()) {
            showProgressDialog("Vui lòng nhập tên bài kiểm tra!");
            testNameInput.requestFocus();
            return;
        }
        if (selectedDeadline == null) {
            showProgressDialog("Vui lòng đặt thời gian kết thúc!");

            return;
        }
        if (selectedClassInfo == null) {
            showProgressDialog("Vui lòng chọn lớp học!");
            return;
        }
        if (pdfUri == null) {
            showProgressDialog("Vui lòng tải tài liệu đề cho bài kiểm tra!");
            return;
        }
        if (excelUri == null) {
            showProgressDialog("Vui lòng tải tài liệu đáp án cho bài kiểm tra!");

            return;
        }
        if (numberOfQuestionsStr.isEmpty()) {
            showProgressDialog("Vui lòng nhập số lượng câu hỏi!");
            numberOfQuestionsInput.requestFocus();
            return;
        }
        if (countDownTimerStr.isEmpty()) {
            showProgressDialog("Vui lòng nhập thời gian làm bài!");
            countDownTimerInput.requestFocus();
            return;
        }


        try {
            int numberOfQuestions = Integer.parseInt(numberOfQuestionsStr);
            int countDownTimer = Integer.parseInt(countDownTimerStr);
            uploadFilesToFirebase(testName, numberOfQuestions, countDownTimer);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter valid numbers for questions and time limit.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFilesToFirebase(String testName, int numberOfQuestions, int countDownTimer) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference pdfRef = storage.getReference().child("exams/" + System.currentTimeMillis() + ".pdf");
        StorageReference excelRef = storage.getReference().child("answers/" + System.currentTimeMillis() + ".xlsx");

        // Hiển thị ProgressDialog
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Đang tải tài liệu...");
        progressDialog.setMessage("Vui lòng đợi...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Upload PDF file
        pdfRef.putFile(pdfUri).addOnSuccessListener(pdfTaskSnapshot -> {
            pdfRef.getDownloadUrl().addOnSuccessListener(pdfUrl -> {
                // Upload Excel file
                excelRef.putFile(excelUri).addOnSuccessListener(excelTaskSnapshot -> {
                    excelRef.getDownloadUrl().addOnSuccessListener(excelUrl -> {
                        // Cả hai file đã được tải lên thành công
                        fetchStudentsAndSaveTest(testName, pdfUrl.toString(), excelUrl.toString(), numberOfQuestions, countDownTimer);
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Tải đề bài lên thành công!", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Excel URL retrieval failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Tải tài liệu (.xlsx) đáp án thành công! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "PDF URL retrieval failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "Tải đề bài (.pdf) thất bại " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchStudentsAndSaveTest(String testName, String pdfUrl, String answerUri, int numberOfQuestions, int countDownTimer) {
        DatabaseReference studentsRef = FirebaseDatabase.getInstance()
                .getReference("Classes")
                .child(selectedClassInfo.getId())
                .child("students");

        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> studentIds = new ArrayList<>();
                for (DataSnapshot student : dataSnapshot.getChildren()) {
                    studentIds.add(student.getKey());
                }
                saveTestToFireStoreDatabase(testName, pdfUrl, studentIds, answerUri, numberOfQuestions, countDownTimer);
                saveTestToRealtimeDatabase(testName, pdfUrl, studentIds, answerUri, numberOfQuestions, countDownTimer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch students: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTestToFireStoreDatabase(String testName, String pdfUrl, List<String> studentIds, String answerUri, int numberOfQuestions, int timeLimit) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String examId = selectedClassInfo.getName() + "-" + testName;
        examId = examId.replace(" ", "_");

        Map<String, Object> examData = new HashMap<>();
        examData.put("name", testName);
        examData.put("className", selectedClassInfo.getName());
        examData.put("classId", selectedClassInfo.getId());
        examData.put("teacherId", teacherId);
        examData.put("deadline", selectedDeadline.getTimeInMillis());
        examData.put("createdAt", System.currentTimeMillis());
        examData.put("status", "pending");
        examData.put("pdfUrl", pdfUrl);
        examData.put("answerUrl", answerUri);
        examData.put("studentIds", studentIds);
        examData.put("numberOfQuestions", numberOfQuestions); // Số lượng câu hỏi
        examData.put("timeLimit", timeLimit); // Thời gian làm bài (phút)

        db.collection("exams")
                .document(examId)
                .set(examData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(),
                        "Test saved successfully!",
                        Toast.LENGTH_SHORT).show();
                    clearForm();
                    getParentFragmentManager().popBackStack();
                            })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                        "Failed to save test: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                });
    }

    private void saveTestToRealtimeDatabase(String testName, String pdfUrl, List<String> studentIds, String answerUri, int numberOfQuestions, int timeLimit) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("exams");

        String examId = selectedClassInfo.getName() + "-" + testName;
        examId = examId.replace(" ", "_");

        Map<String, Object> examData = new HashMap<>();
        examData.put("id", examId);
        examData.put("name", testName);
        examData.put("className", selectedClassInfo.getName());
        examData.put("classId", selectedClassInfo.getId());
        examData.put("teacherId", teacherId);
        examData.put("deadline", selectedDeadline.getTimeInMillis());
        examData.put("createdAt", System.currentTimeMillis());
        examData.put("status", "pending");
        examData.put("pdfUrl", pdfUrl);
        examData.put("answerUrl", answerUri);
        examData.put("studentIds", studentIds);
        examData.put("numberOfQuestions", numberOfQuestions);
        examData.put("timeLimit", timeLimit);

        databaseRef.child(examId).setValue(examData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(),
                            "Test saved successfully!",
                            Toast.LENGTH_SHORT).show();
                    clearForm();
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Failed to save test: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void clearForm() {
        testNameInput.setText("");
        deadlinePicker.setText("Set deadline");
        selectedDeadline = null;
        classPicker.setSelection(0);
        pdfUri = null;
        excelUri = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_PDF_REQUEST) {
                Uri selectedUri = data.getData();
                String fileType = requireContext().getContentResolver().getType(selectedUri);

                if (fileType != null && fileType.equals("application/pdf")) {
                    pdfUri = selectedUri;
                    Toast.makeText(getContext(), "Selected PDF: " + getFileName(pdfUri), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Please select a valid PDF file.", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == PICK_EXCEL_REQUEST) {
                Uri selectedUri = data.getData();
                String fileType = requireContext().getContentResolver().getType(selectedUri);

                if (fileType != null && fileType.contains("spreadsheet")) {
                    excelUri = selectedUri;
                    Toast.makeText(getContext(), "Selected Excel: " + getFileName(excelUri), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Please select a valid Excel file.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}