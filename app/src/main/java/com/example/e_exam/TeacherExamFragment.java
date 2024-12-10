package com.example.e_exam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_exam.adapter.TeacherExamListAdapter;
import com.example.e_exam.model.TeacherExamList;

import java.util.ArrayList;
import java.util.List;

public class TeacherExamFragment extends Fragment {
    private RecyclerView recyclerView;
    private TeacherExamListAdapter adapter;
    private List<TeacherExamList> examList;
    private int currentDisplayedItems = 0;
    private static final int ITEMS_PER_PAGE = 20;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_exam, container, false);

        // Find the button
        Button createExamButton = view.findViewById(R.id.createExamButton);  // Button id from XML

        // Set an OnClickListener to handle button clicks
        createExamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform fragment transaction to navigate to ExamCreateFragment
                assert getFragmentManager() != null;
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new ExamCreateFragment());  // Replace with new fragment
                transaction.addToBackStack(null);  // Allow user to navigate back to TeacherExamFragment
                transaction.commit();
            }
        });

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TeacherExamListAdapter();
        recyclerView.setAdapter(adapter);

        // Initialize and set up scroll listener
        setupScrollListener();
        loadMockData();
        loadMoreItems();

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
                    loadMoreItems();
                }
            }
        });
    }

    private void loadMockData() {
        examList = new ArrayList<>();

        // Hardcoded mock data
        examList.add(new TeacherExamList("Nộp bài thực hành số 6", 1729006273L, false, "1"));
        examList.add(new TeacherExamList("Nộp báo cáo đồ án", 1729006213L, true, "2"));
        examList.add(new TeacherExamList("Nộp trễ tất cả bài thực hành", 1729006153L, false, "3"));
        examList.add(new TeacherExamList("Nộp bài thực hành số 5", 1729130914L, false, "4"));
        examList.add(new TeacherExamList("Nộp bài tập", 1729145627L, false, "5"));
        examList.add(new TeacherExamList("Bài thực hành 2", 1729145567L, true, "6"));
        examList.add(new TeacherExamList("Phát triển ứng dụng trên thiết bị di động", 1729145507L, true, "7"));
        examList.add(new TeacherExamList("Hệ thống nhúng mạng không dây", 1729145447L, false, "8"));
        examList.add(new TeacherExamList("Kiểm tra giữa kì", 1729145387L, false, "9"));
        examList.add(new TeacherExamList("An toàn mạng máy tính", 1729145360L, false, "10"));
        examList.add(new TeacherExamList("Đánh giá hiệu năng hệ thống mạng máy tính", 1729145300L, false, "11"));
        examList.add(new TeacherExamList("Bài tập về nhà", 1729145240L, true, "12"));
        examList.add(new TeacherExamList("Bài tập lớn", 1729145180L, false, "13"));
        examList.add(new TeacherExamList("Bài tập khi rảnh", 1729145120L, true, "14"));
        examList.add(new TeacherExamList("Bài tập khi bận", 1729150213L, false, "15"));
        examList.add(new TeacherExamList("Nhập môn lập trình", 1729150153L, true, "16"));
    }

    private void loadMoreItems() {
        if (examList != null && currentDisplayedItems < examList.size()) {
            int endIndex = Math.min(currentDisplayedItems + ITEMS_PER_PAGE, examList.size());
            List<TeacherExamList> newItems = examList.subList(currentDisplayedItems, endIndex);
            adapter.addExams(newItems);
            currentDisplayedItems = endIndex;
        }
    }
}

