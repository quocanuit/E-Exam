package com.example.e_exam;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.e_exam.adapter.StudentExamListAdapter;
import com.example.e_exam.model.StudentExamList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestStudentFragment extends Fragment {
    private RecyclerView recyclerView;
    private StudentExamListAdapter adapter;
    private List<StudentExamList> examList;
    private FirebaseFirestore db;
    private ListenerRegistration examListener;
    private String className;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_student, container, false);

        initializeFirebase();
        initializeViews(view);
        getIntentData();

        return view;
    }

    private void getIntentData() {
        if (getArguments() != null) {
            className = getArguments().getString("CLASS_NAME");
            if (className != null && db != null) {  // Kiểm tra db đã được khởi tạo
                Log.d("TestStudentFragment", "Loading exams for class: " + className);
                loadExams(className);
            } else {
                Log.e("TestStudentFragment", "className or db is null");
            }
        }
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StudentExamListAdapter();
        adapter.setOnExamClickListener(this::onExamClick);
        recyclerView.setAdapter(adapter);

        // Thêm SwipeRefreshLayout để refresh danh sách
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this::refreshExamList);
    }

    private void initializeFirebase() {
        // Khởi tạo Firestore trước
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("classes")
                    .child(currentUser.getUid());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String classNameFromDb = snapshot.child("className").getValue(String.class);
                    // Chỉ load nếu chưa có className từ arguments
                    if (className == null) {
                        className = classNameFromDb;
                        loadExams(className);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("TestStudentFragment", "Error getting custom UID", error.toException());
                }
            });
        }
    }


    private void loadExams(String className) {
        // Kiểm tra null
        if (db == null) {
            Log.e("TestStudentFragment", "Firestore instance is null");
            return;
        }

        if (className == null || className.isEmpty()) {
            showError("Không có thông tin lớp học");
            return;
        }

        Query examQuery = db.collection("exams")
                .whereEqualTo("className", className)
                .orderBy("deadline", Query.Direction.DESCENDING);

        examListener = examQuery.addSnapshotListener((value, error) -> {
            swipeRefreshLayout.setRefreshing(false);

            if (error != null) {
                handleQueryError(error);
                return;
            }

            if (value != null && !value.isEmpty()) {
                processExamDocuments(value.getDocuments());
            } else {
                showEmptyMessage();
            }
        });
    }

    private void processExamDocuments(List<DocumentSnapshot> documents) {
        List<StudentExamList> newExamList = new ArrayList<>();

        for (DocumentSnapshot document : documents) {
            try {
                // Lấy dữ liệu từ document theo cấu trúc của bạn
                StudentExamList exam = new StudentExamList(
                        document.getString("className"),
                        document.getString("name"),
                        document.getString("status"),
                        document.getLong("deadline"),
                        document.getId()
                );

                exam.setPdfUrl(document.getString("pdfUrl"));
                exam.setAnswerUrl(document.getString("answerUrl"));
                //exam.setTeacherId(document.getString("teacherId"));

                // Kiểm tra deadline
                Long deadline = document.getLong("deadline");
                if (deadline != null) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime > deadline) {
                        exam.setStatus("expired");
                    }
                }

                newExamList.add(exam);
            } catch (Exception e) {
                Log.e("StudentExamFragment", "Error processing exam " + document.getId(), e);
            }
        }

        updateUI(newExamList);
    }

    private void updateUI(List<StudentExamList> newExamList) {
        examList = newExamList;
        adapter.clearExams();
        adapter.addExams(examList);

        if (examList.isEmpty()) {
            showEmptyMessage();
        }
    }

    private void handleQueryError(FirebaseFirestoreException error) {
        String errorMessage;
        if (error.getCode() == FirebaseFirestoreException.Code.UNAVAILABLE) {
            errorMessage = "Không có kết nối mạng";
        } else if (error.getCode() == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
            errorMessage = "Bạn không có quyền truy cập";
        } else {
            errorMessage = "Lỗi: " + error.getMessage();
        }
        showError(errorMessage);
    }

    private void refreshExamList() {
        if (className != null) {
            loadExams(className);
        } else {
            showError("Không có thông tin lớp học");
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showEmptyMessage() {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Không có bài kiểm tra nào", Toast.LENGTH_SHORT).show();
        }
    }

    public void onExamClick(StudentExamList exam) {
        if ("completed".equals(exam.getStatus())) {
            loadExamResult(exam);
        } else if ("expired".equals(exam.getStatus())) {
            showError("Bài kiểm tra đã hết hạn");
        } else {
            openExamDetail(exam);
        }
    }

    private void loadExamResult(StudentExamList exam) {
        db.collection("examResults")
                .document(exam.getId() + "_" + className)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        List<Map<String, Object>> resultMaps = (List<Map<String, Object>>) document.get("results");
                        String examName = document.getString("examName");

                        openResultFragment(resultMaps, examName);
                    }
                })
                .addOnFailureListener(e -> showError("Lỗi tải kết quả: " + e.getMessage()));
    }

    private void openResultFragment(List<Map<String, Object>> resultMaps, String examName) {
        ExamResultFragment resultFragment = new ExamResultFragment();
        Bundle args = new Bundle();
        args.putSerializable("results", new ArrayList<>(resultMaps));
        args.putString("examName", examName);
        args.putBoolean("isViewOnly", true);
        resultFragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, resultFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openExamDetail(StudentExamList exam) {
        ExamDetailFragment detailFragment = ExamDetailFragment.newInstance(
                exam.getId(),
                exam.getClassName(),
                exam.getName(),
                exam.getDueDate(),
                exam.getPdfUrl(),
                exam.getAnswerUrl()
        );

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (examListener != null) {
            examListener.remove();
        }
    }
}