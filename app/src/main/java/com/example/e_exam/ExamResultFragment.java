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

import com.example.e_exam.adapter.ExamListResultAdapter;
import com.example.e_exam.model.Answer;
import com.example.e_exam.model.Sheet;
import com.example.e_exam.model.Topic;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExamResultFragment extends Fragment {

    private View mView;
    private TextView tv_Topic;
    private ListView lv_Result;
    private ArrayList<Answer> results;
    private ExamDetailFragment examDetailFragment;

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
            results = (ArrayList<Answer>) args.getSerializable("results");

            if (results == null) {
                results = new ArrayList<>(); // Nếu "results" là null, khởi tạo nó là một danh sách trống
            }
            String examName = args.getString("examName", "Exam Results");
            tv_Topic.setText(examName);

            // Calculate score
            int correct = 0;
            for (Answer answer : results) {
                if (answer.getSelectedAnswer() != null &&
                        answer.getSelectedAnswer().equals(answer.getCorrectAnswer())) {
                    correct++;
                }
            }
            tv_Topic.setText(String.format("%s - Score: %d/%d",
                    examName, correct, results.size()));
        }

        ExamListResultAdapter adapter = new ExamListResultAdapter(getContext(), results);
        lv_Result.setAdapter(adapter);

        return mView;
    }

    private void initUI() {
        tv_Topic = mView.findViewById(R.id.tv_topic);
        lv_Result = mView.findViewById(R.id.lv_result);
    }

    private void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Sheets");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Answer> allQuestions = new ArrayList<>();
                try {
                    for (DataSnapshot sheetSnapshot : dataSnapshot.getChildren()) {
                        Sheet sheet = sheetSnapshot.getValue(Sheet.class);
                        if (sheet != null && sheet.getTopics() != null) {
                            for (Topic topic : sheet.getTopics()) {
                                if (topic != null && topic.getQuestions() != null) {
                                    allQuestions.addAll(topic.getQuestions());
                                }
                            }
                        }
                    }
                    if (!allQuestions.isEmpty()) {
                        updateListView(allQuestions);
                    } else {
                        showAlertDialog("Thông báo", "Không có dữ liệu câu hỏi");
                    }
                } catch (Exception e) {
                    Log.e("Firebase", "Error processing data: " + e.getMessage());
                    showAlertDialog("Lỗi", "Có lỗi xảy ra khi tải dữ liệu");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error: " + error.getMessage());
                showAlertDialog("Lỗi", "Không thể kết nối đến cơ sở dữ liệu");
            }
        });
    }

    private void updateListView(List<Answer> questions) {
        if (getContext() != null) {
            ExamListResultAdapter adapter = new ExamListResultAdapter(getContext(), questions);
            lv_Result.setAdapter(adapter);
        }
    }

    private void showAlertDialog(String title, String message) {
        if (getContext() == null) return;

        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(false)
                .create()
                .show();
    }
}