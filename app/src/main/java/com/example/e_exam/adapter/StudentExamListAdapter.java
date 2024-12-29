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
    private final List<StudentExamList> examList = new ArrayList<>();  // Changed to private final
    private OnExamClickListener listener;

    public interface OnExamClickListener {
        void onExamClick(StudentExamList exam);
    }

    public void setOnExamClickListener(OnExamClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_student_exam, parent, false);
        return new ViewHolder(view, listener, examList);
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

    // Add method to clear the list if needed
    public void clearExams() {
        examList.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView classNameTextView;
        private final TextView nameTextView;
        private final TextView dueTextView;
        private final Button examButton;
        private final List<StudentExamList> examList;
        private final OnExamClickListener listener;

        public ViewHolder(View itemView, OnExamClickListener listener, List<StudentExamList> examList) {
            super(itemView);
            this.listener = listener;
            this.examList = examList;

            classNameTextView = itemView.findViewById(R.id.classNameTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dueTextView = itemView.findViewById(R.id.dueTextView);
            examButton = itemView.findViewById(R.id.examButton);

            examButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onExamClick(examList.get(position));
                }
            });
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