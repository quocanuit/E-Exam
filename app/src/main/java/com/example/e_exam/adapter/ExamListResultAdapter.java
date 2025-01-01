package com.example.e_exam.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.e_exam.R;

import java.util.ArrayList;
import java.util.Map;

public class ExamListResultAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Map<String, Object>> questions;

    public ExamListResultAdapter(Context context, ArrayList<Map<String, Object>> questions) {
        this.context = context;
        this.questions = questions;
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public Object getItem(int position) {
        return questions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_result_exam, parent, false);
            holder = new ViewHolder();
            holder.tvQuestion = convertView.findViewById(R.id.tv_question);
            holder.tvSelected = convertView.findViewById(R.id.tv_selected);
            holder.tvCorrectAnswer = convertView.findViewById(R.id.tv_correct_answer);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Map<String, Object> question = questions.get(position);

        String questionId = (String) question.get("questionId");
        String selected = (String) question.get("selected");
        String correct = (String) question.get("correct");

        holder.tvQuestion.setText("Question " + questionId);
        holder.tvSelected.setText(selected != null ? selected : "X");
        holder.tvCorrectAnswer.setText(correct);

        // Set color for correct answer
        holder.tvCorrectAnswer.setTextColor(Color.parseColor("#4CAF50")); // Green

        // Set color for selected answer
        if (selected != null) {
            if (selected.equals(correct)) {
                holder.tvSelected.setTextColor(Color.parseColor("#4CAF50")); // Green
            } else {
                holder.tvSelected.setTextColor(Color.parseColor("#F44336")); // Red
            }
        } else {
            holder.tvSelected.setTextColor(Color.parseColor("#F44336")); // Red for no answer
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvQuestion;
        TextView tvSelected;
        TextView tvCorrectAnswer;
    }
}