package com.example.e_exam.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.e_exam.model.Answer;
import com.example.e_exam.R;

import java.util.List;

public class ExamListResultAdapter extends ArrayAdapter<Answer> {
    private Context context;
    private List<Answer> questions;

    public ExamListResultAdapter(Context context, List<Answer> questions) {
        super(context, R.layout.list_result_exam, questions);
        this.context = context;
        this.questions = questions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.list_result_exam, parent, false);
        }

        TextView tv_question = convertView.findViewById(R.id.tv_question);
        TextView tv_selected = convertView.findViewById(R.id.tv_selected);
        TextView tv_correct_answer = convertView.findViewById(R.id.tv_correct_answer);

        Answer answer = questions.get(position);
        tv_question.setText("Question " + answer.getId());
        tv_selected.setText(answer.getSelectedAnswer() != null ?
                answer.getSelectedAnswer() : "No answer");
        tv_correct_answer.setText(answer.getCorrectAnswer());

        // Set color for selected answer
        if (answer.getSelectedAnswer() != null) {
            if (answer.getSelectedAnswer().equals(answer.getCorrectAnswer())) {
                tv_selected.setTextColor(Color.parseColor("#4CAF50")); // Green
            } else {
                tv_selected.setTextColor(Color.parseColor("#F44336")); // Red
            }
        } else {
            tv_selected.setTextColor(Color.parseColor("#F44336")); // Red for no answer
        }

        return convertView;
    }
}
