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

import com.example.e_exam.adapter.StudentExamListAdapter;
import com.example.e_exam.model.Answer;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentExamFragment extends Fragment implements StudentExamListAdapter.OnExamClickListener {
    private RecyclerView recyclerView;
    private StudentExamListAdapter adapter;
    private List<StudentExamList> examList;
    private FirebaseFirestore db;
    private ListenerRegistration examListener;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_exam, container, false);

        initializeFirebase();
        initializeRecyclerView(view);

        return view;
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(currentUser.getUid());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String customUid = snapshot.child("uid").getValue(String.class);
                    // Sử dụng customUid để query Firestore
                    loadExams(customUid);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("StudentExamFragment", "Error getting custom UID", error.toException());
                }
            });
        }

    }

    private void initializeRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StudentExamListAdapter();
        adapter.setOnExamClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void loadExams(String customUid) {
        Log.d("StudentExamFragment", "Current user ID: " + customUid);

        // Kiểm tra các bài thi có trong mảng studentIds mà có studentId giống currentUserId
        Query examQuery = db.collection("exams")
                .whereArrayContains("studentIds", customUid);

        examListener = examQuery.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("StudentExamFragment", "Listen failed.", error);
                return;
            }

            if (value == null || value.isEmpty()) {
                Log.d("StudentExamFragment", "No exams found for user: " + customUid);
                return;
            }

            List<StudentExamList> newExamList = new ArrayList<>();
            for (DocumentSnapshot document : value.getDocuments()) {
                Log.d("StudentExamFragment", "Processing exam: " + document.getId());
                Log.d("StudentExamFragment", "Data: " + document.getData());

                try {
                    String id = document.getId();
                    String name = document.getString("name");
                    String className = document.getString("className");
                    String status = document.getString("status");
                    Long deadline = document.getLong("deadline");
                    String pdfUrl = document.getString("pdfUrl");
                    String answerUrl = document.getString("answerUrl");

                    StudentExamList exam = new StudentExamList(className, name, status, deadline, id);
                    exam.setPdfUrl(pdfUrl);
                    exam.setAnswerUrl(answerUrl);
                    newExamList.add(exam);

                } catch (Exception e) {
                    Log.e("StudentExamFragment", "Error processing exam " + document.getId(), e);
                }
            }

            examList = newExamList;
            adapter.clearExams();
            adapter.addExams(examList);
            updateExamList(value);
            Log.d("StudentExamFragment", "Updated adapter with " + examList.size() + " items");
        });
    }


    private void updateExamList(QuerySnapshot snapshots) {
        examList = new ArrayList<>();
        for (DocumentSnapshot doc : snapshots.getDocuments()) {
            String id = doc.getId();
            String name = doc.getString("name");
            String className = doc.getString("className");
            String status = doc.getString("status");
            Long deadline = doc.getLong("deadline");
            String pdfUrl = doc.getString("pdfUrl");
            String answerUrl = doc.getString("answerUrl");

            if (name != null && className != null) {
                StudentExamList exam = new StudentExamList(className, name, status, deadline, id);
                exam.setPdfUrl(pdfUrl);
                exam.setAnswerUrl(answerUrl);
                examList.add(exam);
            }
        }

        adapter.clearExams();
        if (examList != null && !examList.isEmpty()) {
            Log.d("StudentExamFragment", "Adding " + examList.size() + " exams to adapter");
            adapter.addExams(examList);
        } else {
            Log.d("StudentExamFragment", "No exams to display");
            Toast.makeText(getContext(), "Không có bài kiểm tra nào", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (examListener != null) {
            examListener.remove();
        }
    }

    @Override
    public void onExamClick(StudentExamList exam) {
        if ("completed".equals(exam.getStatus())) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                db.collection("examResults")
                        .document(exam.getId() + "_" + currentUser.getUid())
                        .get()
                        .addOnSuccessListener(document -> {
                            if (document.exists()) {
                                List<Map<String, Object>> resultMaps =
                                        (List<Map<String, Object>>) document.get("results");
                                String examName = document.getString("examName");

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
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(),
                                        "Error loading results: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show());
            }
        } else {
            ExamDetailFragment detailFragment = ExamDetailFragment.newInstance(
                    exam.getClassName(),
                    exam.getName(),
                    exam.getDueDate(),
                    exam.getPdfUrl(),
                    exam.getAnswerUrl(),
                    40
            );

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void showSavedResults(String examName, ArrayList<Answer> results) {
        ExamResultFragment resultFragment = new ExamResultFragment();
        Bundle args = new Bundle();
        args.putSerializable("results", results);
        args.putString("examName", examName);
        args.putBoolean("isViewOnly", true);  // Thêm flag để đánh dấu chế độ xem lại
        resultFragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, resultFragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadExamResult(String examId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        FirebaseFirestore.getInstance()
                .collection("examResults")
                .document(examId + "_" + currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> resultMaps =
                                (List<Map<String, Object>>) documentSnapshot.get("results");

                        // Chuyển đến ExamResultFragment với kết quả
                        ExamResultFragment resultFragment = new ExamResultFragment();
                        Bundle args = new Bundle();
                        args.putSerializable("results", new ArrayList<>(resultMaps));
                        args.putString("examName", documentSnapshot.getString("examName"));
                        resultFragment.setArguments(args);

                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, resultFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Error loading exam result: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }
}