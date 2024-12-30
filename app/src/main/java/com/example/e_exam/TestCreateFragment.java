package com.example.e_exam;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
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
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCreateFragment extends Fragment {
    private static final String TAG = "TestCreate";
    private static final int PICK_PDF_REQUEST = 1;
    private static final int PICK_EXCEL_REQUEST = 2;

    private String teacherId;
    private EditText testNameInput;
    private Spinner classPicker;
    private TextView deadlinePicker;
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
        View view = inflater.inflate(R.layout.fragment_test_create, container, false);
        if (getArguments() != null) {
            teacherId = getArguments().getString("teacherId");
        }

        // Initialize Firebase
        FirebaseApp.initializeApp(requireContext());
        databaseReference = FirebaseDatabase.getInstance().getReference("tests");

        // Initialize the classList
        classList = new ArrayList<>();

        // Bind UI elements
        testNameInput = view.findViewById(R.id.testNameInput);
        classPicker = view.findViewById(R.id.classPicker);
        deadlinePicker = view.findViewById(R.id.deadlinePicker);

        Button generateFileButton = view.findViewById(R.id.generateFileButton);
        Button createTestButton = view.findViewById(R.id.createTestButton);
        Button uploadAnswerButton = view.findViewById(R.id.uploadAnswerButton);

        setupClassPicker();
        setupDeadlinePicker();

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

        return view;
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

        if (testName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter test name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDeadline == null) {
            Toast.makeText(getContext(), "Please set deadline", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedClassInfo == null) {
            Toast.makeText(getContext(), "Please select class", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pdfUri == null) {
            Toast.makeText(getContext(), "Please select PDF file", Toast.LENGTH_SHORT).show();
            return;
        }
        if (excelUri == null) {
            Toast.makeText(getContext(), "Please select answer file", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadFilesToFirebase(testName);
    }

    private void uploadFilesToFirebase(String testName) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference pdfRef = storage.getReference().child("exams/" + System.currentTimeMillis() + ".pdf");
        StorageReference excelRef = storage.getReference().child("answers/" + System.currentTimeMillis() + ".xlsx");

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading files...");
        progressDialog.show();

        // Upload PDF file
        UploadTask pdfUploadTask = pdfRef.putFile(pdfUri);
        pdfUploadTask.addOnSuccessListener(taskSnapshot -> {
            pdfRef.getDownloadUrl().addOnSuccessListener(pdfUrl -> {
                // Upload Excel file
                UploadTask excelUploadTask = excelRef.putFile(excelUri);
                excelUploadTask.addOnSuccessListener(excelTaskSnapshot -> {
                    excelRef.getDownloadUrl().addOnSuccessListener(excelUrl -> {
                        fetchStudentsAndSaveTest(testName, pdfUrl.toString(), excelUrl.toString());
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Files uploaded successfully!", Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Excel upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "PDF upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchStudentsAndSaveTest(String testName, String pdfUrl, String answerUri) {
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
                saveTestToDatabase(testName, pdfUrl, studentIds, answerUri);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch students: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTestToDatabase(String testName, String pdfUrl, List<String> studentIds, String answerUri) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        db.collection("exams")
                .add(examData)
                .addOnSuccessListener(documentReference -> {
                    String examId = documentReference.getId();
                    documentReference.update("id", examId)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Test created successfully!",
                                        Toast.LENGTH_SHORT).show();
                                clearForm();
                                getParentFragmentManager().popBackStack();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(),
                                    "Failed to update exam ID: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Failed to save test: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
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