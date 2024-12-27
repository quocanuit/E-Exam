package com.example.e_exam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_exam.adapter.StudentExamListAdapter;
import com.example.e_exam.model.StudentExamList;

import java.util.ArrayList;
import java.util.List;

public class StudentExamFragment extends Fragment implements StudentExamListAdapter.OnExamClickListener {
    private RecyclerView recyclerView;
    private StudentExamListAdapter adapter;
    private List<StudentExamList> examList;
    private int currentDisplayedItems = 0;
    private static final int ITEMS_PER_PAGE = 20;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_exam, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StudentExamListAdapter();
        adapter.setOnExamClickListener(this);
        recyclerView.setAdapter(adapter);

        setupScrollListener();
        loadMockData();
        loadMoreItems();

        return view;
    }

    @Override
    public void onExamClick(StudentExamList exam) {
        String fileUri = exam.getFileUri(); // Lấy URI file từ item (cần thêm field này trong model StudentExamList)

        ExamDetailFragment detailFragment = ExamDetailFragment.newInstance(
                exam.getClassName(),
                exam.getName(),
                exam.getDueDate(),
                fileUri
        );

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, detailFragment)
                .addToBackStack(null)
                .commit();
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

        examList.add(new StudentExamList("NT531.P11", "Nộp bài thực hành số 6", "pending", 1729173586L, "1"));
        examList.add(new StudentExamList("NT531.P11", "Nộp báo cáo đồ án", "completed", 1729173526L, "2"));
        examList.add(new StudentExamList("NT131.P12", "Nộp trễ tất cả bài thực hành", "pending", 1729173466L, "3"));
        examList.add(new StudentExamList("NT531.P11", "Nộp bài thực hành số 5", "outdated", 1729173406L, "4"));
        examList.add(new StudentExamList("NT531.P11", "Nộp bài thực hành số 4", "outdated", 1729173346L, "5"));
        examList.add(new StudentExamList("NT101.P13", "Bài tập 6", "pending", 1729173286L, "6"));
        examList.add(new StudentExamList("NT118", "Đồ án", "pending", 1729173226L, "7"));
        examList.add(new StudentExamList("NT118", "Bài tập", "completed", 1729173166L, "8"));
        examList.add(new StudentExamList("NT118", "Lý thuyết", "completed", 1729173106L, "9"));
        examList.add(new StudentExamList("NT118.88", "Đồ án p2", "pending", 1729173046L, "10"));
        examList.add(new StudentExamList("NT118.69", "Bài tập", "outdated", 1729172986L, "11"));
    }

    private void loadMoreItems() {
        if (examList != null && currentDisplayedItems < examList.size()) {
            int endIndex = Math.min(currentDisplayedItems + ITEMS_PER_PAGE, examList.size());
            List<StudentExamList> newItems = examList.subList(currentDisplayedItems, endIndex);
            adapter.addExams(newItems);
            currentDisplayedItems = endIndex;
        }
    }
}