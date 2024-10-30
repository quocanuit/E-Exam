package com.example.e_exam;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class StudentScoreFragment extends Fragment {

    private RecyclerView recyclerView;
    private StudentScoreAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_score, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_scores);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<ScoreItem> scores = getSampleScores();
        adapter = new StudentScoreAdapter(scores, score -> {
            Intent intent = new Intent(getActivity(), StudentGradeActivity.class);
            intent.putExtra("subjectName", score.getSubjectName());
            startActivity(intent);
        }, getContext()); // Thêm đối số `getContext()` để cung cấp `Context`
        recyclerView.setAdapter(adapter);

        return view;
    }

    // Tạo danh sách điểm số mẫu
    private List<ScoreItem> getSampleScores() {
        List<ScoreItem> scores = new ArrayList<>();
        scores.add(new ScoreItem("Điểm Môn A"));
        scores.add(new ScoreItem("Điểm Môn B"));
        scores.add(new ScoreItem("Điểm Môn C"));
        return scores;
    }
}
