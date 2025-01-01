package com.example.e_exam;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_exam.adapter.ExamListResultAdapter;
import com.example.e_exam.model.Answer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExamResultFragment extends Fragment {
    private static final String TAG = "ExamResultFragment";
    private View mView;
    private TextView tv_Topic, tv_Score;
    private ListView lv_Result;
    private ExamListResultAdapter adapter;

    public ExamResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_exam_result, container, false);
        initUI();

        Bundle args = getArguments();
        if (args != null) {
            String examName = args.getString("examName", "Exam Results");
            ArrayList<Answer> answerList = (ArrayList<Answer>) args.getSerializable("answerList");
            int score = args.getInt("score", 0);

            if (answerList != null) {
                displayResults(examName, score, answerList);
            } else {
                showAlertDialog("Lỗi", "Không thể tải kết quả bài thi");
            }
        }

        return mView;
    }

    private void initUI() {
        tv_Topic = mView.findViewById(R.id.tv_topic);
        tv_Score = mView.findViewById(R.id.tv_score);
        lv_Result = mView.findViewById(R.id.lv_result);
    }

    private void displayResults(String examName, int score, ArrayList<Answer> answers) {
        // Set exam name
        tv_Topic.setText(examName);

        // Set score
        String scoreText = String.format("Điểm: %d/%d", score, 10);

        tv_Score.setText(scoreText);

        // Set up RecyclerView
        adapter = new ExamListResultAdapter(getContext() , answers);
        lv_Result.setAdapter(adapter);

        // Save results to Firebase (optional)
        saveResultsToFirebase(examName, score, answers);
    }

    private void saveResultsToFirebase(String examName, int score, ArrayList<Answer> answers) {
        // Get current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        // Create result data
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("examName", examName);
        resultData.put("score", score);
        resultData.put("totalQuestions", answers.size());
        resultData.put("submittedAt", System.currentTimeMillis());
        resultData.put("answers", convertAnswersToMap(answers));

        // Save to Realtime Database
        FirebaseDatabase.getInstance()
                .getReference("user_results")
                .child(currentUser.getUid())
                .child(examName.replaceAll("\\s+", "_").toLowerCase())
                .setValue(resultData)
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error saving results to Firebase: " + e.getMessage())
                );
    }

    private Map<String, Object> convertAnswersToMap(ArrayList<Answer> answers) {
        Map<String, Object> answersMap = new HashMap<>();
        for (Answer answer : answers) {
            Map<String, Object> answerData = new HashMap<>();
            answerData.put("selected", answer.getSelectedAnswer());
            answerData.put("correct", answer.getCorrectAnswer());
            answersMap.put("question_" + answer.getId(), answerData);
        }
        return answersMap;
    }

    private void showAlertDialog(String title, String message) {
        if (getContext() == null) return;

        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}