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

import com.example.e_exam.adapter.ExamListResultAdapter;
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
    private ExamDetailFragment examDetailFragment;

    public ExamResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_exam_result, container, false);

        initUI();
        getData();

        return mView;
    }

    private void initUI() {
        tv_Topic = mView.findViewById(R.id.tv_topic);
        lv_Result = mView.findViewById(R.id.lv_result);
    }

    private void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Sheets");

        // Đọc dữ liệu từ Firebase
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Question> allQuestions = new ArrayList<>();

                for (DataSnapshot sheetSnapshot : dataSnapshot.getChildren()) {
                    Sheet sheet = sheetSnapshot.getValue(Sheet.class); // Ánh xạ dữ liệu thành đối tượng Sheet
                    if (sheet != null) {
                        for (Topic topic : sheet.getTopics()) {
                            allQuestions.addAll(topic.getQuestions());
                        }
                    }
                }

                updateListView(allQuestions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi khi đọc dữ liệu
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });
    }

    private void updateListView(List<Question> questions) {
        ExamListResultAdapter adapter = new ExamListResultAdapter(getContext(), questions);
        lv_Result.setAdapter(adapter);
    }
}