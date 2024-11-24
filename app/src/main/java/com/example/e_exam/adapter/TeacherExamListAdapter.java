package com.example.e_exam.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_exam.R;
import com.example.e_exam.model.TeacherExamList;

import java.util.ArrayList;
import java.util.List;

public class TeacherExamListAdapter extends RecyclerView.Adapter<TeacherExamListAdapter.ViewHolder> {
    private final List<TeacherExamList> examList = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_teacher_exam, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TeacherExamList exam = examList.get(position);
        holder.bind(exam);
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    public void addExams(List<TeacherExamList> newExams) {
        int startPosition = examList.size();
        examList.addAll(newExams);
        notifyItemRangeInserted(startPosition, newExams.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView dateTextView;
        private final TextView statusTextView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }

        void bind(TeacherExamList exam) {
            nameTextView.setText(exam.getName());
            dateTextView.setText(exam.getFormattedDate());
            statusTextView.setText(exam.isAssigned() ? "Đã giao" : "Chưa giao");
            statusTextView.setTextColor(exam.isAssigned() ?
                    itemView.getContext().getColor(R.color.green) :
                    itemView.getContext().getColor(R.color.on_surface));
        }
    }
}