package com.example.e_exam.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_exam.R;
import com.example.e_exam.model.StudentExamList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StudentExamListAdapter extends RecyclerView.Adapter<StudentExamListAdapter.ViewHolder> {
    private final List<StudentExamList> examList = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_student_exam, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentExamList exam = examList.get(position);
        holder.bind(exam);
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    public void addExams(List<StudentExamList> newExams) {
        int startPosition = examList.size();
        examList.addAll(newExams);
        notifyItemRangeInserted(startPosition, newExams.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView classNameTextView;
        private final TextView nameTextView;
        private final TextView dueTextView;
        private final Button examButton;

        public ViewHolder(View itemView) {
            super(itemView);
            classNameTextView = itemView.findViewById(R.id.classNameTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dueTextView = itemView.findViewById(R.id.dueTextView);
            examButton = itemView.findViewById(R.id.examButton);
        }

        void bind(StudentExamList exam) {
            classNameTextView.setText(exam.getClassName());
            nameTextView.setText(exam.getName());
            dueTextView.setText(exam.getFormattedDate());

            if (Objects.equals(exam.getStatus(), "pending")) {
                examButton.setBackgroundColor(itemView.getContext().getColor(R.color.primary));
                examButton.setText(itemView.getContext().getString(R.string.is_pending));
                examButton.setEnabled(true);
            } else if (Objects.equals(exam.getStatus(), "completed")) {
                examButton.setBackgroundColor(itemView.getContext().getColor(R.color.pastel_green));
                examButton.setText(itemView.getContext().getString(R.string.is_done));
                examButton.setEnabled(false);
            } else {
                examButton.setBackgroundColor(itemView.getContext().getColor(R.color.red));
                examButton.setText(itemView.getContext().getString(R.string.is_outdated));
                examButton.setEnabled(false);
            }
        }
    }
}
