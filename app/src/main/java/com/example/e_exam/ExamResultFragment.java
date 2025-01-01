package com.example.e_exam;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_exam.adapter.ExamListResultAdapter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class ExamResultFragment extends Fragment {
    private View mView;
    private TextView tvTopic;
    private ListView lvResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_exam_result, container, false);

        initUI();
        processExamResults();

        return mView;
    }

    private void initUI() {
        tvTopic = mView.findViewById(R.id.tv_topic);
        lvResult = mView.findViewById(R.id.lv_result);
    }

    private void processExamResults() {
        Bundle args = getArguments();
        Log.d("ExamResult", "Processing exam results in fragment");

        if (args != null) {
            Map<String, Object> answersMap = (Map<String, Object>) args.getSerializable("resultData");
            Log.d("ExamResult", "Retrieved answers map from bundle: " + (answersMap != null));

            if (answersMap != null) {
                ArrayList<Map<String, Object>> questionsList = new ArrayList<>();
                TreeMap<String, Map<String, Object>> sortedQuestions = new TreeMap<>();

                for (Map.Entry<String, Object> entry : answersMap.entrySet()) {
                    if (entry.getKey().startsWith("question_")) {
                        Map<String, Object> questionData = (Map<String, Object>) entry.getValue();
                        sortedQuestions.put(entry.getKey(), questionData);
                        Log.d("ExamResult", "Processing question: " + entry.getKey() +
                                ", Data: " + questionData);
                    }
                }

                questionsList.addAll(sortedQuestions.values());
                Log.d("ExamResult", "Total questions processed: " + questionsList.size());

                // Tính điểm
                int correctAnswers = 0;
                for (Map<String, Object> question : questionsList) {
                    String selected = (String) question.get("selected");
                    String correct = (String) question.get("correct");
                    if (selected != null && selected.equals(correct)) {
                        correctAnswers++;
                    }
                }
                Log.d("ExamResult", "Correct answers: " + correctAnswers +
                        " out of " + questionsList.size());

                String examName = args.getString("examName", "Kết quả bài thi");
                tvTopic.setText(String.format("%s - Điểm: %d/%d",
                        examName, correctAnswers, questionsList.size()));

                ExamListResultAdapter adapter = new ExamListResultAdapter(getContext(), questionsList);
                lvResult.setAdapter(adapter);
                Log.d("ExamResult", "Set up adapter with questions");
            } else {
                Log.e("ExamResult", "Answers map is null in fragment");
            }
        } else {
            Log.e("ExamResult", "No arguments passed to fragment");
        }
    }
}