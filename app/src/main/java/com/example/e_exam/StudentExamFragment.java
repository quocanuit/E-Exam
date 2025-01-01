package com.example.e_exam;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class StudentExamFragment extends Fragment implements StudentExamListAdapter.OnExamClickListener {
    private View mView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Button btnAllExam, btnDoneExam, btnPendingExam;
    private StudentExamListAdapter adapter;
    private List<StudentExamList> examList;
    private FirebaseFirestore db;
    private ListenerRegistration examListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_student_exam, container, false);

        initUI();
        initializeFirebase();
        onClickListener();

        return mView;
    }

    private void initUI(){
        btnAllExam = mView.findViewById(R.id.btnAllExam);
        btnDoneExam = mView.findViewById(R.id.btnDoneExam);
        btnPendingExam = mView.findViewById(R.id.btnPendingExam);
        initializeRecyclerView(mView);

        btnAllExam.setSelected(true);
        btnDoneExam.setSelected(false);
        btnPendingExam.setSelected(false);
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
                    String studentUid = snapshot.child("uid").getValue(String.class);
                    // Sử dụng studentUid để query FireStore
                    loadExams(studentUid);
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
        swipeRefreshLayout = mView.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this::refreshExamList);
    }

    private void refreshExamList() {
        if (examListener != null) {
            examListener.remove();
        }
        if (examList != null) {
            adapter.clearExams();
            adapter.addExams(examList);
        }
    }

    private void onClickListener(){
        btnAllExam.setOnClickListener(v -> {
            updateButtonStates(btnAllExam);
            adapter.showAllExams();
        });

        btnDoneExam.setOnClickListener(v -> {
            updateButtonStates(btnDoneExam);
            adapter.filterExams(true); // true for completed exams
        });

        btnPendingExam.setOnClickListener(v -> {
            updateButtonStates(btnPendingExam);
            adapter.filterExams(false); // false for pending exams
        });
    }

    private void updateButtonStates(Button selectedButton) {
        btnAllExam.setSelected(false);
        btnDoneExam.setSelected(false);
        btnPendingExam.setSelected(false);
        selectedButton.setSelected(true);
    }

    public void loadExams(String studentUid) {
        Query examQuery = db.collection("exams")
                .whereArrayContains("studentIds", studentUid)
                .orderBy("deadline", Query.Direction.ASCENDING)
                .orderBy("className", Query.Direction.ASCENDING);

        examListener = examQuery.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("StudentExamFragment", "Listen failed.", error);
                return;
            }

            List<StudentExamList> newExamList = new ArrayList<>();
            for (DocumentSnapshot document : value.getDocuments()) {
                try {
                    String id = document.getId();
                    String name = document.getString("name");
                    String className = document.getString("className");
                    String status = document.getString("status");
                    Long deadline = document.getLong("deadline");
                    String pdfUrl = document.getString("pdfUrl");
                    String answerUrl = document.getString("answerUrl");

                    if (deadline != null) {
                        StudentExamList exam = new StudentExamList(className, name, status, deadline, id);
                        exam.setPdfUrl(pdfUrl);
                        exam.setAnswerUrl(answerUrl);

                        checkExamCompletion(exam);

                        newExamList.add(exam);
                    }
                } catch (Exception e) {
                    Log.e("StudentExamFragment", "Error processing exam " + document.getId(), e);
                }
            }

            examList = newExamList;
            adapter.clearExams();
            adapter.addExams(examList);
        });
    }

    private void checkExamCompletion(StudentExamList exam) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            db.collection("examResults")
                    .document(exam.getId())
                    .collection("submissions")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            exam.setStatus("completed");
                            adapter.notifyDataSetChanged();
                        }
                    });
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
                        .document(exam.getId())
                        .collection("submissions")
                        .document(currentUser.getUid())
                        .get()
                        .addOnSuccessListener(document -> {
                            if (document.exists()) {
                                List<Map<String, Object>> resultMaps = null;

                                // Kiểm tra nếu "results" tồn tại và không null
                                if (document.get("answers") instanceof List) {
                                    resultMaps = (List<Map<String, Object>>) document.get("answers");
                                }

                                String examName = document.getString("examName");

                                if (resultMaps != null && !resultMaps.isEmpty()) {
                                    ExamResultFragment resultFragment = new ExamResultFragment();
                                    Bundle args = new Bundle();
                                    args.putSerializable("answers", new ArrayList<>(resultMaps));
                                    args.putString("examName", examName);
                                    args.putBoolean("isViewOnly", true);
                                    resultFragment.setArguments(args);

                                    requireActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.frame_layout, resultFragment)
                                            .addToBackStack(null)
                                            .commit();
                                } else {
                                    Toast.makeText(
                                            getContext(),
                                            "Không có kết quả để hiển thị.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            } else {
                                Toast.makeText(
                                        getContext(),
                                        "Không tìm thấy tài liệu kết quả.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(
                                        getContext(),
                                        "Lỗi khi tải kết quả: " + e.getMessage(),
                                        Toast.LENGTH_SHORT
                                ).show());
            }
        } else {
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
    }

}