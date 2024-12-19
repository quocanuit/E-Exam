package com.example.e_exam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.e_exam.Question;
import com.example.e_exam.R;

import java.util.List;

public class ExamListResultAdapter extends ArrayAdapter<Question> {
    private Context context;
    private List<Question> questions;

    public ExamListResultAdapter(Context context, List<Question> questions) {
        super(context, R.layout.list_result_exam, questions);
        this.context = context;
        this.questions = questions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_result_exam, parent, false);
        }

        TextView tv_question = convertView.findViewById(R.id.tv_question);
        TextView tv_selected = convertView.findViewById(R.id.tv_selected);
        TextView tv_correct_answer = convertView.findViewById(R.id.tv_correct_answer);

        Question question = questions.get(position);
        tv_question.setText(question.getId());
        tv_selected.setText(question.getCorrectAnswer());
        tv_correct_answer.setText(question.getCorrectAnswer());

        return convertView;
    }
}
