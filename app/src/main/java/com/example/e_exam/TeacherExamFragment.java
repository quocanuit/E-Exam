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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_exam.adapter.TeacherExamListAdapter;
import com.example.e_exam.model.TeacherExamList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class TeacherExamFragment extends Fragment {
    private String teacherId;
    private RecyclerView recyclerView;
    private TeacherExamListAdapter adapter;
    private List<TeacherExamList> examList;
    private int currentDisplayedItems = 0;
    private static final int ITEMS_PER_PAGE = 20;

    public static TeacherExamFragment newInstance(String teacherId) {
        TeacherExamFragment fragment = new TeacherExamFragment();
        Bundle args = new Bundle();
        args.putString("teacherId", teacherId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_exam, container, false);

        if (getArguments() != null) {
            teacherId = getArguments().getString("teacherId");
        }

        // Find the button
        Button createExamButton = view.findViewById(R.id.createExamButton); // Button id from XML
        Button createTestButton = view.findViewById(R.id.createTestButton);

        // Set an OnClickListener to handle button clicks
        createExamButton.setOnClickListener(v -> {
            // Perform fragment transaction to navigate to ExamCreateFragment
            assert getFragmentManager() != null;
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, ExamCreateFragment.newInstance(teacherId));  // Replace with new fragment
            transaction.addToBackStack(null);  // Allow user to navigate back to TeacherExamFragment
            transaction.commit();
        });

        createTestButton.setOnClickListener(v -> {
            // Perform fragment transaction to navigate to TestCreateFragment
            assert getFragmentManager() != null;
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, TestCreateFragment.newInstance(teacherId));  // Replace with new fragment
            transaction.addToBackStack(null);  // Allow user to navigate back to TeacherExamFragment
            transaction.commit();
        });

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TeacherExamListAdapter();
        recyclerView.setAdapter(adapter);

        // Initialize and set up scroll listener
        setupScrollListener();

        // Load data from Firebase
        loadDataFromFirebase();

        return view;
    }

    private void setupScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount < examList.size()) {
                    loadDataFromFirebase();
                }
            }
        });
    }

    private void loadDataFromFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Exams");

        databaseRef.orderByChild("timestamp").limitToLast(ITEMS_PER_PAGE).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (examList == null) {
                    examList = new ArrayList<>();
                } else {
                    examList.clear();  // Clear the existing list
                }

                // Add new data to the list
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    TeacherExamList exam = snapshot.getValue(TeacherExamList.class);
                    if (exam != null) {
                        examList.add(exam); // Add each exam with classCode
                    }
                }

                // Notify adapter of data changes
                adapter.addExams(examList); // Notify adapter that new exams were added
            } else {
                Toast.makeText(getContext(), "Error loading data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Error loading data from Firebase", task.getException());
            }
        });
    }

}
